package com.planner.util;

import com.planner.models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planner.schedule.day.Day;

public class JsonHandler {

    public static String createUserConfig(UserConfig userConfig) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(userConfig);
    }

    public static UserConfig readUserConfig(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, UserConfig.class);
    }

    public static String createCardJson(Card card) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(card);
    }

    public static Card readCardJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Card.class);
    }

    public static String createTaskJson(Task task) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(task);
    }

    public static Task readTaskJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Task.class);
    }

    public static String createDayJson(Day day) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(day);
    }
}