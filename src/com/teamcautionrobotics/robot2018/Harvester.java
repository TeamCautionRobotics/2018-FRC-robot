package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

public class Harvester {

    private VictorSP grabber;
    private VictorSP intakeLeft;
    private VictorSP intakeRight;

    private DigitalInput colorSensor;

    private Timer timer;
    private boolean timedSpin = false;
    private double spinDuration = 0;

    private double spinPower = 0;

    public Harvester(int grabberChannel, int intakeLeftChannel, int intakeRightChannel,
            int colorSensorChannel) {

        grabber = new VictorSP(grabberChannel);
        intakeLeft = new VictorSP(intakeLeftChannel);
        intakeRight = new VictorSP(intakeRightChannel);

        colorSensor = new DigitalInput(colorSensorChannel);

        timer = new Timer();
        timer.start();
    }

    /**
     * Set the individual motor powers directly. {@link #move(double)},
     * {@link #spin(double, double)}, or {@link #timedSpin} should probably be used instead.
     * 
     * @param power positive for in, negative for out, range of [-1, 1]
     */
    public void moveMotors(double grabberPower, double intakeLeftPower, double intakeRightPower) {
        // stops grabber from spinning in if cubeInGrabber color sensor is triggered
        grabber.set(grabberPower);
        intakeLeft.set(intakeLeftPower);
        intakeRight.set(intakeRightPower);
    }

    /**
     * Move the intake motors. This also applies the spin power and handles resetting the spin power
     * when the specified time has elapsed. The grabber motors will never move in the opposite
     * direction as the inPower specifies (this could happen if a fast spin and slow inPower
     * isrequested).
     * 
     * @param grabberPower overall speed of the entire intake (grabber and inner part). Positive for
     *        in, negative for out. range of [-1, 1]
     */
    public void move(double grabberPower, double intakePower) {
        // Check if the spinDuration has elapsed
        if (timedSpin && timer.get() > spinDuration) {
            this.spinPower = 0;
            timedSpin = false;
        }

        double leftPower, rightPower;
        if (true) {
            // Intake rollers can spin reverse of the grabber
            leftPower = intakePower - this.spinPower;
            rightPower = intakePower + this.spinPower;
        } else {
            // Intake rollers can not spin reverse of the grabber
            leftPower = spinPower < 0 ? 0 : grabberPower;
            rightPower = spinPower > 0 ? 0 : grabberPower;
        }

        // Keep the grabber motors from being sent opposite of the inPower
        if (grabberPower > 0) {
            leftPower = Math.max(leftPower, 0);
            rightPower = Math.max(rightPower, 0);
        } else {
            leftPower = Math.min(leftPower, 0);
            rightPower = Math.min(rightPower, 0);
        }

        // TODO: maybe clean this up
        moveMotors(grabberPower, leftPower, rightPower);
    }

    public void move(double inPower) {
        move(inPower, inPower);
    }

    /**
     * Do a timed spin. After the specified time, the spinPower reverts to zero.
     * 
     * @param spinPower The speed difference between the left and right sides. Negative to spin
     *        counterclockwise, positive to spin clockwise. range of [-1, 1]
     * @time How long from now the spin should apply for.
     */
    public void timedSpin(double spinPower, double time) {
        timer.reset();
        this.spinPower = spinPower;

        timedSpin = true;
        spinDuration = time;
    }

    /**
     * Do a spin. This will stop any currently running timed spin and has no expiration.
     * 
     * @param spinPower
     */
    public void spin(double spinPower) {
        timedSpin = false;
        this.spinPower = spinPower;
    }

    public void bulldoze() {
        move(grabber.get(), -1.0);
    }

    public boolean cubeIsInGrabber() {
        return !colorSensor.get();
    }
}
