package com.planner.schedule;

import com.planner.schedule.day.Day;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.util.EventLog;
import com.planner.util.Time;

import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The class {@code CompactScheduler} implements the interface {@link Scheduler} for compact scheduling actions.
 * This involves the utilization of Longest-Job-First in order to compute the schedule for the week. {@link UserConfig}
 * options allow for another style such as Shortest-Job-First well as other restraints for scheduling.
 * <p>
 * Schedule corrections occur when overflows happen and are properly handled in accordance with the algorithm type. In {@code CompactScheduler},
 * this involves maintaining that distributive mindset when reallocating the tasks across the given week.
 *
 * @author Andrew Roe
 * @since 0.3.0
 */
public class CompactScheduler implements Scheduler {

    /** Singleton for CompactScheduler */
    private static CompactScheduler singleton;
    /** Holds relevant data for user settings in scheduling */
    private final UserConfig userConfig;
    /** EventLog for logging data on Day actions */
    private final EventLog eventLog;

    /**
     * Constructs a new {@code CompactScheduler} with a given {@link UserConfig} and {@link EventLog}
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     */
    private CompactScheduler(UserConfig userConfig, EventLog eventLog) {
        this.userConfig = userConfig;
        this.eventLog = eventLog;

    }

    /**
     * Retrieves a singleton of {@code CompactScheduler} for scheduling purposes
     *
     * @param userConfig user settings for scheduling purposes
     * @param eventLog EventLog for logging data on Day actions
     * @return instance of {@code CompactScheduler}
     */
    protected static CompactScheduler getSingleton(UserConfig userConfig, EventLog eventLog) {
        if(singleton == null) {
            singleton = new CompactScheduler(userConfig, eventLog);
        }
        return singleton;
    }

    @Override
    public int assignDay(Day day, int errorCount, PriorityQueue<Task> complete, PriorityQueue<Task> taskManager, Calendar date) {
        PriorityQueue<Task> incomplete = new PriorityQueue<>();
        int numErrors = errorCount;
        while ((!taskManager.isEmpty() && day.hasSpareHours()) ||
                (!taskManager.isEmpty() && taskManager.peek().getDueDate().equals(day.getDate()))) {
            // gets first task from heap and finds max possible hours available
            Task task = taskManager.remove();
            double maxHours = getMaxHours(day, task, date);
            // status of task creation
            boolean validTaskStatus = day.addSubTaskManually(task, maxHours);
            // adds task to relevant completion heap
            if (task.getDueDate().equals(day.getDate()) || task.getSubTotalHoursRemaining() == 0) complete.add(task);
            else incomplete.add(task);
            // reports scheduling action
            eventLog.reportDayAction(day, task, validTaskStatus);
            // updates number of errors
            numErrors += validTaskStatus ? 0 : 1;

            if (!validTaskStatus && Time.differenceOfDays(task.getDueDate(), day.getDate()) > 0) break;
        }
        while (!incomplete.isEmpty()) {
            taskManager.add(incomplete.remove());
        }
        return numErrors;
    }

    private double getMaxHours(Day day, Task task, Calendar date) {
        // sets up the starting hour for the day based on the given time from 'date'
        int startingHour = date.get(Calendar.HOUR_OF_DAY);
        boolean today = false;
        if (date.get(Calendar.DATE) == day.getDate().get(Calendar.DATE)
                && date.get(Calendar.MONTH) == day.getDate().get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == day.getDate().get(Calendar.YEAR)) {
            startingHour = Math.max(userConfig.getRange()[0], startingHour);
            today = true;
        } else {
            startingHour = userConfig.getRange()[0];
        }

        double maxHours = 0.0;
        if (task.getDueDate().equals(day.getDate())) {
            if (userConfig.isFitDay()) {
                double remainingHours = 24.0 - (startingHour + day.getHoursFilled());
                maxHours = Math.min(remainingHours, task.getSubTotalHoursRemaining());
                // this chops off 30 minutes at the end of the day when it's past midnight
                if (remainingHours - maxHours < 1.0 && date.get(Calendar.MINUTE) >= 30) {
                    maxHours -= 0.5;
                }
//                if (startingHour + maxHours >= 24 && date.get(Calendar.MINUTE) >= 30) {
//                    maxHours -= 0.5;
//                }
            } else {
                maxHours = task.getSubTotalHoursRemaining();
            }
        } else if (today) { // we need to deal with Scenarios C & D [DONE]
            double remainingHours = userConfig.getRange()[1] - (startingHour + day.getHoursFilled());
            if (remainingHours > 0 && day.getSpareHours() > 0) {
                maxHours = Math.min(remainingHours, task.getSubTotalHoursRemaining());
                maxHours = Math.min(day.getSpareHours(), maxHours);
            }
        } else {
            maxHours = Math.min(day.getSpareHours(), task.getSubTotalHoursRemaining());
//            if (maxHours < userConfig.getMinHours() && task.getSubTotalHoursRemaining() > maxHours) maxHours = 0.0;
        }
        // this only works with compact scheduler since it clumps tasks together when assigning them
        // logic: if our 'maxHours' for the task is less than 'minHours' from config
        //          AND there are more possible hours you could have utilized, then we disregard it until a later time
        if (maxHours < userConfig.getMinHours() && task.getSubTotalHoursRemaining() > maxHours) maxHours = 0.0;
        return maxHours;
    }

    @Override
    public boolean correctSchedule(List<Day> schedule, int errorCount) {
        return false;
    }
}
