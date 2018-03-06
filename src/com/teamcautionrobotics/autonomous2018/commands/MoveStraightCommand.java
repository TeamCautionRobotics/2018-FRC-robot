package com.teamcautionrobotics.autonomous2018.commands;


import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

import edu.wpi.first.wpilibj.Timer;

public class MoveStraightCommand implements Command {

    private DriveBase driveBase;

    private Timer timer;

    private double speed;
    private double time;
    private boolean keepHeading;

    private boolean needsToStart;
    private boolean complete;

    private double heading;

    public MoveStraightCommand(DriveBase driveBase, double speed, double time,
            boolean keepHeading) {
        this.driveBase = driveBase;

        this.speed = speed;
        this.time = time;
        this.keepHeading = keepHeading;

        timer = new Timer();

        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart) {
            timer.reset();
            timer.start();
            heading = keepHeading ? driveBase.courseHeading : driveBase.getGyroAngle();
            needsToStart = false;
        }

        if (!complete) {
            if (timer.get() >= time) {
                driveBase.drive(0);
                timer.stop();
                complete = true;
            } else {
                double angle = heading - driveBase.getGyroAngle();
                System.out.println("Angle: " + angle + "  Heading: " + heading);
                driveBase.drive(speed, speed - angle * 0.03);
            }
        }
        return complete;
    }

    @Override
    public void reset() {
        needsToStart = true;
        complete = false;
    }

}
