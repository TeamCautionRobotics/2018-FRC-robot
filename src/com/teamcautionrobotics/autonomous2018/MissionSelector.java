package com.teamcautionrobotics.autonomous2018;

import java.util.ArrayList;
import java.util.Arrays;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.Mission;
import com.teamcautionrobotics.autonomous2018.AutoEnums.AutoObjective;
import com.teamcautionrobotics.autonomous2018.AutoEnums.PlateSide;
import com.teamcautionrobotics.autonomous2018.AutoEnums.StartingPosition;
import com.teamcautionrobotics.autonomous2018.commands.CommandFactory2018;
import com.teamcautionrobotics.robot2018.Harvester.HarvesterAngle;
import com.teamcautionrobotics.robot2018.Elevator.ElevatorLevel;

public class MissionSelector {

    public final Mission driveForwardMission, centerMissionRightSwitch, centerMissionLeftSwitch,
            centerMissionRightScale, centerMissionLeftScale, rightMissionRightSwitch,
            rightMissionRightScale, leftMissionLeftSwitch, leftMissionLeftScale;
    public static final Mission DO_NOTHING_MISSION = new Mission("do nothing mission");

    private final CommandFactory2018 commandFactory;

    private final double EXPECTED_ENCODER_TEST_DISTANCE = 6.0;

    public MissionSelector(CommandFactory2018 commandFactory) {
        this.commandFactory = commandFactory;

        driveForwardMission = new Mission("drive forward mission",
                commandFactory.setIntakeMotor(0.08),
                commandFactory.moveStraightDistance(0.5, 100, true),
                commandFactory.moveStraight(-0.1, 0.2, false)
                );

        centerMissionRightSwitch = new Mission("center mission right switch",
                    commandFactory.setIntakeMotor(0.08),
                    commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                    commandFactory.delay(0.5),
                    commandFactory.moveStraightDistance(0.5, 20, true),
                    commandFactory.turnInPlace(0.8, 40),
                    commandFactory.setElevator(ElevatorLevel.SWITCH),
                    commandFactory.moveStraightDistance(0.5, 37, true),
                    commandFactory.turnInPlace(-0.8, 40),
                    commandFactory.moveStraight(0.8, 0.6, false),
                    commandFactory.setDriveMotors(0.2),
                    commandFactory.delay(1.0),
                    commandFactory.deployCube()
                );

        centerMissionLeftSwitch = new Mission("center mission left switch",
                    commandFactory.setIntakeMotor(0.08),
                    commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                    commandFactory.delay(0.5), 
                    commandFactory.moveStraightDistance(0.5, 20, true),
                    commandFactory.turnInPlace(-0.8, 50),
                    commandFactory.setElevator(ElevatorLevel.SWITCH),
                    commandFactory.moveStraightDistance(0.5, 80, true),
                    commandFactory.turnInPlace(0.8, 42),
                    commandFactory.moveStraight(0.8, 0.6, false),
                    commandFactory.setDriveMotors(0.2),
                    commandFactory.delay(1.0),
                    commandFactory.deployCube()
                );

        centerMissionRightScale = new Mission("center mission right scale",
                commandFactory.setIntakeMotor(0.08),
                commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                commandFactory.delay(0.5),
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 50),
                commandFactory.moveStraightDistance(0.5, 195, true),
                commandFactory.setElevator(ElevatorLevel.HIGH_SCALE),
                commandFactory.turnInPlace(0.3, 90),
                commandFactory.moveStraight(0.5, 0.3, false),
                commandFactory.deployCube()
        );

        centerMissionLeftScale = new Mission("center mission left scale",
                commandFactory.setIntakeMotor(0.08),
                commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                commandFactory.delay(0.5),
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 185, true),
                commandFactory.setElevator(ElevatorLevel.HIGH_SCALE),
                commandFactory.turnInPlace(-0.3, 85),
                commandFactory.moveStraight(0.5, 0.3, false),
                commandFactory.deployCube()
        );

        rightMissionRightSwitch = new Mission("right mission switch",
                commandFactory.setIntakeMotor(0.08),
                commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                commandFactory.delay(0.5),
                commandFactory.moveStraightDistance(0.5, 145, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                commandFactory.turnInPlace(0.5, -80),
                commandFactory.setElevator(ElevatorLevel.SWITCH, true),
                commandFactory.moveStraight(0.6, 0.5, false),
                commandFactory.setDriveMotors(0.2),
                commandFactory.deployCube()
                );

        rightMissionRightScale = new Mission("right mission scale",
                    commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                    commandFactory.delay(0.5), 
                    commandFactory.moveStraightDistance(0.5, 294, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.setElevator(ElevatorLevel.HIGH_SCALE),
                    commandFactory.turnInPlace(0.5, -80),
                    commandFactory.moveStraight(0.5, 0.3, false),
                    commandFactory.deployCube()
                );
        leftMissionLeftSwitch = new Mission("left mission switch",
                    commandFactory.setIntakeMotor(0.08),
                    commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                    commandFactory.delay(0.5),
                    commandFactory.moveStraightDistance(0.5, 145, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.turnInPlace(0.5, 80),
                    commandFactory.setElevator(ElevatorLevel.SWITCH, true),
                    commandFactory.moveStraight(0.6, 0.5, false),
                    commandFactory.setDriveMotors(0.2),
                    commandFactory.deployCube()
                );

        leftMissionLeftScale = new Mission("left mission scale",
                    commandFactory.setIntakeMotor(0.08),
                    commandFactory.rotateHarvester(HarvesterAngle.AIMED),
                    commandFactory.delay(0.5), 
                    commandFactory.moveStraightDistance(0.5, 294, true),
                    commandFactory.moveStraight(-0.1, 0.2, false),
                    commandFactory.setElevator(ElevatorLevel.HIGH_SCALE),
                    commandFactory.turnInPlace(0.5, 80),
                    commandFactory.moveStraight(0.8, 0.5, false),
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
