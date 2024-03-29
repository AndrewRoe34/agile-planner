package com.agile.planner.scripter.functional;

import com.agile.planner.models.Card;
import com.agile.planner.models.Label;
import com.agile.planner.models.Task;
import com.agile.planner.scripter.exception.InvalidGrammarException;
import com.agile.planner.models.CheckList;

import java.util.List;

/**
 * Function --> print: [class] <br>
 * Provides output based on whether it is by class (i.e. all class items) or by
 * variable (which can be dynamic or specified)
 *
 * @author Andrew Roe
 */
public class PrintState extends State {

    @Override
    protected void processFunc(String line) {
        String s = verifyString(line);
        if(s != null) {
            System.out.println(s);
            //scriptLog.reportPrintFunc(false, s);
            return;
        }
        String[] tokens = processArguments(line, 2, null);
        switch (tokens[0]) {
            case "task":
                System.out.println(taskList);
                break;
            case "_task":
                if (tokens[1] == null) {
                    List<Task> tasks = FunctionState.getTasks();
                    if (tasks == null) {
                        System.out.println(taskList.get(taskList.size() - 1));
                    } else if (tasks.size() == 1) {
                        System.out.println(tasks.get(0));
                    } else {
                        System.out.println(tasks);
                    }
                } else {
                    System.out.println(taskList.get(Integer.parseInt(tokens[1])));
                }
                break;
            case "checklist":
                System.out.println(clList);
                break;
            case "_checklist":
                if (tokens[1] == null) {
                    List<CheckList> checkLists = FunctionState.getCheckLists();
                    if (checkLists == null) {
                        System.out.println(clList.get(clList.size() - 1));
                    } else if (checkLists.size() == 1) {
                        System.out.println(checkLists.get(0));
                    } else {
                        System.out.println(checkLists);
                    }
                } else {
                    System.out.println(clList.get(Integer.parseInt(tokens[1])));
                }
                break;
            case "card":
                System.out.println(cardList);
                break;
            case "_card":
                if (tokens[1] == null) {
                    List<Card> cards = FunctionState.getCards();
                    if(cards == null) {
                        System.out.println(cardList.get(cardList.size() - 1));
                    } else if(cards.size() == 1) {
                        System.out.println(cards.get(0));
                    } else {
                        System.out.println(cards);
                    }
                } else {
                    System.out.println(cardList.get(Integer.parseInt(tokens[1])));
                }
                break;
            case "board":
                // Handle board case (if needed)
                break;
            case "_board":
                if (tokens[1] == null) {
                    // Handle _board case (if needed)
                } else {
                    // Handle _board case with tokens[1] (if needed)
                }
                break;
            case "label":
                System.out.println(labelList);
                break;
            case "_label":
                if (tokens[1] == null) {
                    List<Label> labels = FunctionState.getLabels();
                    if(labels == null) {
                        System.out.println(labelList.get(labelList.size() - 1));
                    } else if(labels.size() == 1) {
                        System.out.println(labels.get(0));
                    } else {
                        System.out.println(labels);
                    }
                } else {
                    System.out.println(labelList.get(Integer.parseInt(tokens[1])));
                }
                break;
            default:
                throw new InvalidGrammarException("Invalid input. Expected [print: <class>]");
        }

        //scriptLog.reportPrintFunc(true, tokens[0] + (tokens[1] == null ? "" : " " + tokens[1]));
    }

    private String verifyString(String line) {
        boolean quoteBegin = false;
        boolean quoteEnd = false;
        int beginIdx = 0;
        int endIdx = 0;
        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == ' ' || line.charAt(i) == '\t') {
                beginIdx++;
            } else if(line.charAt(i) == 'p') break;
        }
        beginIdx += 6;
        for(int i = beginIdx; i < line.length(); i++) {
            if(!quoteBegin && line.charAt(i) == '"') {
                quoteBegin = true;
                beginIdx = i;
            } else if(!quoteEnd && line.charAt(i) == '"') {
                quoteEnd = true;
                endIdx = i;
            } else if(!quoteBegin && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                return null;
            } else if(quoteEnd && line.charAt(i) != ' ' && line.charAt(i) != '\t' &&
                    line.charAt(i) != '\n' && line.charAt(i) != '\r') {
                return null;
            }
        }
        if(beginIdx >= endIdx) return null;
        return line.substring(beginIdx + 1, endIdx);
    }
}
