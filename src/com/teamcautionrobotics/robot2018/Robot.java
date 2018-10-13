/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package com.teamcautionrobotics.robot2018;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.teamcautionrobotics.autonomous.Mission;
import com.teamcautionrobotics.autonomous.MissionScriptMission;
import com.teamcautionrobotics.autonomous.MissionSendable;
import com.teamcautionrobotics.autonomous2018.AutoEnums.AutoMode;
import com.teamcautionrobotics.autonomous2018.AutoEnums.AutoObjective;
import com.teamcautionrobotics.autonomous2018.AutoEnums.PlateSide;
import com.teamcautionrobotics.autonomous2018.AutoEnums.StartingPosition;
import com.teamcautionrobotics.autonomous2018.MissionSelector;
import com.teamcautionrobotics.autonomous2018.commands.CommandFactory2018;
import com.teamcautionrobotics.misc2018.EnhancedJoystick;
import com.teamcautionrobotics.misc2018.FunctionRunnerSendable;
import com.teamcautionrobotics.misc2018.Gamepad;
import com.teamcautionrobotics.misc2018.Gamepad.Axis;
import com.teamcautionrobotics.misc2018.Gamepad.Button;
import com.teamcautionrobotics.robot2018.Harvester.HarvesterAngle;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {

    static final Path missionScriptPath = Paths.get("/opt/mission.ms");

    DriveBase driveBase;

    EnhancedJoystick driverLeft;
    EnhancedJoystick driverRight;
    Gamepad manipulator;

    Harvester harvester;
    Elevator elevator;

    boolean elevatorRaiseButtonPressed = false;
    boolean elevatorLowerButtonPressed = false;
    boolean elevatorPIDManualModeEnabled = false;

    // Based on eyeball averaging max elevator speed
    static final double ELEVATOR_NUDGE_SPEED = 30; // Units are inches per second

    CommandFactory2018 commandFactory;
    MissionScriptMission missionScriptMission;
    MissionSendable missionSendable;

    String fmsData;
    PlateSide switchPosition;
    PlateSide scalePosition;

    SendableChooser<AutoMode> autoModeChooser;
    SendableChooser<StartingPosition> startingPositionChooser;
    SendableChooser<AutoObjective> autoObjectiveChooser;
    MissionSelector missionSelector;
    Mission activeMission;

    private FunctionRunnerSendable elevatorEncoderResetSendable;
    private FunctionRunnerSendable harvesterEncoderResetSendable;
    private FunctionRunnerSendable angulatorPidResetSendable;

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        PowerDistributionPanel pdp = new PowerDistributionPanel();
        SmartDashboard.putData(pdp);

        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        /* TODO: Find out angulator port, encoder ports, and PID values
         * Double check other port values as well
         */
        harvester = new Harvester(3, 4, 8, 9, 0.01, 0.001, 0.03);
        elevator = new Elevator(2, 4, 5, 6, 7, 0.8, 0.1, 0.4);

        elevatorEncoderResetSendable = new FunctionRunnerSendable("Reset elevator encoder", () -> {
            DriverStation.reportWarning(String.format(
                    "Resetting elevator encoder from SmartDashboard. Encoder was at %f inches.%n",
                    elevator.getCurrentHeight()), false);
            elevator.resetEncoder();
        });
        SmartDashboard.putData(elevatorEncoderResetSendable);

        harvesterEncoderResetSendable = new FunctionRunnerSendable("Reset angulator encoder", () -> {
            DriverStation.reportWarning(String.format(
                    "Reset angulator encoder from SmartDashboard. Encoder was at %f degrees.%n",
                    harvester.getCurrentAngle()), false);
            harvester.resetEncoder();
        });
        SmartDashboard.putData(harvesterEncoderResetSendable);

        angulatorPidResetSendable = new FunctionRunnerSendable("Reset angulator PID", () -> {
            boolean pidWasEnabled = harvester.pidController.isEnabled();
            DriverStation.reportWarning(String.format(
                    "Reset angulator PID from SmartDashboard. PID controller enabled was %b.%n",
                    pidWasEnabled), false);
            harvester.pidController.reset();
            if (pidWasEnabled) {
                harvester.enablePID();
            }
        });
        SmartDashboard.putData(angulatorPidResetSendable);

        commandFactory = new CommandFactory2018(driveBase, harvester, elevator);

        missionScriptMission = new MissionScriptMission("Mission Script Mission", missionScriptPath,
                commandFactory);


        startingPositionChooser = new SendableChooser<>();
        addObjectsToChooser(startingPositionChooser, StartingPosition.CENTER,
                StartingPosition.values());
        SmartDashboard.putData("Starting Position Select", startingPositionChooser);

        autoObjectiveChooser = new SendableChooser<>();
        addObjectsToChooser(autoObjectiveChooser, AutoObjective.SWITCH, AutoObjective.values());
        SmartDashboard.putData("Auto Objective Select", autoObjectiveChooser);

        autoModeChooser = new SendableChooser<>();
        addObjectsToChooser(autoModeChooser, AutoMode.FMS_DATA, AutoMode.values());
        SmartDashboard.putData("Autonomous Mode Select", autoModeChooser);

        // TODO(Schuyler): fix the missionSendable
        missionSendable = new MissionSendable("Teleop Mission", () -> missionScriptMission);
        SmartDashboard.putData(missionSendable);


        SmartDashboard.putData("elevator PID", elevator.pidController);
        SmartDashboard.putData("Angulator PID", harvester.pidController);

        missionSelector = new MissionSelector(commandFactory);
    }

    @Override
    public void autonomousInit() {
        switch (autoModeChooser.getSelected()) {
            case FMS_DATA:
                fmsData = DriverStation.getInstance().getGameSpecificMessage();
                System.out.format("FMS Data for Plate Positions: '%s'%n", fmsData);
                if (fmsData.length() == 3) {
                    if (fmsData.charAt(0) == 'L') {
                        switchPosition = PlateSide.LEFT;
                    } else if (fmsData.charAt(0) == 'R') {
                        switchPosition = PlateSide.RIGHT;
                    } else {
                        System.err.println("FMS switch char is neither 'L' nor 'R'");
                    }
                    if (fmsData.charAt(1) == 'L') {
                        scalePosition = PlateSide.LEFT;
                    } else if (fmsData.charAt(1) == 'R') {
                        scalePosition = PlateSide.RIGHT;
                    } else {
                        System.err.println("FMS scale char is neither 'L' nor 'R'");
                    }
                } else {
                    System.err.println("FMS does not pass a three-char string for plate position.");
                }

                activeMission = missionSelector.selectMissionFromFieldData(switchPosition,
                        scalePosition, startingPositionChooser.getSelected(),
                        autoObjectiveChooser.getSelected());
                System.out.format("FMS auto selected mission is %s%n", activeMission.getName());
                break;

            case DO_NOTHING:
                activeMission = MissionSelector.DO_NOTHING_MISSION;
                break;

            case MISSION_SCRIPT:
                activeMission = missionScriptMission;
                break;
        }

        if (activeMission != null) {
            activeMission.reset();
            System.out.println("Mission '" + activeMission.getName() + "' Started");
        } else {
            DriverStation.reportWarning(
                    String.format("autonomous init: activeMission is null. Selected mode is %s",
                            autoModeChooser.getSelected()),
                    false);
        }
    }

    @Override
    public void autonomousPeriodic() {
        if (activeMission != null) {
            if (activeMission.run()) {
                System.out.println("Mission '" + activeMission.getName() + "' Complete");
                activeMission = null;
            }
        }
    }

    @Override
    public void teleopPeriodic() {
        // TODO(Schuyler): fix this
        // SmartDashboard.putString("selected mission", autoModeChooser.getSelected().getName());

        if ((missionSendable.run() /* && !autoModeChooser.getSelected().enableControls */)
                || driveBase.pidController.isEnabled()) {
            return;
        }

        double forwardCommand = -driverRight.getY();
        double turnCommand = driverLeft.getX();
        double leftPower = forwardCommand + turnCommand;
        double rightPower = forwardCommand - turnCommand;

        double speedLimit = speedLimitFromElevatorHeight(elevator.getCurrentHeight());
        leftPower *= speedLimit;
        rightPower *= speedLimit;

        driveBase.drive(leftPower, rightPower);

        boolean driverHarvesterControl = false;
        double grabberPower = 0;
        if (driverLeft.getTrigger() || driverRight.getTrigger()) {
            driverHarvesterControl = true;
        } else {
            grabberPower = 0.5 * manipulator.getAxis(Axis.LEFT_Y);
        }

        if (driverHarvesterControl) {
            if (driverLeft.getTrigger()) {
                grabberPower = -0.5;
            }
            if (driverRight.getTrigger()) {
                grabberPower = 0.5;
            }
        } else {
                int dPadAngle = manipulator.getPOV();
                switch (dPadAngle) {
                    // D-pad is up
                    case 0:
                        grabberPower = -0.5;
                        break;

                    // D-pad is left
                    case 270:
                        grabberPower = -0.375;
                        break;

                    // D-pad is down
                    case 180:
                        grabberPower = -0.25;
                        break;
                }
        }

        harvester.move(grabberPower != 0 ? grabberPower : 0.08);

        // When true, use the angulator motor to move the angultor up, then reset the encoder.
        boolean angulatorEncoderRealign = manipulator.getAxis(Axis.LEFT_TRIGGER) > 0.5;

        if (!angulatorEncoderRealign && (driverRight.getRawButton(3) || driverRight.getRawButton(2))) {
            if (elevator.getCurrentHeight() <= 2.0) {
                harvester.disablePID();
            } else {
                harvester.enablePID();
                harvester.setDestinationAngle(HarvesterAngle.DOWN);
            }
        } else {
            harvester.enablePID();
            harvester.setDestinationAngle(HarvesterAngle.AIMED);
        }

        if (angulatorEncoderRealign) {
            harvester.disablePID();
            harvester.angulator.set(0.3);
            harvester.resetEncoder();
        }

        boolean elevatorRaiseButton = manipulator.getButton(Button.Y);
        if (elevatorRaiseButton != elevatorRaiseButtonPressed) {
            if (elevatorRaiseButton) {
                elevator.setDestinationLevel(elevator.getDestinationElevatorLevel().next());
            }
            elevatorRaiseButtonPressed = elevatorRaiseButton;
        }

        boolean elevatorLowerButton = manipulator.getButton(Button.A);
        if (elevatorLowerButton != elevatorLowerButtonPressed) {
            if (elevatorLowerButton) {
                elevator.setDestinationLevel(elevator.getDestinationElevatorLevel().previous());
            }
            elevatorLowerButtonPressed = elevatorLowerButton;
        }

        // true when the elevator should operate in manual mode (PID disabled)
        boolean elevatorPIDManualModeTrigger = manipulator.getAxis(Axis.RIGHT_TRIGGER) > 0.5;

        if (!elevatorPIDManualModeTrigger && elevatorPIDManualModeTrigger != elevatorPIDManualModeEnabled) {
            elevator.setDestinationHeight(elevator.getCurrentHeight());
        }

        elevatorPIDManualModeEnabled = elevatorPIDManualModeTrigger;

        if (elevatorPIDManualModeEnabled) {
            elevator.disablePID();
        } else {
            elevator.enablePID();
        }

        // Joystick down moves the elevator up
        double elevatorMoveCommand = manipulator.getAxis(Axis.RIGHT_Y);

        SmartDashboard.putBoolean("Elevator manual mode enabled", elevatorPIDManualModeEnabled);
        SmartDashboard.putNumber("Elevator Move Command (manip)", elevatorMoveCommand);

        if (elevatorPIDManualModeEnabled) {
            elevator.move(elevatorMoveCommand);
        } else {
            // Use the manipulator right joystick Y to adjust the PID controller's setpoint
            double dt = this.getPeriod();
            double changeInHeight = ELEVATOR_NUDGE_SPEED * elevatorMoveCommand * dt; // inches
            if (elevatorMoveCommand != 0) {
                elevator.setDestinationHeight(elevator.getCurrentHeight() + changeInHeight);
            }
        }
    }

    @Override
    public void disabledInit() {
        // Disable the harvester PID controller when the robot is disabled to prevent integral
        // windup. This also resets the PID controller, clearing the integral term.
        harvester.disablePID();
    }

    @Override
    public void robotPeriodic() {
        elevatorEncoderResetSendable.update();
        harvesterEncoderResetSendable.update();
        putEncoders();
        putSensors();
    }


    double speedLimitFromElevatorHeight(double height) {
        // Chosen by linear fit through (30 in, 1) and (70 in, 0.5)
        double limit = height * -0.0125 + 1.375;

        // Keep speed limit in range of [0.4, 1]
        limit = Math.max(Math.min(limit, 1), 0.4);

        return limit;
    }

    void putSensors() {
        SmartDashboard.putBoolean("Stage one down", elevator.stageOneIsDown());
        SmartDashboard.putBoolean("Stage two down", elevator.stageTwoIsDown());
        SmartDashboard.putBoolean("Elevator fully down",
                elevator.stageOneIsDown() && elevator.stageTwoIsDown());
    }

    void putEncoders() {
        SmartDashboard.putNumber("drive left distance", driveBase.getLeftDistance());
        SmartDashboard.putNumber("drive right distance", driveBase.getRightDistance());

        SmartDashboard.putNumber("drive left speed", driveBase.getLeftSpeed());
        SmartDashboard.putNumber("drive right speed", driveBase.getRightSpeed());

        SmartDashboard.putString("desired elevator level", elevator.getDestinationElevatorLevel().toString());
        SmartDashboard.putString("elevator level", elevator.getCurrentElevatorLevel().toString());
        SmartDashboard.putNumber("elevator distance", elevator.getCurrentHeight());
        SmartDashboard.putNumber("elevator speed", elevator.elevatorEncoder.getRate());

        SmartDashboard.putNumber("Angulator angle", harvester.getCurrentAngle());
        SmartDashboard.putNumber("Angulator setpoint", harvester.getDestinationAngle());
        SmartDashboard.putString("Angulator level",
                Harvester.convertAngleToHarvesterAngle(harvester.getCurrentAngle()).toString());
    }


    static <V> void addObjectsToChooser(SendableChooser<V> chooser, V defaultChoice, V objects[]) {
        chooser.addDefault(defaultChoice.toString(), defaultChoice);
        for (V choice : objects) {
            if (choice == defaultChoice) {
                continue;
            }
            chooser.addObject(choice.toString(), choice);
        }
    }
}
