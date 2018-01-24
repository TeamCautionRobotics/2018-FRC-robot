package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Intake {

    private VictorSP intake1;
    private VictorSP intake2;

    public Intake(int motorPort1, int motorPort2) {
        intake1 = new VictorSP(motorPort1);
        intake2 = new VictorSP(motorPort2);
    }

    /**
     * @param power positive for in, negative is out, range of [-1, 1]
     */
    public void run(double power) {
        intake1.set(power);
        intake2.set(power);
    }

    public void in() {
        this.run(1.0);
    }

    public void out() {
        this.run(-1.0);
    }

}
