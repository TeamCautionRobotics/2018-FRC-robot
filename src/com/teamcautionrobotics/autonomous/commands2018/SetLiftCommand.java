package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Lift;
import com.teamcautionrobotics.robot2018.Lift.LiftLevel;

public class SetLiftCommand implements Command {
    
    private Lift lift;
    private LiftLevel liftLevel;

    public SetLiftCommand(Lift lift, LiftLevel liftLevel) {
        this.lift = lift;
        this.liftLevel = liftLevel;
        reset();
    }

    @Override
    public boolean run() {
        lift.setLevel(liftLevel);
        return true;
    }

    @Override
    public void reset() {
        
    }

}
