package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Harvester;

public class RotateHarvesterCommand implements Command {

    private Harvester harvester;

    private double angle;
    private boolean waitForRotation;

    private boolean rotationCommanded;

    /**
     * Rotate the harvester to a specified angle.
     * 
     * @param harvester
     * @param angle The desired angle to which to rotate the harvester
     * @see {@link Harvester#move(double)}
     */
    public RotateHarvesterCommand(Harvester harvester, double angle, boolean waitForRotation) {
        this.harvester = harvester;
        this.angle = angle;
        this.waitForRotation = waitForRotation;
        reset();
    }

    @Override
    public boolean run() {
        if (!rotationCommanded) {
            harvester.enablePID();
            harvester.setDestinationAngle(angle);
            rotationCommanded = true;
        }

        if (waitForRotation) {
            return harvester.atDestinationAngle();
        } else {
            return true;
        }
    }

    @Override
    public void reset() {
        rotationCommanded = false;
    }

}
