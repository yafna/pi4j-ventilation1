package com.github.yafna.vent1.dto;

public enum ButtonMatrix {
    BACK,
    ENTER,
    LEFT,
    RIGHT;

    public static ButtonMatrix getByInt(int num) {
        if (num == 1) {
            return BACK;
        }
        if (num == 2) {
            return ENTER;
        }
        if (num == 3) {
            return LEFT;
        }
        if (num == 4) {
            return RIGHT;
        }
        return null;
    }
}
