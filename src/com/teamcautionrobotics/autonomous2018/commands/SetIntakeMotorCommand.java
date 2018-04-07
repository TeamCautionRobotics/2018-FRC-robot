package com.teamcautionrobotics.autonomous2018.commands;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.Harvester;

public class SetIntakeMotorCommand implements Command {

    private Harvester harvester;
    private double power;

    public SetIntakeMotorCommand(Harvester harvester, double power) {
        this.harvester = harvester;
        this.power = power;
        reset();
    }

    @Override
    public boolean run() {
        harvester.move(power);
        return true;
    }

    @Override
    public void reset() {}
}
