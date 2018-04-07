package com.teamcautionrobotics.autonomous2018;

import com.teamcautionrobotics.autonomous.Command;
import com.teamcautionrobotics.robot2018.DriveBase;

public class SetDriveMotorsCommand implements Command {

    private final double motorPower;
    private final DriveBase driveBase;

    public SetDriveMotorsCommand(DriveBase driveBase, double power) {
        this.driveBase = driveBase;
        motorPower = power;
        reset();
    }

    @Override
    public boolean run() {
        driveBase.drive(motorPower);
        return true;
    }

    @Override
    public void reset() {}

}
