package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Climb {

    private VictorSP climbMotor;

    public Climb(int motorPort) {

        climbMotor = new VictorSP(motorPort);

    }

    public void move(double power) {

        climbMotor.set(power);

    }

    public void ascend() {

        climbMotor.set(1.0);

    }

    public void descend() {

        climbMotor.set(-1.0);

    }

}
