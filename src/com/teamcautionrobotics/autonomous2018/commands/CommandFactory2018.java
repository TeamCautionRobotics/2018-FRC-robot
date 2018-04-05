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
    private Elevator elevator;

    public CommandFactory2018(DriveBase driveBase, Harvester harvester, Elevator elevator) {
        this.driveBase = driveBase;
        this.harvester = harvester;
        this.elevator = elevator;
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
        return new MoveIntakeCommand(harvester, elevator, power, time);
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

    public Command setElevator(ElevatorLevel elevatorLevel) {
        return setElevator(elevatorLevel.height);
    }

    public Command setElevator(double height) {
        return setElevator(height, false);
    }

    public Command setElevator(ElevatorLevel elevatorLevel, boolean waitForElevator) {
        return setElevator(elevatorLevel.height, waitForElevator);
    }

    public Command setElevator(double height, boolean waitForElevator) {
        return new SetElevatorCommand(elevator, height, waitForElevator);
    }

    public Command checkDriveEncoders(double expectedDistance) {
        return new CheckDriveEncodersCommand(driveBase, expectedDistance);
    }

    public Command resetEncoders() {
        return new ResetEncoders(driveBase);
    }
}
