package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Intake {

    private VictorSP intake1;
    private VictorSP intake2;

    public Intake(int motorPort1, int motorPort2) {

        intake1 = new VictorSP(motorPort1);
        intake2 = new VictorSP(motorPort2);

    }

    // in is positive
    public void run(double power) {

        intake1.set(power);
        intake2.set(power);

    }

    public void in() {

        intake1.set(1.0);
        intake2.set(1.0);

    }

    public void out() {

        intake1.set(-1.0);
        intake2.set(-1.0);

    }

}
