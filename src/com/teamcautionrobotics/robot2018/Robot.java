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

import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
    DriveBase driveBase;

    EnhancedJoystick driverLeft;
    EnhancedJoystick driverRight;
    Gamepad manipulator;

    Intake intake;
    Climb climb;
    
    Timer timer;
    
    double spinProportion = 0;
    double spinRatio = 1;
    boolean spinFinished = true;
    
    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        //motor ports??
        intake = new Intake(3, 4, 5);
        climb = new Climb(2);
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
        
        if (manipulator.getButton(Button.LEFT_BUMPER)) {
            spinProportion = 0.75;
            spinRatio = 4.0/3;
            spinFinished = false;
            timer.reset();
            timer.start();
        }
        
        if (manipulator.getButton(Button.RIGHT_BUMPER)) {
            spinProportion = 1.0;
            spinRatio = 0.75;
            spinFinished = false;
            timer.reset();
            timer.start();
        }
        
        if (timer.get() >= 0.2) {
            spinFinished = true;
        }
        
        if (spinFinished) {
            intake.stop();
            timer.stop();
            timer.reset();
        } else {
            intake.spin(spinProportion, spinRatio);
        }
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {}
}
