package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.misc2018.AbstractPIDSource;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

public class Harvester {

    public enum HarvesterAngle {
        UP(90), AIMED(45), DOWN(0);

        private static HarvesterAngle[] values = values();

        public final double angle;

        private HarvesterAngle(double angle) {
            this.angle = angle;
        }
    }

    private VictorSP grabber;
    private VictorSP angularOptimizer;

    private Encoder angularOptimizerEncoder;
    private DigitalInput colorSensor;

    private PIDController pidController;

    private Timer timer;
    private boolean timedSpin = false;
    private double spinDuration = 0;

    private double spinPower = 0;

    public Harvester(int grabberChannel, int angularOptimizerChannel,
            int angularOptimizerEncoderChannelA, int angularOptimizerEncoderChannelB,
            int colorSensorChannel, double Kp, double Ki, double Kd) {

        grabber = new VictorSP(grabberChannel);
        angularOptimizer = new VictorSP(angularOptimizerChannel);

        colorSensor = new DigitalInput(colorSensorChannel);

        timer = new Timer();
        timer.start();

        angularOptimizerEncoder =
                new Encoder(angularOptimizerEncoderChannelA, angularOptimizerEncoderChannelB);
        angularOptimizerEncoder.setDistancePerPulse(360 / 1024);

        pidController = new PIDController(Kp, Ki, Kd, 0,
                new AngularOptimizerPIDSource(PIDSourceType.kDisplacement),
                this::moveAngularOptimizer);
        pidController.setOutputRange(-1, 1);
        // TODO: get from lift levels or other better non magic place for number
        pidController.setInputRange(0, 90);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * Set the grabber motor power directly. {@link #move(double)}, {@link #spin(double, double)},
     * or {@link #timedSpin} should probably be used instead.
     * 
     * @param power positive for in, negative for out, range of [-1, 1]
     */
    public void moveGrabber(double grabberPower) {
        // stops grabber from spinning in if cubeInGrabber color sensor is triggered
        grabber.set(grabberPower);
    }

    /**
     * Move the harvester motors. This also applies the spin power and handles resetting the spin
     * power when the specified time has elapsed. The grabber motors will never move in the opposite
     * direction as the inPower specifies (this could happen if a fast spin and slow inPower is
     * requested).
     * 
     * @param grabberPower overall speed of grabber. Positive for in, negative for out. range of
     *        [-1, 1]
     */
    public void move(double grabberPower) {
        // Check if the spinDuration has elapsed
        if (timedSpin && timer.get() > spinDuration) {
            this.spinPower = 0;
            timedSpin = false;
        }

        double leftPower, rightPower;
        if (true) {
            // Grabber rollers can spin reverse of the grabber
            leftPower = grabberPower - this.spinPower;
            rightPower = grabberPower + this.spinPower;
        } else {
            // Grabber rollers can not spin reverse of the grabber
            leftPower = spinPower < 0 ? 0 : grabberPower;
            rightPower = spinPower > 0 ? 0 : grabberPower;
        }

        // TODO: maybe clean this up
        moveGrabber(grabberPower);
    }

    public void moveAngularOptimizer(double angularPower) {
        angularOptimizer.set(angularPower);
    }

    public void setDestinationAngle(double angle) {
        pidController.setSetpoint(angle);
    }

    public void setDestinationAngle(HarvesterAngle harvesterAngle) {
        setDestinationAngle(harvesterAngle.angle);
    }

    public double getCurrentAngle() {
        return angularOptimizerEncoder.getDistance();
    }

    public HarvesterAngle getCurrentHarvesterAngle() {
        return convertAngleToHarvesterAngle(getCurrentAngle());
    }

    public double getDestinationAngle() {
        return pidController.getSetpoint();
    }

    public HarvesterAngle getDestinationHarvesterAngle() {
        return convertAngleToHarvesterAngle(getDestinationAngle());
    }

    public boolean atDestinationAngle() {
        return Math.abs(getDestinationAngle() - getCurrentAngle()) <= 1;
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

    public static HarvesterAngle convertAngleToHarvesterAngle(double angle) {
        HarvesterAngle convertedHarvesterAngle = HarvesterAngle.values[0];
        for (HarvesterAngle harvesterAngle : HarvesterAngle.values) {
            // 22.5 is a magic number
            double midpoint = harvesterAngle.angle + 22.5;
            convertedHarvesterAngle = harvesterAngle;
            // OHHHHH WE'RE HALFWAY THERE! WHOAWHOA then move on to the next LiftLevel and try
            // again; if not, break out of the loop and return the current liftPosition the for loop
            // is on
            if (angle < midpoint) {
                break;
            }
        }
        return convertedHarvesterAngle;
    }

    public boolean cubeIsInGrabber() {
        return !colorSensor.get();
    }

    public void enablePID() {
        if (!pidController.isEnabled()) {
            pidController.reset();
            pidController.enable();
        }
    }

    public void disablePID() {
        if (pidController.isEnabled()) {
            // pidController.reset() also disables the PID controller
            pidController.reset();
        }
    }

    public void resetEncoder() {
        angularOptimizerEncoder.reset();
    }

    class AngularOptimizerPIDSource extends AbstractPIDSource {

        public AngularOptimizerPIDSource(PIDSourceType sourceType) {
            super(sourceType);
        }

        @Override
        public double pidGet() {
            switch (type) {
                case kDisplacement:
                    return angularOptimizerEncoder.getDistance();
                case kRate:
                    return angularOptimizerEncoder.getRate();
                default:
                    return 0.0;
            }
        }
    }
}
