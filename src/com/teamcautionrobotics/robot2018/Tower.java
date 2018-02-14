package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class Tower implements PIDOutput, PIDSource {

    enum TowerPosition {
        GROUND, SWITCH, LOW_SCALE, HIGH_SCALE;

        private static TowerPosition[] vals = values();

        public TowerPosition next() {
            if (this.ordinal() == vals.length - 1) {
                return vals[this.ordinal()];
            } else {
                return vals[this.ordinal() + 1];
            }
        }

        public TowerPosition previous() {
            if (this.ordinal() == 0) {
                return vals[this.ordinal()];
            } else {
                return vals[this.ordinal() - 1];
            }
        }
    }

    private TowerPosition currentTowerPosition;

    private VictorSP towerMotor;
    private Encoder towerEncoder;
    private PIDController pidController;

    private double desiredPosition;

    public Tower(int motorPort, int encoderChannelA, int encoderChannelB, double Kp, double Ki,
            double Kd) {
        towerMotor = new VictorSP(motorPort);
        towerEncoder = new Encoder(encoderChannelA, encoderChannelB);
        towerEncoder.setDistancePerPulse((4 * Math.PI) / 1024);
        pidController.setOutputRange(-1, 1);
        pidController.setAbsoluteTolerance(3);
        pidController = new PIDController(Kp, Ki, Kd, 0, this, this);
        pidController.setOutputRange(-1, 1);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        towerMotor.set(power);
        disablePID();
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

    public void setPosition(TowerPosition desiredTowerPosition) {
        switch (desiredTowerPosition) {
            case GROUND:
                desiredPosition = 0;
                break;
            case SWITCH:
                desiredPosition = 19;
                break;
            case LOW_SCALE:
                desiredPosition = 48;
                break;
            case HIGH_SCALE:
                desiredPosition = 76;
                break;
            default:
                System.err.println("towerPosition is not set to a normal value. Continuing.");
                break;
        }
        setCurrentTowerPosition();
        pidController.setSetpoint(desiredPosition);
        enablePID();
        currentTowerPosition = desiredTowerPosition;
    }

    public void setPosition(double position) {
        pidController.setSetpoint(position);
        enablePID();
    }

    public TowerPosition getCurrentTowerPosition() {
        return (currentTowerPosition);
    }

    public void setCurrentTowerPosition() {
        TowerPosition towerPosition;
        if (getDistance() < 9.5) {
            towerPosition = TowerPosition.GROUND;
        } else if (getDistance() >= 9.5 && getDistance() < (19 + 48) / 2) {
            towerPosition = TowerPosition.SWITCH;
        } else if (getDistance() >= (19 + 48) / 2 && getDistance() < (48 + 76) / 2) {
            towerPosition = TowerPosition.LOW_SCALE;
        } else {
            towerPosition = TowerPosition.HIGH_SCALE;
        }
        currentTowerPosition = towerPosition;
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
        towerEncoder.reset();
    }

    public double getDistance() {
        return towerEncoder.getDistance();
    }

    @Override
    public void pidWrite(double speed) {
        SmartDashboard.putNumber("pid drive speed", speed);
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
