package com.teamcautionrobotics.autonomous;

import com.teamcautionrobotics.autonomous.commands2018.DelayCommand;
import com.teamcautionrobotics.autonomous.commands2018.MoveStraightCommand;
import com.teamcautionrobotics.autonomous.commands2018.MoveStraightDistanceCommand;
import com.teamcautionrobotics.autonomous.commands2018.MoveStraightPIDCommand;
import com.teamcautionrobotics.autonomous.commands2018.ResetEncoders;
import com.teamcautionrobotics.autonomous.commands2018.TurnInPlaceCommand;
import com.teamcautionrobotics.robot2018.DriveBase;

public class CommandFactory {
    private DriveBase driveBase;

    public CommandFactory(DriveBase driveBase) {
        this.driveBase = driveBase;
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

    public Command resetEncoders() {
        return new ResetEncoders(driveBase);
    }

    public Command delay(double time) {
        return new DelayCommand(time);
    }

}
