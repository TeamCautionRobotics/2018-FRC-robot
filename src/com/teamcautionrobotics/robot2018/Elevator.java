package com.teamcautionrobotics.robot2018;

import com.teamcautionrobotics.misc2018.AbstractPIDSource;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {

    public enum ElevatorLevel {
        GROUND(0), SWITCH(28), LOW_SCALE(56), HIGH_SCALE(62);

        private static ElevatorLevel[] values = values();

        public final double height;

        private ElevatorLevel(double height) {
            this.height = height;
        }

        public ElevatorLevel next() {
            if (this.ordinal() == values.length - 1) {
                return this;
            } else {
                return values[this.ordinal() + 1];
            }
        }

        public ElevatorLevel previous() {
            if (this.ordinal() == 0) {
                return this;
            } else {
                return values[this.ordinal() - 1];
            }
        }
    }

    private VictorSP elevatorMotor;
    private DigitalInput stageOneDown;
    private DigitalInput stageTwoDown;
    public Encoder elevatorEncoder;
    public PIDController pidController;

    public Elevator(int motorPort, int encoderChannelA, int encoderChannelB, int stageOneDownPort,
            int stageTwoDownPort, double Kp, double Ki, double Kd) {
        elevatorMotor = new VictorSP(motorPort);
        elevatorMotor.setInverted(true);
        stageOneDown = new DigitalInput(stageOneDownPort);
        stageTwoDown = new DigitalInput(stageTwoDownPort);
        elevatorEncoder = new Encoder(encoderChannelA, encoderChannelB);

        // Winch drum diameter is 1.25 inches
        elevatorEncoder.setDistancePerPulse((1.25 * Math.PI) / 1024);

        pidController = new PIDController(Kp, Ki, Kd, 0,
                new ElevatorPIDSource(PIDSourceType.kDisplacement), this::move);
        pidController.setOutputRange(-1, 1);
        // TODO: get from elevator levels or other better non magic place for number
        pidController.setInputRange(0, 62);
        pidController.setAbsoluteTolerance(3);
    }

    /**
     * Directly set the power of the motors. Probably best not to use this, but use
     * {@link #setDestinationLevel(ElevatorLevel)} or {@link #setDestinationHeight(double)} instead.
     * 
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        SmartDashboard.putNumber("elevator power", power);
        if (stageOneIsDown() && stageTwoIsDown()) {
            resetEncoder();
        }
        elevatorMotor.set(power);
    }

    public void setDestinationHeight(double height) {
        pidController.setSetpoint(height);
    }

    public void setDestinationLevel(ElevatorLevel destinationElevatorLevel) {
        setDestinationHeight(destinationElevatorLevel.height);
    }

    public double getCurrentHeight() {
        return elevatorEncoder.getDistance();
    }

    public ElevatorLevel getCurrentElevatorLevel() {
        return convertHeightToElevatorLevel(getCurrentHeight());
    }

    public double getDestinationHeight() {
        return pidController.getSetpoint();
    }

    public ElevatorLevel getDestinationElevatorLevel() {
        return convertHeightToElevatorLevel(getDestinationHeight());
    }

    public boolean atDestination() {
        if (getDestinationHeight() <= getCurrentHeight() + 1
                && getDestinationHeight() >= getCurrentHeight() - 1) {
            return true;
        }
        return false;
    }

    public static ElevatorLevel convertHeightToElevatorLevel(double height) {
        ElevatorLevel convertedElevatorLevel = ElevatorLevel.values()[0];
        for (ElevatorLevel elevatorLevel : ElevatorLevel.values) {
            double midpoint = (elevatorLevel.height + elevatorLevel.next().height) / 2;
            convertedElevatorLevel = elevatorLevel;
            // OHHHHH WE'RE HALFWAY THERE! WHOAWHOA then move on to the next ElevatorLevel and try
            // again; if not, break out of the loop and return the current ElevatorLevel the for loop
            // is on
            if (height < midpoint) {
                break;
            }
        }
        return convertedElevatorLevel;
    }

    public boolean stageOneIsDown() {
        return !stageOneDown.get();
    }

    public boolean stageTwoIsDown() {
        return !stageTwoDown.get();
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
        elevatorEncoder.reset();
    }

    class ElevatorPIDSource extends AbstractPIDSource {

        public ElevatorPIDSource(PIDSourceType sourceType) {
            super(sourceType);
        }

        @Override
        public double pidGet() {
            switch (type) {
                case kDisplacement:
                    return getCurrentHeight();
                case kRate:
                    return elevatorEncoder.getRate();
                default:
                    return 0.0;
            }
        }
    }
}
