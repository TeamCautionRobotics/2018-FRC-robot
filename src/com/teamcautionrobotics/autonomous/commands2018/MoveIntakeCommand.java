package com.teamcautionrobotics.autonomous.commands2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Intake;
import com.teamcautionrobotics.robot2018.Lift;

import edu.wpi.first.wpilibj.Timer;

public class MoveIntakeCommand implements Command {

    private Intake intake;
    private Lift lift;

    private Timer timer;

    private double power;
    private double time;

    private boolean needsToStart;
    private boolean complete;

    public MoveIntakeCommand(Intake intake, Lift lift, double power, double time) {
        this.intake = intake;
        this.power = power;
        this.lift = lift;
        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart) {
            timer.reset();
            timer.start();
            needsToStart = false;
        }

        if (!complete) {
            if (timer.get() >= time) {
                intake.move(0);
                timer.stop();
                complete = true;
            } else {
                if (lift.atDestination()) {
                    intake.move(power);
                }
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
