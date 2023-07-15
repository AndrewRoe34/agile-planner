package agile.planner.scripter;

import agile.planner.data.Task;
import agile.planner.manager.ScheduleManager;
import agile.planner.scripter.exception.InvalidGrammarException;

/**
 * The class {@code TaskState} deals with the construction of {@link Task} instances throughout the
 * scripting file. It ensures that each {@link Task} receives an appropriate id and is synced
 * with the {@link ScheduleManager} class. {@link State} first determines the appropriate context
 * switch and then transfers over control to {@code TaskState} to perform its own responsibilities. <p>
 * Class --> task: [name:string], [hours:int], [days:int] <br>
 * Creates a new instance of a Task while utilizing dynamic variable usage
 *
 * @author Andrew Roe
 */
public class TaskState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 3, ",");
        try {
            int id = scheduleManager.getLastTaskId() + taskList.size();
            int hours = Integer.parseInt(tokens[1]);
            int days = Integer.parseInt(tokens[2]);
            taskList.add(new Task(id, tokens[0], hours, days));
            scriptLog.reportTaskCreation(id, tokens[0], hours, days);
            System.out.println("Task added.. [T" + (scheduleManager.getLastTaskId() + taskList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[task: <name: string>, <hours: int>, <num_days: int>]");
        }
    }
}
