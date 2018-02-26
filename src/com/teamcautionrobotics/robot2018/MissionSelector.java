package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.CommandFactory;
import com.teamcautionrobotics.autonomous.Mission;
import com.teamcautionrobotics.autonomous.MissionScriptMission;
import com.teamcautionrobotics.robot2018.AutoEnums.AutoObjective;
import com.teamcautionrobotics.robot2018.AutoEnums.PlateSide;
import com.teamcautionrobotics.robot2018.AutoEnums.StartingPosition;

public class MissionSelector {

    public final Mission driveForwardMission, centerMissionRightSwitch, centerMissionLeftSwitch,
            centerMissionRightScale, centerMissionLeftScale, rightMissionRightSwitch,
            rightMissionRightScale, leftMissionLeftSwitch, leftMissionLeftScale;
    public static final Mission DO_NOTHING_MISSION = new Mission("do nothing mission");


    public MissionSelector(CommandFactory commandFactory) {
        driveForwardMission = new Mission("drive forward mission",
                commandFactory.moveStraightDistance(0.5, 60, true),
                commandFactory.moveStraight(-0.1, 0.2, false));

        centerMissionRightSwitch = new Mission("center mission right switch",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 45),
                commandFactory.moveStraightDistance(0.5, 20, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.3, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        centerMissionLeftSwitch = new Mission("center mission left switch",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 30, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(-0.3, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        centerMissionRightScale = new Mission("center mission right scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 50),
                commandFactory.moveStraightDistance(0.5, 195, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.3, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        centerMissionLeftScale = new Mission("center mission left scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 185, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(-0.3, 85), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        rightMissionRightSwitch = new Mission("right mission switch",
                commandFactory.moveStraightDistance(0.5, 130, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, -90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        rightMissionRightScale = new Mission("right mission scale",
                commandFactory.moveStraightDistance(0.5, 260, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, -90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        leftMissionLeftSwitch = new Mission("left mission switch",
                commandFactory.moveStraightDistance(0.5, 130, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        leftMissionLeftScale = new Mission("left mission scale",
                commandFactory.moveStraightDistance(0.5, 260, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );
    }

    public Mission selectMissionFromFieldData(PlateSide switchSide, PlateSide scaleSide,
            StartingPosition startingPosition, AutoObjective autoObjective) {
        if (autoObjective == AutoObjective.AUTO_LINE) {
            return driveForwardMission;
        } else if (autoObjective == AutoObjective.SWITCH) {
            if (startingPosition == StartingPosition.CENTER) {
                if (switchSide == PlateSide.LEFT) {
                    return centerMissionLeftSwitch;
                } else if (switchSide == PlateSide.RIGHT) {
                    return rightMissionRightSwitch;
                }
            } else if (startingPosition == StartingPosition.LEFT) {
                if (switchSide == PlateSide.LEFT) {
                    return leftMissionLeftSwitch;
                } else if (switchSide == PlateSide.RIGHT) {
                    return driveForwardMission;
                }
            } else if (startingPosition == StartingPosition.RIGHT) {
                if (switchSide == PlateSide.LEFT) {
                    return driveForwardMission;
                } else if (switchSide == PlateSide.RIGHT) {
                    return rightMissionRightSwitch;
                }
            }
        } else if (autoObjective == AutoObjective.SCALE) {
            if (startingPosition == StartingPosition.CENTER) {
                if (scaleSide == PlateSide.LEFT) {
                    return centerMissionLeftScale;
                } else if (scaleSide == PlateSide.RIGHT) {
                    return centerMissionRightScale;
                }
            } else if (startingPosition == StartingPosition.LEFT) {
                if (scaleSide == PlateSide.LEFT) {
                    return leftMissionLeftScale;
                } else if (scaleSide == PlateSide.RIGHT) {
                    return driveForwardMission;
                }
            } else if (startingPosition == StartingPosition.RIGHT) {
                if (scaleSide == PlateSide.LEFT) {
                    return driveForwardMission;
                } else if (scaleSide == PlateSide.RIGHT) {
                    return rightMissionRightScale;
                }
            }
        } else if (autoObjective == AutoObjective.SWITCH_OR_SCALE) {
            if (startingPosition == StartingPosition.CENTER) {
                if (switchSide == PlateSide.LEFT) {
                    return centerMissionLeftSwitch;
                } else if (switchSide == PlateSide.RIGHT) {
                    return centerMissionRightSwitch;
                }
            } else if (startingPosition == StartingPosition.LEFT) {
                if (switchSide == PlateSide.LEFT) {
                    return leftMissionLeftSwitch;
                } else if (switchSide == PlateSide.RIGHT) {
                    if (scaleSide == PlateSide.LEFT) {
                        return leftMissionLeftScale;
                    } else if (scaleSide == PlateSide.RIGHT) {
                        return driveForwardMission;
                    }
                }
            } else if (startingPosition == StartingPosition.RIGHT) {
                if (switchSide == PlateSide.LEFT) {
                    if (scaleSide == PlateSide.LEFT) {
                        return driveForwardMission;
                    } else if (scaleSide == PlateSide.RIGHT) {
                        return rightMissionRightScale;
                    }
                } else if (switchSide == PlateSide.RIGHT) {
                    return rightMissionRightSwitch;
                }
            }
        } else if (autoObjective == AutoObjective.DO_NOTHING) {
            return DO_NOTHING_MISSION;
        }
        return new Mission("Mission selection error", new Command() {
            @Override
            public boolean run() {
                System.err.format(
                        "Unable to select mission. Switch side: %s, scale side: %s, "
                                + "starting position: %s, auto objective: %s%n",
                        switchSide.toString(), scaleSide.toString(), startingPosition.toString(),
                        autoObjective.toString());
                return true;
            }

            @Override
            public void reset() {}
        });
    }
}
