/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package com.teamcautionrobotics.robot2018;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.CommandFactory;
import com.teamcautionrobotics.autonomous.Mission;
import com.teamcautionrobotics.autonomous.MissionScriptMission;
import com.teamcautionrobotics.autonomous.MissionSendable;
import com.teamcautionrobotics.robot2018.Gamepad.Axis;
import com.teamcautionrobotics.robot2018.Gamepad.Button;

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

    static final Path missionScriptPath = Paths.get("/opt/roborio/mission.ms");
    DriveBase driveBase;

    EnhancedJoystick driverLeft;
    EnhancedJoystick driverRight;
    Gamepad manipulator;

    Intake intake;
    Climb climb;

    CommandFactory commandFactory;
    MissionScriptMission missionScriptMission;
    MissionSendable missionSendable;
    SendableChooser<Mission> missionChooser;

    @Override
    public void robotInit() {
        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        intake = new Intake(2, 3);
        climb = new Climb(4);

        commandFactory = new CommandFactory(driveBase);
        
        missionChooser = new SendableChooser<Mission>();
        missionChooser.setName("Mission Chooser");

        missionSendable = new MissionSendable("Run Auto in Teleop", missionChooser::getSelected);
        SmartDashboard.putData(missionSendable);

        missionScriptMission = new MissionScriptMission("Mission Script Mission", missionScriptPath,
                commandFactory);
        missionChooser.addObject("Do not use -- Mission Script", missionScriptMission);
        
        missionChooser.addDefault("Do Nothing Mission", new Mission("Do Nothing Mission"));
        
        SmartDashboard.putData(missionChooser);
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
        if ((missionSendable.run() && !missionChooser.getSelected().enableControls)
                || driveBase.pidController.isEnabled()) {
            return;
        }
        
        driveBase.drive(driverLeft.getY(), driverRight.getY());

        intake.run(manipulator.getAxis(Axis.LEFT_Y));

        if (manipulator.getButton(Button.X)) {
            climb.ascend();
        }
        
        
        
        
        
        
        
        
        
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {}
}
