package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.commands.CommandFactory;
import com.teamcautionrobotics.robot2018.DriveBase;
import com.teamcautionrobotics.robot2018.Harvester;
import com.teamcautionrobotics.robot2018.Harvester.HarvesterAngle;
import com.teamcautionrobotics.robot2018.Elevator;
import com.teamcautionrobotics.robot2018.Elevator.ElevatorLevel;

public class CommandFactory2018 extends CommandFactory {
    private DriveBase driveBase;
    private Harvester harvester;
    private Elevator lift;

    public CommandFactory2018(DriveBase driveBase, Harvester harvester, Elevator lift) {
        this.driveBase = driveBase;
        this.harvester = harvester;
        this.lift = lift;
    }


    public Command moveStraight(double speed, double time, boolean keepHeading) {
        return new MoveStraightCommand(driveBase, speed, time, keepHeading);
    }

    public Command moveStraightDistance(double speed, double distance, boolean stopAtEnd) {
        return new MoveStraightDistanceCommand(driveBase, speed, distance, stopAtEnd);
    }

    public Command moveStraightPID(double distance) {
        return new MoveStraightPIDCommand(driveBase, 1.0, distance);
    }

    public Command moveStraightPID(double maxSpeed, double distance) {
        return new MoveStraightPIDCommand(driveBase, maxSpeed, distance);
    }

    public Command turnInPlace(double speed, double targetAngle) {
        return new TurnInPlaceCommand(driveBase, speed, targetAngle);
    }

    public Command moveIntake(double power, double time) {
        return new MoveIntakeCommand(harvester, lift, power, time);
    }

    public Command rotateHarvester(double angle, boolean waitForRotation) {
        return new RotateHarvesterCommand(harvester, angle, waitForRotation);
    }

    public Command rotateHarvester(double angle) {
        return rotateHarvester(angle, true);
    }

    public Command rotateHarvester(HarvesterAngle harvesterAngle, boolean waitForRotation) {
        return rotateHarvester(harvesterAngle.angle, waitForRotation);
    }

    public Command rotateHarvester(HarvesterAngle harvesterAngle) {
        return rotateHarvester(harvesterAngle.angle);
    }

    public Command deployCube() {
        return moveIntake(-0.3, 0.5);
    }

    public Command setLift(ElevatorLevel liftLevel) {
        return setLift(liftLevel.height);
    }

    public Command setLift(double height) {
        return setLift(height, false);
    }

    public Command setLift(ElevatorLevel liftLevel, boolean waitForLift) {
        return setLift(liftLevel.height, waitForLift);
    }

    public Command setLift(double height, boolean waitForLift) {
        return new SetLiftCommand(lift, height, waitForLift);
    }

    public Command checkDriveEncoders(double expectedDistance) {
        return new CheckDriveEncodersCommand(driveBase, expectedDistance);
    }

    public Command resetEncoders() {
        return new ResetEncoders(driveBase);
    }
}
