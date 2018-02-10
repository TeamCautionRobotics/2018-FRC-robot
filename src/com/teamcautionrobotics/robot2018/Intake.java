package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Timer;

public class Intake {

    private VictorSP intake;
    private VictorSP grabberLeft;
    private VictorSP grabberRight;
    private Timer timer;

    public Intake(int motorPortIntake, int motorPortGrabberLeft, int motorPortGrabberRight) {
        intake = new VictorSP(motorPortIntake);
        grabberLeft = new VictorSP(motorPortGrabberLeft);
        grabberRight = new VictorSP(motorPortGrabberRight);
        timer = new Timer();
    }

    /**
     * @param power positive for in, negative is out, range of [-1, 1]
     */
    public void run(double intakePower, double grabberPowerLeft, double grabberPowerRight) {
        intake.set(intakePower);
        grabberLeft.set(grabberPowerLeft);
        grabberRight.set(grabberPowerRight);
    }

    public void run(double power) {
        run(power, power, power);
    }

    /**
     * @param inPower is average magnitude of grabberLeft and grabberRight, range of [-1, 1].
     * @param spinPower is added to grabberRight and subtracted from grabberLeft.
     * @time spins for that amount of time
     * @return true if spin is finished, false if not
     */
    public boolean spin(double inPower, double spinPower, double time) {
        double leftPower = inPower + spinPower;
        double rightPower = inPower - spinPower;
        run(0.0, leftPower, rightPower);
        startTimerWithoutResetting();
        return tick(time);
    }

    public boolean spin(double proportion, double ratio) {
        return spin(proportion, ratio, 0.2);
    }

    public boolean tick(double time) {
        boolean finished = false;
        if (timer.get() >= time) {
            this.stop();
            timer.stop();
            timer.reset();
            finished = true;
        }
        return finished;
    }

    public void in() {
        this.run(1.0);
    }

    public void out() {
        this.run(-1.0);
    }

    public void spinRight() {
        this.spin(1, 3.0 / 4);
    }

    public void spinLeft() {
        this.spin(0.75, 4.0 / 3);
    }

    public void stop() {
        this.run(0.0);
    }
    
    public void startTimerWithoutResetting() {
        if (timer.get() == 0) {
            timer.start();
        }
    }
    
    public void resetTimer() {
        timer.reset();
    }
}
