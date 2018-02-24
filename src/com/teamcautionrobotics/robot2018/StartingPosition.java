package com.teamcautionrobotics.robot2018;

public enum StartingPosition {
    LEFT_POSITION("left position"), CENTER_POSITION("center position"),
            RIGHT_POSITION("right position");

    public String name;

    private StartingPosition(String name) {
        this.name = name;
    }
}
