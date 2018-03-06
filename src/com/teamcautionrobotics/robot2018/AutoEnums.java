package com.teamcautionrobotics.robot2018;

class AutoEnums {
    // Which task we want to complete in autonomous
    public enum AutoObjective {
        SWITCH("switch"), SCALE("scale"), SWITCH_OR_SCALE("automatically select switch, scale, or auto line"), AUTO_LINE("auto line"), DO_NOTHING("do nothing");

        public final String name;

        private AutoObjective(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // Which side of the plate is our alliance's color. From the driver's perspective.
    public enum PlateSide {
        LEFT, RIGHT
    }

    // Where the robot is starting. From the driver's perspective.
    public enum StartingPosition {
        LEFT, CENTER, RIGHT
    }
}