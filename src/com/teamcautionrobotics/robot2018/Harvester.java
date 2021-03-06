package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.misc2018.AbstractPIDSource;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Harvester {

    public enum HarvesterAngle {
        UP(0), AIMED(-45), DOWN(-85);

        public final double angle;

        private HarvesterAngle(double angle) {
            this.angle = angle;
        }
    }

    private VictorSP grabber;
    public VictorSP angulator;

    private Encoder angulatorEncoder;

    public PIDController pidController;

    public Harvester(int grabberChannel, int angulatorChannel,
            int angulatorEncoderChannelA, int angulatorEncoderChannelB, double Kp,
            double Ki, double Kd) {
        grabber = new VictorSP(grabberChannel);
        angulator = new VictorSP(angulatorChannel);
//        angulator.setInverted(true);

        angulatorEncoder =
                new Encoder(angulatorEncoderChannelB, angulatorEncoderChannelA);
        // The sources of these constants           degrees  pulses/rot  additional reduction
        angulatorEncoder.setDistancePerPulse((360.0 / 1024.0) * (12.0 / 28.0));

        pidController = new PIDController(Kp, Ki, Kd, 0,
                new AngulatorPIDSource(PIDSourceType.kDisplacement),
                this::moveAngulator);
        pidController.setOutputRange(-1, 1);
        // TODO: Set to correct values
        pidController.setInputRange(-105, 10);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * Set the grabber motor power directly.
     * @param power positive for in, negative for out, range of [-1, 1]
     */
    public void move(double power) {
        // stops grabber from spinning in if cubeInGrabber color sensor is triggered
        grabber.set(power);
    }

    public void moveAngulator(double angularPower) {
        angulator.set(angularPower);
        SmartDashboard.putNumber("Angulator power", angularPower);
    }

    public void setDestinationAngle(double angle) {
        pidController.setSetpoint(angle);
    }

    public void setDestinationAngle(HarvesterAngle harvesterAngle) {
        setDestinationAngle(harvesterAngle.angle);
    }

    public double getCurrentAngle() {
        return angulatorEncoder.getDistance();
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

    public static HarvesterAngle convertAngleToHarvesterAngle(double angle) {
        HarvesterAngle convertedHarvesterAngle = HarvesterAngle.values()[0];
        for (HarvesterAngle harvesterAngle : HarvesterAngle.values()) {
            // 22.5 is a magic number
            double midpoint = harvesterAngle.angle + 22.5;
            convertedHarvesterAngle = harvesterAngle;
            // OHHHHH WE'RE HALFWAY THERE! WHOAWHOA then move on to the next HarvesterAngle and try
            // again; if not, break out of the loop and return the current harvesterAngle the for
            // loop
            // is on and a pear
            if (angle < midpoint) {
                break;
            }
        }
        return convertedHarvesterAngle;
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
        angulatorEncoder.reset();
    }

    class AngulatorPIDSource extends AbstractPIDSource {

        public AngulatorPIDSource(PIDSourceType sourceType) {
            super(sourceType);
        }

        @Override
        public double pidGet() {
            switch (type) {
                case kDisplacement:
                    return angulatorEncoder.getDistance();
                case kRate:
                    return angulatorEncoder.getRate();
                default:
                    return 0.0;
            }
        }
    }
}
