package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class Lift implements PIDOutput, PIDSource {

    enum LiftLevel {
        GROUND(0), SWITCH(19), LOW_SCALE(48), HIGH_SCALE(76);

        private static LiftLevel[] vals = values();

        double position;

        private LiftLevel(double position) {
            this.position = position;
        }

        public LiftLevel next() {
            if (this.ordinal() == vals.length - 1) {
                return vals[this.ordinal()];
            } else {
                return vals[this.ordinal() + 1];
            }
        }

        public LiftLevel previous() {
            if (this.ordinal() == 0) {
                return vals[this.ordinal()];
            } else {
                return vals[this.ordinal() - 1];
            }
        }
    }

    private LiftLevel currentLiftLevel;

    private VictorSP liftMotor;
    private Encoder liftEncoder;
    private PIDController pidController;

    private double desiredPosition;

    public Lift(int motorPort, int encoderChannelA, int encoderChannelB, double Kp, double Ki,
            double Kd) {
        liftMotor = new VictorSP(motorPort);
        liftEncoder = new Encoder(encoderChannelA, encoderChannelB);
        liftEncoder.setDistancePerPulse((4 * Math.PI) / 1024);
        pidController = new PIDController(Kp, Ki, Kd, 0, this, this);
        pidController.setOutputRange(-1, 1);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        disablePID();
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

    public void setLevel(LiftLevel desiredLiftLevel) {
        desiredPosition = desiredLiftLevel.position;
        setCurrentLiftLevel();
        pidController.setSetpoint(desiredPosition);
        enablePID();
        currentLiftLevel = desiredLiftLevel;
    }

    public void setPosition(double position) {
        pidController.setSetpoint(position);
        enablePID();
    }

    public LiftLevel getCurrentLiftLevel() {
        return (currentLiftLevel);
    }

    public void setCurrentLiftLevel() {
        LiftLevel liftLevel;
        if (getDistance() < 9.5) {
            liftLevel = LiftLevel.GROUND;
        } else if (getDistance() >= 9.5 && getDistance() < (19 + 48) / 2) {
            liftLevel = LiftLevel.SWITCH;
        } else if (getDistance() >= (19 + 48) / 2 && getDistance() < (48 + 76) / 2) {
            liftLevel = LiftLevel.LOW_SCALE;
        } else {
            liftLevel = LiftLevel.HIGH_SCALE;
        }
        currentLiftLevel = liftLevel;
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
    public void pidWrite(double position) {
        SmartDashboard.putNumber("pid lift position", position);
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
