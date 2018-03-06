package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

public class MoveStraightDistanceCommand implements Command {

    private DriveBase driveBase;

    private double speed;
    private double distance;
    private boolean stopAtEnd;

    private boolean needsToStart;
    private boolean complete;

    private double heading;

    public MoveStraightDistanceCommand(DriveBase driveBase, double speed, double distance, boolean stopAtEnd) {
        this.driveBase = driveBase;

        this.speed = speed;
        this.distance = distance;
        this.stopAtEnd = stopAtEnd;

        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart) {
            driveBase.resetEncoders();
            heading = driveBase.getGyroAngle();

            driveBase.courseHeading = heading;

            needsToStart = false;
        }

        if (complete) {
            return true;
        } else {
            if (Math.abs(driveBase.getDistance()) >= distance) {
                if (stopAtEnd) {
                    driveBase.drive(0);
                }
                complete = true;
            } else {
                double angle = heading - driveBase.getGyroAngle();
                System.out.println("Angle: " + angle + "  Heading: " + heading);
                driveBase.drive(speed, speed - angle * 0.03);
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
