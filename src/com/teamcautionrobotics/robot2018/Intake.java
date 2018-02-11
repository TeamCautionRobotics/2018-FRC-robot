package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {

    private VictorSP intake;
    private VictorSP grabberLeft;
    private VictorSP grabberRight;

    private Timer timer;
    private boolean timedSpin = false;
    private double spinDuration = 0;

    private double spinPower = 0;

    public Intake(int intakeChannel, int grabberLeftChannel, int grabberRightChannel) {
        intake = new VictorSP(intakeChannel);
        grabberLeft = new VictorSP(grabberLeftChannel);
        grabberRight = new VictorSP(grabberRightChannel);

        timer = new Timer();
        timer.start();
    }

    /**
     * Set the individual motor powers directly. {@link #move(double)},
     * {@link #spin(double, double)}, or {@link #timedSpin} should probably be used instead.
     * 
     * @param power positive for in, negative for out, range of [-1, 1]
     */
    public void moveMotors(double intakePower, double grabberLeftPower, double grabberRightPower) {
        intake.set(intakePower);
        grabberLeft.set(grabberLeftPower);
        grabberRight.set(grabberRightPower);
    }

    /**
     * Move the intake motors. This also applies the spin power and handles resetting the spin power
     * when the specified time has elapsed. The grabber motors will never move in the opposite
     * direction as the inPower specifies (this could happen if a fast spin and slow inPower is
     * requested).
     * @param inPower overall speed of the entire intake (grabber and inner part). Positive for in,
     *        negative for out. range of [-1, 1]
     */
    public void move(double inPower) {
        // Check if the spinDuration has elapsed
        if (timedSpin && timer.get() > spinDuration) {
            this.spinPower = 0;
            timedSpin = false;
        }

        double leftPower = inPower - this.spinPower;
        double rightPower = inPower + this.spinPower;

        // Keep the grabber motors from being sent opposite of the inPower
        if (inPower > 0) {
            leftPower = Math.max(leftPower, 0);
            rightPower = Math.max(rightPower, 0);
        } else {
            leftPower = Math.min(leftPower, 0);
            rightPower = Math.min(rightPower, 0);
        }

        moveMotors(inPower, leftPower, rightPower);
    }

    /**
     * Do a timed spin. After the specified time, the spinPower reverts to zero. 
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
     * @param spinPower
     */
    public void spin(double spinPower) {
        timedSpin = false;
        this.spinPower = spinPower;
    }
}
