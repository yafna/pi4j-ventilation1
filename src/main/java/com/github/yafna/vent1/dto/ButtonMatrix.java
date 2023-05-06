package com.github.yafna.vent1.dto;

public enum ButtonMatrix {
    S1,
    S2,
    S3,
    S4;

    public static ButtonMatrix getByInt(int num) {
        if (num == 1) {
            return S1;
        }
        if (num == 2) {
            return S2;
        }
        if (num == 3) {
            return S3;
        }
        if (num == 4) {
            return S4;
        }
        return null;
    }
}
