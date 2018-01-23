package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Climb {

    private VictorSP climbMotor;

    public Climb(int motorPort) {

        climbMotor = new VictorSP(motorPort);

    }

    public void ascend() {

        climbMotor.set(1.0);

    }

    public void ascend(double power) {

        climbMotor.set(power);

    }

    public void descend() {

        climbMotor.set(-1.0);

    }

    public void descend(double power) {

        climbMotor.set(-power);

    }

}
