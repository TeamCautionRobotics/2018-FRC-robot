package com.teamcautionrobotics.autonomous;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class MissionSendable implements NamedSendable {

    private ITable table;
    private String name;

    private Supplier<Mission> missionSupplier;

    private boolean running = false;
    private boolean initialized = false;
    private boolean finished = true;
    private Mission selectedMission;

    public MissionSendable(String name, Supplier<Mission> selectedMissionSupplier) {
        this.name = name;
        missionSupplier = selectedMissionSupplier;
    }

    public boolean run() {
        if (running) {
            if (!initialized) {
                selectedMission = missionSupplier.get();
                if (selectedMission != null) {
                    selectedMission.reset();
                    System.out.format("Teleop mission '%s' Started%n", selectedMission.getName());
                    initialized = true;
                    finished = false;
                    return running;
                } else {
                    running = false;
                    table.putBoolean("running", false);
                }
            }

            finished = selectedMission.run();

            if (finished) {
                running = false;
                initialized = false;
                table.putBoolean("running", false);
                System.out.format("Teleop mission '%s' Completed Successfully%n", selectedMission.getName());
            }
        } else if (!finished) {
            // Not running and not finished: were were cancelled
            System.out.format("Teleop mission '%s' Cancelled%n", selectedMission.getName());
            initialized = false;
            finished = true;
        }
        return running;
    }


    private final ITableListener listener = (table, key, value, isNew) -> {
        running = (boolean) value;
    };

    @Override
    public void initTable(ITable subtable) {
        if (table != null) {
            table.removeTableListener(listener);
        }

        table = subtable;
        if (table != null) {
        table.putString("name", name);
        table.putBoolean("running", false);
        table.addTableListener("running", listener, false);
        } else {
            System.err.println("MissionSendable initTable passed null ITable!");
        }
    }

    @Override
    public ITable getTable() {
        return table;
    }

    @Override
    public String getSmartDashboardType() {
        return "Command";
    }

    @Override
    public String getName() {
        return name;
    }

}
