package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class Lift implements PIDSource {

    enum LiftLevel {
        GROUND(0), SWITCH(19), LOW_SCALE(48), HIGH_SCALE(76);

        private static LiftLevel[] values = values();

        double height;

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
    private Encoder liftEncoder;
    private PIDController pidController;

    private double destinationHeight;

    public Lift(int motorPort, int encoderChannelA, int encoderChannelB, double Kp, double Ki,
            double Kd) {
        liftMotor = new VictorSP(motorPort);
        liftEncoder = new Encoder(encoderChannelA, encoderChannelB);
        liftEncoder.setDistancePerPulse((4 * Math.PI) / 1024);
        pidController = new PIDController(Kp, Ki, Kd, 0, this, this::move);
        pidController.setOutputRange(-1, 1);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        liftMotor.set(power);
    }

    public void stop() {
        this.move(0);
    }

    public void setLevel(LiftLevel destinationLiftLevel) {
        setHeight(destinationLiftLevel.height);
    }

    public void setHeight(double height) {
        pidController.setSetpoint(height);
        enablePID();
    }

    public LiftLevel getCurrentLiftLevel() {
        return convertHeightToLiftLevel(getCurrentHeight());
    }

    public double getCurrentHeight() {
        return liftEncoder.getDistance();
    }

    public double getDestinationHeight() {
        return pidController.getSetpoint();
    }

    public LiftLevel getDestinationLiftLevel() {
        return convertHeightToLiftLevel(getDestinationHeight());
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

    class LiftPIDSource implements PIDSource {

        private PIDSourceType type;

        public LiftPIDSource(PIDSourceType sourceType) {
            type = sourceType;
        }

        @Override
        public void setPIDSourceType(PIDSourceType sourceType) {
            type = sourceType;
        }

        @Override
        public PIDSourceType getPIDSourceType() {
            return type;
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

    @Override
    /**
     * Not implemented. Always displacement pid source.
     * 
     * @see edu.wpi.first.wpilibj.PIDSource#setPIDSourceType(edu.wpi.first.wpilibj.PIDSourceType)
     */
    public void setPIDSourceType(PIDSourceType pidSource) {

    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet() {
        return getCurrentHeight();
    }
}
