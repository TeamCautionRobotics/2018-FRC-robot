package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

public class MoveStraightPIDCommand implements Command {

    private DriveBase driveBase;

    private double distance;

    private boolean needsToStart;
    private boolean complete;

    public MoveStraightPIDCommand(DriveBase driveBase, double maxSpeed, double distance) {
        this.driveBase = driveBase;

        this.distance = distance;

        driveBase.pidController.setOutputRange(-maxSpeed, maxSpeed);

        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart) {
            driveBase.resetEncoders();

            driveBase.pidInit();
            driveBase.pidController.setSetpoint(distance);
            driveBase.pidController.enable();

            needsToStart = false;
        }

        complete = driveBase.pidController.onTarget();
        if (complete) {
            driveBase.pidController.disable();
        }
        return complete;
    }

    @Override
    public void reset() {
        needsToStart = true;
        complete = false;
    }

}
