package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Lift;

public class SetLiftCommand implements Command {
    
    private Lift lift;
    private double height;
    private final boolean waitForLiftAtDestination;
    private boolean liftCommanded = false;

    public SetLiftCommand(Lift lift, double height, boolean waitForLiftAtDestination) {
        this.lift = lift;
        this.height = height;
        this.waitForLiftAtDestination = waitForLiftAtDestination;
    }

    @Override
    public boolean run() {
        if (!liftCommanded) {
            lift.setDestinationHeight(height);
            liftCommanded = true;
        }

        if (waitForLiftAtDestination) {
            return lift.atDestination();
        } else {
            return true;
        }
    }

    @Override
    public void reset() {
        liftCommanded = false;
    }

}
