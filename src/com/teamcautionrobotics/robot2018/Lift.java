package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.misc2018.AbstractPIDSource;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift {

    public enum LiftLevel {
        GROUND(0), SWITCH(28), LOW_SCALE(56), HIGH_SCALE(62);

        private static LiftLevel[] values = values();

        public final double height;

        private LiftLevel(double height) {
            this.height = height;
        }

        public LiftLevel next() {
            if (this.ordinal() == values.length - 1) {
                return this;
            } else {
                return values[this.ordinal() + 1];
            }
        }

        public LiftLevel previous() {
            if (this.ordinal() == 0) {
                return this;
            } else {
                return values[this.ordinal() - 1];
            }
        }
    }

    private VictorSP liftMotor;
    public Encoder liftEncoder;
    public PIDController pidController;

    public Lift(int motorPort, int encoderChannelA, int encoderChannelB, double Kp, double Ki,
            double Kd) {
        liftMotor = new VictorSP(motorPort);
        liftMotor.setInverted(true);
        liftEncoder = new Encoder(encoderChannelA, encoderChannelB);

        // Winch drum diameter is 1.25 inches
        liftEncoder.setDistancePerPulse((1.25 * Math.PI) / 1024);
        pidController = new PIDController(Kp, Ki, Kd, 0,
                new LiftPIDSource(PIDSourceType.kDisplacement), this::move);
        pidController.setOutputRange(-1, 1);
        // TODO: get from lift levels or other better non magic place for number
        pidController.setInputRange(0, 62);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * Directly set the power of the motors. Probably best not to use this, but use
     * {@link #setLevel(LiftLevel)} or {@link #setHeight(double)} instead.
     * 
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        SmartDashboard.putNumber("lift power", power);
        liftMotor.set(power);
    }

    public void setHeight(double height) {
        pidController.setSetpoint(height);
    }

    public void setLevel(LiftLevel destinationLiftLevel) {
        setHeight(destinationLiftLevel.height);
    }

    public double getCurrentHeight() {
        return liftEncoder.getDistance();
    }

    public LiftLevel getCurrentLiftLevel() {
        return convertHeightToLiftLevel(getCurrentHeight());
    }

    public double getDestinationHeight() {
        return pidController.getSetpoint();
    }

    public LiftLevel getDestinationLiftLevel() {
        return convertHeightToLiftLevel(getDestinationHeight());
    }
    
    public boolean atDestination() {
        if (getDestinationHeight() <= getCurrentHeight() + 1
                && getDestinationHeight() >= getCurrentHeight() - 1) {
            return true;
        }
        return false;
    }

    public static LiftLevel convertHeightToLiftLevel(double height) {
        LiftLevel convertedLiftLevel = LiftLevel.values()[0];
        for (LiftLevel liftLevel : LiftLevel.values) {
            double midpoint = (liftLevel.height + liftLevel.next().height) / 2;
            convertedLiftLevel = liftLevel;
            // OHHHHH WE'RE HALFWAY THERE! WHOAWHOA then move on to the next LiftLevel and try
            // again; if not, break out of the loop and return the current liftPosition the for loop
            // is on
            if (height < midpoint) {
                break;
            }
        }
        return convertedLiftLevel;
    }

    public void enablePID() {
        if (!pidController.isEnabled()) {
            pidController.enable();
        }
    }

    public void disablePID() {
        if (pidController.isEnabled()) {
            pidController.disable();
        }
    }

    public void resetEncoder() {
        liftEncoder.reset();
    }

    class LiftPIDSource extends AbstractPIDSource {

        public LiftPIDSource(PIDSourceType sourceType) {
            super(sourceType);
        }

        @Override
        public double pidGet() {
            switch (type) {
                case kDisplacement:
                    return getCurrentHeight();
                case kRate:
                    return liftEncoder.getRate();
                default:
                    return 0.0;
            }
        }
    }
}
