/**
 * 
 */
package com.teamcautionrobotics.autonomous;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

public class MissionScriptMission extends Mission {
    Path missionScriptFile;

    CommandFactory commandFactory;

    public MissionScriptMission(String name, Path missionScriptFile,
            CommandFactory commandFactory, boolean enableControls) {
        super(name, enableControls);
        this.missionScriptFile = missionScriptFile;
        this.commandFactory = commandFactory;
    }


    public MissionScriptMission(String name, Path missionScriptFile, CommandFactory commandFactory) {
        this(name, missionScriptFile, commandFactory, false);
    }

    @Override
    public synchronized void reset() {
        step = commands.size();
        Mission parsedMission = null;
        try {
            parsedMission = MissionScript.parseMission(this.getName(),
                    Files.readAllLines(missionScriptFile), commandFactory);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        if (parsedMission != null) {
            commands = parsedMission.getCommands();
        }

        super.reset();
    }

    @Override
    public synchronized boolean run() {
        return super.run();
    }
}
