package com.teamcautionrobotics.autonomous2018;

import java.util.ArrayList;
import java.util.Arrays;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.Mission;
import com.teamcautionrobotics.autonomous2018.AutoEnums.AutoObjective;
import com.teamcautionrobotics.autonomous2018.AutoEnums.PlateSide;
import com.teamcautionrobotics.autonomous2018.AutoEnums.StartingPosition;
import com.teamcautionrobotics.autonomous2018.commands.CommandFactory2018;
import com.teamcautionrobotics.robot2018.Lift.LiftLevel;

public class MissionSelector {

    public final Mission driveForwardMission, centerMissionRightSwitch, centerMissionLeftSwitch,
            centerMissionRightScale, centerMissionLeftScale, rightMissionRightSwitch,
            rightMissionRightScale, leftMissionLeftSwitch, leftMissionLeftScale;
    public static final Mission DO_NOTHING_MISSION = new Mission("do nothing mission");

    private final CommandFactory2018 commandFactory;

    private final double EXPECTED_ENCODER_TEST_DISTANCE = 6.0;

    public MissionSelector(CommandFactory2018 commandFactory) {
        this.commandFactory = commandFactory;

        driveForwardMission = makeMissionWithPrefix("drive forward mission",
                        commandFactory.moveStraightDistance(0.5, 100, true),
                        commandFactory.moveStraight(-0.1, 0.2, false)
                );

        centerMissionRightSwitch = makeMissionWithPrefix("center mission right switch",
                    commandFactory.moveStraightDistance(0.5, 20, true),
                    commandFactory.turnInPlace(0.3, 40),
                    commandFactory.setLift(LiftLevel.SWITCH),
                    commandFactory.moveStraightDistance(0.5, 55, true),
                    commandFactory.turnInPlace(-0.3, 30),
                    commandFactory.moveStraight(0.5, 0.2, false),
                    commandFactory.delay(0.5),
                    commandFactory.deployCube()
                );

        centerMissionLeftSwitch = makeMissionWithPrefix("center mission left switch",
                    commandFactory.moveStraightDistance(0.5, 20, true),
                    commandFactory.turnInPlace(-0.3, 50),
                    commandFactory.setLift(LiftLevel.SWITCH),
                    commandFactory.moveStraightDistance(0.5, 80, true),
                    commandFactory.turnInPlace(0.3, 42),
                    commandFactory.moveStraight(0.7, 0.2, false),
                    commandFactory.delay(0.5),
                    commandFactory.deployCube()
                );

        centerMissionRightScale = makeMissionWithPrefix("center mission right scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 50),
                commandFactory.moveStraightDistance(0.5, 195, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.3, 90), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        centerMissionLeftScale = makeMissionWithPrefix("center mission left scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 185, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(-0.3, 85), commandFactory.moveStraight(0.5, 0.3, false)
        // DEPLOY THE CUBE!!!!!!!
        );

        rightMissionRightSwitch = makeMissionWithPrefix("right mission switch",
                commandFactory.delay(0.5),
                commandFactory.moveStraightDistance(0.5, 145, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                commandFactory.turnInPlace(0.5, -80),
                commandFactory.moveStraight(0.5, 0.35, false),
                commandFactory.setLift(LiftLevel.SWITCH, true),
                commandFactory.deployCube()
                );

        rightMissionRightScale = makeMissionWithPrefix("right mission scale",
                    commandFactory.moveStraightDistance(0.5, 294, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.setLift(LiftLevel.HIGH_SCALE),
                    commandFactory.turnInPlace(0.5, -80),
                    commandFactory.moveStraight(0.5, 0.3, false),
                    commandFactory.deployCube()
                );
        leftMissionLeftSwitch = makeMissionWithPrefix("left mission switch",
                    commandFactory.delay(0.5),
                    commandFactory.moveStraightDistance(0.5, 145, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.turnInPlace(0.5, 80),
                    commandFactory.moveStraight(0.5, 0.35, false),
                    commandFactory.setLift(LiftLevel.SWITCH, true),
                    commandFactory.deployCube()
                );

        leftMissionLeftScale = makeMissionWithPrefix("left mission scale",
                    commandFactory.moveStraightDistance(0.5, 294, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.setLift(LiftLevel.HIGH_SCALE),
                    commandFactory.turnInPlace(0.5, 80),
                    commandFactory.moveStraight(0.5, 0.3, false),
                    commandFactory.deployCube()
                );
    }

    private Mission makeMissionWithPrefix(String name, Command... commands) {
        ArrayList<Command> autoCommands = new ArrayList<>();

        // Commands we want to run at the beginning of all of the autonomous missions
        autoCommands.addAll(Arrays.asList(commandFactory.resetEncoders(),
                commandFactory.moveStraight(0.3, 0.5, false),
                commandFactory.checkDriveEncoders(EXPECTED_ENCODER_TEST_DISTANCE),
                commandFactory.moveStraight(-0.5, 0.4, false)
            ));

        autoCommands.addAll(Arrays.asList(commands));
        return new Mission(name, autoCommands);
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
                    return centerMissionRightSwitch;
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
