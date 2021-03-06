package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Harvester;
import com.teamcautionrobotics.robot2018.Elevator;

import edu.wpi.first.wpilibj.Timer;

public class MoveIntakeCommand implements Command {

    private Harvester harvester;
    private Elevator elevator;

    private Timer timer;

    private double power;
    private double time;

    private boolean needsToStart;
    private boolean complete;

    /**
     * Run the harvester for a period of time. Waits for the elevator to be at its destination before
     * running the harvester.
     * 
     * @param harvester
     * @param elevator
     * @param power The power at which to run the harvester
     * @param time Number of seconds for which to run the harvester
     * @see {@link Harvester#move(double)}
     */
    public MoveIntakeCommand(Harvester harvester, Elevator elevator, double power, double time) {
        this.harvester = harvester;
        this.power = power;
        this.elevator = elevator;
        this.time = time;
        timer = new Timer();
        reset();
    }

    @Override
    public boolean run() {
        if (needsToStart && elevator.atDestination()) {
            timer.reset();
            timer.start();
            harvester.move(power);
            needsToStart = false;
        }

        if (!complete) {
            if (timer.get() >= time) {
                harvester.move(0);
                timer.stop();
                complete = true;
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
