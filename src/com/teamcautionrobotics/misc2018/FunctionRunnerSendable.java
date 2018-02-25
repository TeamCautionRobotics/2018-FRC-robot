package com.teamcautionrobotics.misc2018;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class FunctionRunnerSendable extends SendableBase implements Sendable {

    private boolean needsToRun;
    private final BooleanSupplier function;

    /**
     * 
     * @param name
     * @param function Returns true when complete.
     */
    public FunctionRunnerSendable(String name, BooleanSupplier function) {
        setName(name);
        this.function = function;
    }

    public void update() {
        if (needsToRun) {
            needsToRun = !function.getAsBoolean();
        }
    }


    @Override
    public void initSendable(SendableBuilder builder) {
      builder.setSmartDashboardType("Command");
      builder.addStringProperty(".name", this::getName, null);
      builder.addBooleanProperty("running", ()->needsToRun, (value)->needsToRun=value);
    }
}
