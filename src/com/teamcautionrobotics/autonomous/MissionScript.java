package com.teamcautionrobotics.autonomous;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

public class MissionScript {

    public static Mission parseMission(String missionName, List<String> code,
            CommandFactory factory) throws ParseException {
        Mission mission = new Mission(missionName);
        int lineNumber = 1;
        for (String line : code) {
            String lineWithoutComment = line.split("//")[0];

            String[] parenSplit = lineWithoutComment.split("\\(");
            if (parenSplit.length == 2) {
                String name = parenSplit[0].trim();
                String signature = name + "(";
                String[] parametersTogether = parenSplit[1].split("\\)");

                Class<?>[] parameterClasses;
                Object[] parameterValues;

                if (parametersTogether.length > 0) {
                    String[] parameters = parametersTogether[0].split(",");
                    int paramIndex = 0;
                    parameterClasses = new Class[parameters.length];
                    parameterValues = new Object[parameters.length];
                    for (String parameter : parameters) {
                        if (parameter.equals("true")) {
                            parameterClasses[paramIndex] = boolean.class;
                            parameterValues[paramIndex] = true;
                        } else if (parameter.equals("false")) {
                            parameterClasses[paramIndex] = boolean.class;
                            parameterValues[paramIndex] = false;
                        } else {
                            try {
                                parameterClasses[paramIndex] = double.class;
                                parameterValues[paramIndex] = Double.parseDouble(parameter);
                            } catch (NumberFormatException e) {
                                String exceptionMessage = String.format(
                                        "Failed to parse parameter as boolean or double on line %d: \"%s\"%n",
                                        lineNumber, parameter);
                                throw new ParseException(exceptionMessage + e.getMessage(),
                                        lineNumber);
                            }
                        }
                        paramIndex++;
                    }
                    for (int p = 0; p < parameterClasses.length; p++) {
                        signature += parameterClasses[p].getName();
                        if (p != parameterClasses.length - 1) {
                            signature += ", ";
                        }
                    }
                } else {
                    parameterClasses = new Class[0];
                    parameterValues = new Object[0];
                }
                signature += ")";
                try {
                    Method method = CommandFactory.class.getMethod(name, parameterClasses);

                    Object returnValue = method.invoke(factory, parameterValues);

                    if (!(returnValue instanceof Command)) {
                        String exceptionMessage =
                                String.format("Method did not return a Command: %s returned: %s",
                                        signature, returnValue.getClass().getName());
                        throw new ParseException(exceptionMessage, lineNumber);
                    }

                    mission.add((Command) returnValue);

                } catch (NoSuchMethodException e) {
                    String exceptionMessage =
                            String.format("Failed to find method: %s%nFull signature: %s",
                                    signature, e.getMessage());
                    throw new ParseException(exceptionMessage, lineNumber);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (parenSplit.length == 1) {
                System.err.format(
                        "Failed to find opening parenthesis in line %d, continuing: \"%s\"%n",
                        lineNumber, lineWithoutComment);
                continue;
            } else {
                String exceptionMessage =
                        String.format("Too many opening parentheses in line %d: \"%s\"", lineNumber,
                                lineWithoutComment);
                // Determining the position of the parenthesis is difficult, so we just use the line
                // number
                throw new ParseException(exceptionMessage, lineNumber);
            }
            lineNumber++;
        }
        return mission;
    }

}
