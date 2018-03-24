package com.teamcautionrobotics.autonomous2018.commands;


import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

import edu.wpi.first.wpilibj.DriverStation;

public class CheckDriveEncodersCommand implements Command {

    private DriveBase driveBase;

    private final double expectedDistance;

    public CheckDriveEncodersCommand(DriveBase driveBase, double expectedDistance) {
        this.driveBase = driveBase;

        this.expectedDistance = expectedDistance;

        reset();
    }

    @Override
    public boolean run() {
        double rightDistance = driveBase.getRightDistance();
        double leftDistance = driveBase.getLeftDistance();

        DriverStation.reportWarning(
                String.format("Check encoders command: expected distance: %f%n", expectedDistance),
                false);
        DriverStation.reportWarning(
                String.format("Check encoders command: left distance: %f, right distance: %f%n",
                        leftDistance, rightDistance),
                false);

        if (Math.abs(rightDistance) < expectedDistance) {
            driveBase.setUseLeftEncoder(true);
            DriverStation.reportWarning(
                    "Check encoders command: distance less than expected, using left", false);
        }

        return true;
    }

    @Override
    public void reset() {}

}
