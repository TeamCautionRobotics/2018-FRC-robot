package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.autonomous.commands.CommandFactory;
import com.teamcautionrobotics.robot2018.DriveBase;
import com.teamcautionrobotics.robot2018.Intake;

public class CommandFactory2018 extends CommandFactory {
    private DriveBase driveBase;
    private Intake intake;
    

    public CommandFactory2018(DriveBase driveBase, Intake intake) {
        this.driveBase = driveBase;
        this.intake = intake;
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
    
    public Command moveIntake(double speed, double time) {
        return new MoveIntakeCommand(intake, speed, time);
    }

    public Command deployCube() {
        return moveIntake(-1.0, 0.5);
    }

    public Command resetEncoders() {
        return new ResetEncoders(driveBase);
    }
}
