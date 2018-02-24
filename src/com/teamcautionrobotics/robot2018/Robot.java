/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package com.teamcautionrobotics.robot2018;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    static final Path missionScriptPath = Paths.get("/opt/mission.ms");

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
    SendableChooser<StartingPosition> startingPositionChooser;
    Mission activeMission;

    @Override
    public void robotInit() {
        driveBase = new DriveBase(0, 1, 0, 1, 2, 3);

        driverLeft = new EnhancedJoystick(0, 0.1);
        driverRight = new EnhancedJoystick(1, 0.1);
        manipulator = new Gamepad(2);

        intake = new Intake(2, 3, 4);
        climb = new Climb(5);

        commandFactory = new CommandFactory(driveBase);

        missionScriptMission = new MissionScriptMission("Mission Script Mission", missionScriptPath,
                commandFactory);

        startingPositionChooser = new SendableChooser<>();
        for (StartingPosition i: StartingPosition.values()) {
            startingPositionChooser.addObject(i.name, i);
        }

        missionChooser = new SendableChooser<>();

        missionChooser.addDefault("Do Nothing Mission", new Mission("Do Nothing Mission"));
        missionChooser.addObject("Do not use -- Mission Script", missionScriptMission);
        SmartDashboard.putData("Autonomous Mode Select", missionChooser);

        Mission crossAutoLineMission = new Mission("Drive Forward Mission",
                commandFactory.moveStraightDistance(0.5,60,true),
                commandFactory.moveStraight(-0.1, 0.2, false)
        );
        missionChooser.addObject("Drive Forward Mission", crossAutoLineMission);

        Mission centerMissionRightSwitch = new Mission("center mission right switch",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 45),
                commandFactory.moveStraightDistance(0.5, 20, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.3, 90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("center mission right switch", centerMissionRightSwitch);

        Mission centerMissionLeftSwitch = new Mission("center mission left switch",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 30, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(-0.3, 90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("center mission left switch", centerMissionLeftSwitch);

        Mission centerMissionRightScale = new Mission("center mission right scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(-0.3, 45),
                commandFactory.moveStraightDistance(0.5, 140, true),
                commandFactory.turnInPlace(0.3, 50),
                commandFactory.moveStraightDistance(0.5, 195, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.3, 90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("center mission right scale", centerMissionRightScale);

        Mission centerMissionLeftScale = new Mission("center mission left scale",
                commandFactory.moveStraightDistance(0.5, 30, true),
                commandFactory.turnInPlace(0.3, 60),
                commandFactory.moveStraightDistance(0.5, 160, true),
                commandFactory.turnInPlace(-0.3, 50),
                commandFactory.moveStraightDistance(0.5, 185, true),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(-0.3, 85),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("center mission left scale", centerMissionLeftScale);

        Mission rightMissionSwitch = new Mission("right mission switch",
                commandFactory.moveStraightDistance(0.5, 130, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, -90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("right mission switch", rightMissionSwitch);

        
        Mission rightMissionScale = new Mission("right mission scale",
                commandFactory.moveStraightDistance(0.5, 260, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, -90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("right mission scale", rightMissionScale);
        
        Mission leftMissionSwitch = new Mission("left mission switch",
                commandFactory.moveStraightDistance(0.5, 130, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, 90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("left mission switch", leftMissionSwitch);

        Mission leftMissionScale = new Mission("left mission scale",
                commandFactory.moveStraightDistance(0.5, 260, true),
                commandFactory.moveStraight(-0.1, 0.2, false),
                // LIFT THE CUBE!!!!!!!
                commandFactory.turnInPlace(0.5, 90),
                commandFactory.moveStraight(0.5, 0.3, false)
                // DEPLOY THE CUBE!!!!!!!
        );
        missionChooser.addObject("left mission scale", leftMissionScale);

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
        driveBase.drive(forwardCommand + turnCommand, forwardCommand - turnCommand);

        if (manipulator.getButton(Button.X)) {
            climb.ascend();
        }

        // Left bumper spins counterclockwise
        if (manipulator.getButton(Button.LEFT_BUMPER)) {
            intake.timedSpin(-0.25, 0.1);
        }

        // Right bumper spins clockwise
        if (manipulator.getButton(Button.RIGHT_BUMPER)) {
            intake.timedSpin(0.25, 0.1);
        }

        intake.move(manipulator.getAxis(Axis.LEFT_Y));
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {}
}
