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

    Intake intake;
    Lift lift;

    boolean liftRaiseButtonPressed = false;
    boolean liftLowerButtonPressed = false;
    boolean liftPIDManualModeEnabled = false;

    // Based on eyeball averaging max lift speed
    static final double LIFT_NUDGE_SPEED = 30; // Units are inches per second
    static final double MAX_LIFT_HEIGHT_FOR_INTAKE = 2.2; // Inches lift height

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

    private FunctionRunnerSendable liftEncoderResetSendable;

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

        intake = new Intake(3, 4, 5, 8);
        lift = new Lift(2, 4, 5, 6, 7, 0.8, 0.1, 0.4);

        liftEncoderResetSendable = new FunctionRunnerSendable("Reset lift encoder", () -> {
            DriverStation.reportWarning(String.format(
                    "Resetting lift encoder from SmartDashboard. Encoder was at %f inches.%n",
                    lift.getCurrentHeight()), false);
            lift.resetEncoder();
        });
        SmartDashboard.putData(liftEncoderResetSendable);

        commandFactory = new CommandFactory2018(driveBase, intake, lift);

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


        SmartDashboard.putData("lift PID", lift.pidController);

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

        double speedLimit = speedLimitFromLiftHeight(lift.getCurrentHeight());
        leftPower *= speedLimit;
        rightPower *= speedLimit;

        driveBase.drive(leftPower, rightPower);

        boolean driverPrismHarvesting = false;
        double grabberPower = 0;
        double intakePower = 0;
        if (lift.getCurrentHeight() < MAX_LIFT_HEIGHT_FOR_INTAKE) {
            if (driverRight.getTrigger()) {
                driverPrismHarvesting = true;
            } else {
                intakePower = grabberPower = manipulator.getAxis(Axis.LEFT_Y);
            }

            // Spin controls only permitted if lift is down and driver is not harvesting
            if (!driverPrismHarvesting) {
                // Left bumper spins counterclockwise
                if (manipulator.getButton(Button.LEFT_BUMPER)) {
                    intake.timedSpin(-0.5, 0.1);
                }

                // Right bumper spins clockwise
                if (manipulator.getButton(Button.RIGHT_BUMPER)) {
                    intake.timedSpin(0.5, 0.1);
                }
            }
        } else {
            // disable intake when lift is up
            intakePower = 0;
            grabberPower = manipulator.getAxis(Axis.LEFT_Y);
        }

        if (driverPrismHarvesting) {
            grabberPower = 0.5;
            intakePower = 0.5;
        } else {
            if (intakePower == 0) {
                int dPadAngle = manipulator.getPOV();
                switch (dPadAngle) {
                    // D-pad is up
                    case 0:
                        grabberPower = -1;
                        break;

                    // D-pad is left
                    case 270:
                        grabberPower = -0.5;
                        break;

                    // D-pad is down
                    case 180:
                        grabberPower = -0.1;
                        break;
                }
            }
        }

        intake.move(grabberPower, intakePower);

        // Only allow bulldozing if the driver is not commanding a prism harvest and
        // the intake is not being moved
        if (!driverPrismHarvesting && intakePower == 0
                && manipulator.getAxis(Axis.LEFT_TRIGGER) > 0.5) {
            intake.bulldoze();
        }


        boolean liftRaiseButton = manipulator.getButton(Button.Y);
        if (liftRaiseButton != liftRaiseButtonPressed) {
            if (liftRaiseButton) {
                lift.setDestinationLevel(lift.getDestinationLiftLevel().next());
            }
            liftRaiseButtonPressed = liftRaiseButton;
        }

        boolean liftLowerButton = manipulator.getButton(Button.A);
        if (liftLowerButton != liftLowerButtonPressed) {
            if (liftLowerButton) {
                lift.setDestinationLevel(lift.getDestinationLiftLevel().previous());
            }
            liftLowerButtonPressed = liftLowerButton;
        }

        boolean liftPIDManualModeTrigger = manipulator.getAxis(Axis.RIGHT_TRIGGER) > 0.5;

        if (!liftPIDManualModeTrigger && liftPIDManualModeTrigger != liftPIDManualModeEnabled) {
            lift.setDestinationHeight(lift.getCurrentHeight());
        }

        liftPIDManualModeEnabled = liftPIDManualModeTrigger;

        if (liftPIDManualModeEnabled) {
            lift.disablePID();
        } else {
            lift.enablePID();
        }

        if (liftPIDManualModeEnabled) {
            // Right manipulator joystick down for lift up
            lift.move(manipulator.getAxis(Axis.RIGHT_Y));
        } else {
            // Use the manipulator right joystick Y to adjust the PID controller's setpoint
            double dt = this.getPeriod();
            double liftNudgeCommand = manipulator.getAxis(Axis.RIGHT_Y);
            double changeInHeight = LIFT_NUDGE_SPEED * liftNudgeCommand * dt; // inches
            if (liftNudgeCommand != 0) {
                lift.setDestinationHeight(lift.getCurrentHeight() + changeInHeight);
            }
        }
    }

    @Override
    public void robotPeriodic() {
        liftEncoderResetSendable.update();
        putEncoders();
        putSensors();
    }


    double speedLimitFromLiftHeight(double height) {
        // Chosen by linear fit through (30 in, 1) and (70 in, 0.5)
        double limit = height * -0.0125 + 1.375;

        // Keep speed limit in range of [0.4, 1]
        limit = Math.max(Math.min(limit, 1), 0.4);

        return limit;
    }

    void putSensors() {
        SmartDashboard.putBoolean("Stage one down", lift.stageOneIsDown());
        SmartDashboard.putBoolean("Stage two down", lift.stageTwoIsDown());
        SmartDashboard.putBoolean("Lift fully down",
                lift.stageOneIsDown() && lift.stageTwoIsDown());

        SmartDashboard.putBoolean("Power prismⁿ™ in grabber", intake.cubeIsInGrabber());
    }

    void putEncoders() {
        SmartDashboard.putNumber("drive left distance", driveBase.getLeftDistance());
        SmartDashboard.putNumber("drive right distance", driveBase.getRightDistance());

        SmartDashboard.putNumber("drive left speed", driveBase.getLeftSpeed());
        SmartDashboard.putNumber("drive right speed", driveBase.getRightSpeed());

        SmartDashboard.putString("desired lift level", lift.getDestinationLiftLevel().toString());
        SmartDashboard.putString("lift level", lift.getCurrentLiftLevel().toString());
        SmartDashboard.putNumber("lift distance", lift.getCurrentHeight());
        SmartDashboard.putNumber("lift speed", lift.liftEncoder.getRate());
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
