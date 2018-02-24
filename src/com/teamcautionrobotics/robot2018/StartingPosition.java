package com.teamcautionrobotics.robot2018;

public enum StartingPosition {
    LEFT_POSITION("Left position"), CENTER_POSITION("Center position"),
            RIGHT_POSITION("Right pisition");

    public String name;

    private StartingPosition(String name) {
        this.name = name;
    }
}
