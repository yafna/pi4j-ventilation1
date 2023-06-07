package com.github.yafna.vent1.dto;

public enum MenuItem {
    MAIN_MENU("MAIN_MENU"),
    TEMPERATURE("TEMPERATURE"),
    PRESSURE("PRESSURE"),
    RELAY("RELAY"),
    DISPLAY_COLORS("DISPLAY_COLORS"); //test item

    private String displayStr;

    MenuItem(String displayStr) {
        this.displayStr = displayStr;
    }

    public String getDisplayStr(){
        return displayStr;
    }
}
