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
import com.teamcautionrobotics.robot2018.AutoEnums.AutoObjective;
import com.teamcautionrobotics.robot2018.AutoEnums.PlateSide;
import com.teamcautionrobotics.robot2018.AutoEnums.StartingPosition;
import com.teamcautionrobotics.robot2018.Gamepad.Axis;
import com.teamcautionrobotics.robot2018.Gamepad.Button;

import edu.wpi.first.wpilibj.DriverStation;
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

    String fmsData;
    PlateSide switchPosition;
    PlateSide scalePosition;

    SendableChooser<Mission> missionChooser;
    SendableChooser<StartingPosition> startingPositionChooser;
    SendableChooser<AutoObjective> autoObjectiveChooser;
    MissionSelector missionSelector;
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

        startingPositionChooser = new SendableChooser<>();
        startingPositionChooser.addDefault("Center", StartingPosition.CENTER);
        startingPositionChooser.addObject("Left", StartingPosition.LEFT);
        startingPositionChooser.addObject("Right", StartingPosition.RIGHT);
        SmartDashboard.putData("Starting Position Select", startingPositionChooser);

        autoObjectiveChooser = new SendableChooser<>();
        autoObjectiveChooser.addDefault("Switch", AutoObjective.SWITCH);
        autoObjectiveChooser.addObject("Scale", AutoObjective.SCALE);
        autoObjectiveChooser.addObject(AutoObjective.SWITCH_OR_SCALE.toString(), AutoObjective.SWITCH_OR_SCALE);
        autoObjectiveChooser.addObject("Auto line", AutoObjective.AUTO_LINE);
        autoObjectiveChooser.addObject("Do nothing", AutoObjective.DO_NOTHING);
        SmartDashboard.putData("Auto Objective Select", autoObjectiveChooser);

        missionChooser = new SendableChooser<>();

        missionScriptMission = new MissionScriptMission("Mission Script Mission", missionScriptPath,
                commandFactory);
        missionChooser.addDefault("Do not use -- Mission Script", missionScriptMission);
        SmartDashboard.putData("Autonomous Mode Select", missionChooser);

        missionSendable = new MissionSendable("Teleop Mission", missionChooser::getSelected);
        SmartDashboard.putData(missionSendable);

        missionSelector = new MissionSelector(commandFactory);
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
        fmsData = DriverStation.getInstance().getGameSpecificMessage();
        System.out.println("dsg " + fmsData);
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

        activeMission = missionSelector.selectMissionFromFieldData(switchPosition, scalePosition,
                startingPositionChooser.getSelected(), autoObjectiveChooser.getSelected());

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
