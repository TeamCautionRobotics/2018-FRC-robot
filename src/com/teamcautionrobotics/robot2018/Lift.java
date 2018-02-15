package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
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

    public void ascend() {
        this.move(1.0);
    }

    public void descend() {
        this.move(-1.0);
    }

    public void stop() {
        this.move(0);
    }

    public void setLevel(LiftLevel destinationLiftLevel) {
        destinationHeight = destinationLiftLevel.height;
        pidController.setSetpoint(destinationHeight);
        enablePID();
    }

    public void setHeight(double height) {
        pidController.setSetpoint(height);
        enablePID();
    }

    public LiftLevel getCurrentLiftLevel() {
        return convertHeightToLiftLevel(getCurrentHeight());
    }

    public double getCurrentHeight() {
        return getDistance();
    }

    public double getDestinationHeight() {
        return pidController.getSetpoint();
    }

    public LiftLevel getDestinationLiftLevel() {
        return convertHeightToLiftLevel(getDestinationHeight());
    }

    private LiftLevel convertHeightToLiftLevel(double height) {
        LiftLevel convertedLiftLevel = LiftLevel.GROUND;
        for (LiftLevel liftLevel : LiftLevel.values) {
            double midpoint = (liftLevel.height + liftLevel.next().height) / 2;
            convertedLiftLevel = liftLevel;
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

    public double getDistance() {
        return liftEncoder.getDistance();
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
        return getDistance();
    }
}
