package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

public class TurnInPlaceCommand implements Command {

    private DriveBase driveBase;

    private double speed;
    private double targetAngle;

    private boolean needsToStart;
    private boolean complete;

    private double startAngle;

    public TurnInPlaceCommand(DriveBase driveBase, double speed, double targetAngleDifference) {
        this.driveBase = driveBase;

        this.speed = speed;
        this.targetAngle = targetAngleDifference;

        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart) {
            startAngle = driveBase.getGyroAngle();
            needsToStart = false;
        }

        if (complete) {
            return true;
        } else {
            if (Math.abs(startAngle - driveBase.getGyroAngle()) >= Math.abs(targetAngle)) {
                driveBase.drive(0);
                complete = true;
            } else {
                if (targetAngle > 0) {
                    driveBase.drive(speed, -speed);
                } else {
                    driveBase.drive(-speed, speed);
                }
            }
            return false;
        }
    }

    @Override
    public void reset() {
        needsToStart = true;
        complete = false;
    }

}
