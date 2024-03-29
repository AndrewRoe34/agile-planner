package com.agile.planner.scripter.functional;

import com.agile.planner.user.UserConfig;

public class PreProcessorState extends State {

    @Override
    protected void processFunc(String line) {
        switch(line) {
            case "__DEF_CONFIG__":
                int[] arr = {8, 8, 8, 8, 8, 8, 8};
                State.userConfig = new UserConfig("name", "email", arr, 14,
                        14, false, true, false,0, 0);
                break;
            case "__CURR_CONFIG__":
            case "__LOG__":
            case "__DEBUG__":
            case "__IMPORT__":
            case "__EXPORT__":
            default:
                break;
        }
    }
}
