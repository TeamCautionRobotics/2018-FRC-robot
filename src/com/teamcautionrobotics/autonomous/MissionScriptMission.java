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

    // Here is where we should load and parse the MissionScript file
    @Override
    public void reset() {
        Mission parsedMission = null;
        try {
            parsedMission = MissionScript.parseMission(this.getName(),
                    Files.readAllLines(missionScriptFile), commandFactory);
        } catch (ParseException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (parsedMission != null) {
            // TODO: do something with the output of the parser
            // This could be getting its commands and loading them into ourself (this instance of
            // MissionScriptMission class) or it could be something else. Maybe keeping the output
            // as another Mission object that we run when it should be run).
            // If we load the commands into ourself, we (the programmers) may want to make
            // ArrayList<Command> commands from Mission protected, so we can access it directly.
        }

        // TODO: We then need to call reset to initialize all of the commands in the newly parsed
        // and loaded mission
        super.reset();
    }


    // TODO: We may not actually need to override the run method. Though, this depends on how we
    // store the parser output.
    @Override
    public boolean run() {
        return super.run();
    }
}
