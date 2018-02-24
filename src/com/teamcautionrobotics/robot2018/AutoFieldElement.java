package com.teamcautionrobotics.robot2018;

public enum AutoFieldElement {
    SWITCH("switch"), SCALE("scale"), AUTO_LINE("auto line"), DO_NOTHING("do nothing");

    public String name;

    private AutoFieldElement(String name) {
        this.name = name;
    }
}
