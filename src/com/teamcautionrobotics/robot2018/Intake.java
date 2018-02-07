package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Timer;

public class Intake {

    private VictorSP intake1;
    private VictorSP grabberLeft;
    private VictorSP grabberRight;

    public Intake(int motorPortIntake, int motorPortGrabber1, int motorPortGrabber2) {
        intake1 = new VictorSP(motorPortIntake);
        grabberLeft = new VictorSP(motorPortGrabber1);
        grabberRight = new VictorSP(motorPortGrabber2);
    }

    /**
     * @param power positive for in, negative is out, range of [-1, 1]
     */
    public void run(double intakePower, double grabberPowerLeft, double grabberPowerRight) {
        intake1.set(intakePower);
        grabberLeft.set(grabberPowerLeft);
        grabberRight.set(grabberPowerRight);
    }
    
    public void run(double power) {
        run(power, power, power);
    }

    /**
     * @param proportion is value set to grabberRight, range of [0, 1]. 
     * Values from [-1, 0) will not throw an error, but are recommended against.
     * @param ratio is multiplied by proportion into grabberLeft. 
     * Math.abs(ratio * proportion) cannot be greater than 1
     * Always spins both motors inward.
     */
    public void spin(double proportion, double ratio) {
        double leftPower = proportion * ratio;
        double rightPower = proportion;
        run(0.0, leftPower, rightPower);
    }

    public void in() {
        this.run(1.0);
    }

    public void out() {
        this.run(-1.0);
    }

    public void spinRight() {
        this.spin(1, 3.0/4);
    }

    public void spinLeft() {
        this.spin(0.75, 4.0/3);
    }
    
    public void stop() {
        this.run(0.0);
    }
}
