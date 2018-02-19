package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Lift;
import com.teamcautionrobotics.robot2018.Lift.LiftLevel;

public class SetLiftCommand implements Command {
    
    private Lift lift;
    private LiftLevel liftLevel;
    private final boolean waitForLiftAtDestination;
    private boolean liftCommanded = false;

    public SetLiftCommand(Lift lift, LiftLevel liftLevel, boolean waitForLiftAtDestination) {
        this.lift = lift;
        this.liftLevel = liftLevel;
        this.waitForLiftAtDestination = waitForLiftAtDestination;
    }

    @Override
    public boolean run() {
        if (!liftCommanded) {
            lift.setLevel(liftLevel);
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
