package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Climb {

    private VictorSP climbMotor;

    public Climb(int motorPort) {
        climbMotor = new VictorSP(motorPort);
    }

    /**
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        climbMotor.set(power);
    }

    public void ascend() {
        this.move(1.0);
    }

    public void descend() {
        this.move(-1.0);
    }

}
