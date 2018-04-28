package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Elevator;

public class SetElevatorCommand implements Command {

    private Elevator elevator;
    private double height;
    private final boolean waitForElevatorAtDestination;
    private boolean elevatorCommanded = false;

    public SetElevatorCommand(Elevator elevator, double height, boolean waitForElevatorAtDestination) {
        this.elevator = elevator;
        this.height = height;
        this.waitForElevatorAtDestination = waitForElevatorAtDestination;
    }

    @Override
    public boolean run() {
        if (!elevatorCommanded) {
            elevator.enablePID();
            elevator.setDestinationHeight(height);
            elevatorCommanded = true;
        }

        if (waitForElevatorAtDestination) {
            return elevator.atDestination();
        } else {
            return true;
        }
    }

    @Override
    public void reset() {
        elevatorCommanded = false;
    }

}
