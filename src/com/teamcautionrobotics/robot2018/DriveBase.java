package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase implements PIDOutput, PIDSource {

    private VictorSP driveLeft;
    private VictorSP driveRight;

    private Encoder leftEncoder;
    private Encoder rightEncoder;

    private ADXRS450_Gyro gyro;

    private double heading;
    public double courseHeading;

    public PIDController pidController;

    public DriveBase(int left, int right, int shifterChannel, int leftA, int leftB, int rightA,
            int rightB) {
        driveLeft = new VictorSP(left);
        driveRight = new VictorSP(right);

        leftEncoder = new Encoder(leftA, leftB, false, EncodingType.k4X);
        rightEncoder = new Encoder(rightA, rightB, true, EncodingType.k4X);

        leftEncoder.setDistancePerPulse((4 * Math.PI) / 1024);
        rightEncoder.setDistancePerPulse((4 * Math.PI) / 1024);

        pidController = new PIDController(0.04, 0, 0.1, 0, this, this);
        pidController.setOutputRange(-1, 1);
        pidController.setAbsoluteTolerance(3);

        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        heading = gyro.getAngle();
        courseHeading = heading;
    }

    public void drive(double left, double right) {
        driveLeft.set(left);
        driveRight.set(-right);
    }

    public void drive(double speed) {
        drive(speed, speed);
    }

    public void resetGyro() {
        gyro.reset();
    }

    public double getGyroAngle() {
        return gyro.getAngle();
    }

    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    public double getDistance() {
        return getRightDistance();
    }

    public double getRightDistance() {
        return rightEncoder.getDistance();
    }

    public double getRightSpeed() {
        return rightEncoder.getRate();
    }

    public double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    public double getLeftSpeed() {
        return leftEncoder.getRate();
    }


    public void pidInit() {
        heading = getGyroAngle();
        courseHeading = heading;
    }

    @Override
    public void pidWrite(double speed) {
        SmartDashboard.putNumber("pid drive speed", speed);
        double angle = heading - getGyroAngle();
        drive(speed, speed - angle * 0.03);
    }


    @Override
    /**
     * Not implemented. Always displacment pid source.
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
