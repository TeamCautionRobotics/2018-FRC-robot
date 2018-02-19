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
import com.teamcautionrobotics.autonomous.commands2018.CommandFactory2018;
import com.teamcautionrobotics.misc2018.EnhancedJoystick;
import com.teamcautionrobotics.misc2018.Gamepad;
import com.teamcautionrobotics.misc2018.Gamepad.Axis;
import com.teamcautionrobotics.misc2018.Gamepad.Button;

import edu.wpi.first.wpilibj.DigitalInput;
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
    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */

    static final Path missionScriptPath = Paths.get("/opt/mission.ms");

    DriveBase driveBase;

    EnhancedJoystick driverLeft;
    EnhancedJoystick driverRight;
    Gamepad manipulator;

    Intake intake;
    Lift lift;
    
    DigitalInput stageOneDown;
    DigitalInput stageTwoDown;

    boolean liftRaiseButtonPressed = false;
    boolean liftLowerButtonPressed = false;

    static final double LIFT_NUDGE_SPEED = 10; // Units are inches per second

    CommandFactory2018 commandFactory;
    MissionScriptMission missionScriptMission;
    MissionSendable missionSendable;
    SendableChooser<Mission> missionChooser;
    Mission activeMission;

    @Override
    public void robotInit() {
        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        intake = new Intake(3, 4, 5);
        lift = new Lift(2, 4, 5, 1, 1, 1);
        
        stageOneDown = new DigitalInput(6);
        stageTwoDown = new DigitalInput(7);

        commandFactory = new CommandFactory2018(driveBase);

        missionScriptMission = new MissionScriptMission("Mission Script Mission", missionScriptPath,
                commandFactory);

        missionChooser = new SendableChooser<>();

        missionChooser.addObject("Do not use -- Mission Script", missionScriptMission);
        missionChooser.addDefault("Do Nothing Mission", new Mission("Do Nothing Mission"));
        SmartDashboard.putData("Autonomous Mode Select", missionChooser);

        missionSendable = new MissionSendable("Teleop Mission", missionChooser::getSelected);
        SmartDashboard.putData(missionSendable);
    }

    @Override
    public void disabledPeriodic() {
        SmartDashboard.putString("selected mission", missionChooser.getSelected().getName());
    }
    /**
     * This autonomous (along with the chooser code above) shows how to select between different
     * autonomous modes using the dashboard. The sendable chooser code works with the Java
     * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
     * uncomment the getString line to get the auto name from the text box below the Gyro
     *
     * <p>
     * You can add additional auto modes by adding additional comparisons to the switch structure
     * below with additional strings. If using the SendableChooser make sure to add them to the
     * chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        activeMission = missionChooser.getSelected();

        if (activeMission != null) {
            activeMission.reset();
            System.out.println("Mission '" + activeMission.getName() + "' Started");
        }
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (activeMission != null) {
            if (activeMission.run()) {
                System.out.println("Mission '" + activeMission.getName() + "' Complete");
                activeMission = null;
            }
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        SmartDashboard.putString("selected mission", missionChooser.getSelected().getName());

        if ((missionSendable.run() && !missionChooser.getSelected().enableControls)
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


        // Left bumper spins counterclockwise
        if (manipulator.getButton(Button.LEFT_BUMPER)) {
            intake.timedSpin(-0.25, 0.1);
        }

        // Right bumper spins clockwise
        if (manipulator.getButton(Button.RIGHT_BUMPER)) {
            intake.timedSpin(0.25, 0.1);
        }

        intake.move(manipulator.getAxis(Axis.LEFT_Y));

        if (stageOneDown.get() && stageTwoDown.get()) {
            lift.resetEncoder();
        }
        
        boolean liftRaiseButton = manipulator.getButton(Button.Y);
        if (liftRaiseButton != liftRaiseButtonPressed) {
            if (liftRaiseButton) {
                lift.setLevel(lift.getCurrentLiftLevel().next());
            }
            liftRaiseButtonPressed = liftRaiseButton;
        }

        boolean liftLowerButton = manipulator.getButton(Button.Y);
        if (liftLowerButton != liftRaiseButtonPressed) {
            if (liftLowerButton) {
                lift.setLevel(lift.getCurrentLiftLevel().previous());
            }
            liftLowerButtonPressed = liftLowerButton;
        }

        // manual lift control
        double dt = this.getPeriod();
        double liftNudgeCommand = manipulator.getAxis(Axis.RIGHT_Y);
        double changeInHeight = LIFT_NUDGE_SPEED * liftNudgeCommand * dt; // inches
        lift.setHeight(lift.getCurrentHeight() + changeInHeight);
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {}

    double speedLimitFromLiftHeight(double height) {
        // Chosen by linear fit through (30 in, 1) and (70 in, 0.5)
        double limit = height * -0.0125 + 1.375;

        // Keep speed limit in range of [0.4, 1]
        limit = Math.max(Math.min(limit, 1), 0.4);

        return limit;
    }
}
