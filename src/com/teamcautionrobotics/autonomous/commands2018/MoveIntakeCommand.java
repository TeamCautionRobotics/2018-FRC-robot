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

    /**
     * Run the intake for a period of time. Waits for the lift to be at its destination before
     * running the intake.
     * 
     * @param intake
     * @param lift
     * @param power The power at which to run the intake
     * @param time Number of seconds for which to run the intake
     * @see {@link Intake#move(double)}
     */
    public MoveIntakeCommand(Intake intake, Lift lift, double power, double time) {
        this.intake = intake;
        this.power = power;
        this.lift = lift;
        timer = new Timer();
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
