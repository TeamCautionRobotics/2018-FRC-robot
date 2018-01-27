/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.robot2018.Gamepad.Axis;
import com.teamcautionrobotics.robot2018.Gamepad.Button;

import edu.wpi.first.wpilibj.TimedRobot;

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

    DriveBase driveBase;

    EnhancedJoystick driverLeft;
    EnhancedJoystick driverRight;
    Gamepad manipulator;

    Intake intake;
    Climb climb;

    @Override
    public void robotInit() {
        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        intake = new Intake(2, 3);
        climb = new Climb(4);
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
    public void autonomousInit() {}

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {}

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        driveBase.drive(-driverLeft.getY(), -driverRight.getY());

        intake.run(manipulator.getAxis(Axis.LEFT_Y));

        if (manipulator.getButton(Button.X)) {
            climb.ascend();
        }

        double spinSpeed = 0;

        if (manipulator.getAxis(Axis.LEFT_TRIGGER) > 0) {
            spinSpeed = -manipulator.getAxis(Axis.LEFT_TRIGGER);
        }
        if (manipulator.getAxis(Axis.RIGHT_TRIGGER) > 0) {
            spinSpeed = manipulator.getAxis(Axis.RIGHT_TRIGGER);
        }
        intake.spin(spinSpeed);
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {}
}
