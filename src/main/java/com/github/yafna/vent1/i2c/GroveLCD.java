package com.github.yafna.vent1.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public class GroveLCD implements AutoCloseable {
    public static final int DISPLAY_RGB_ADDR = 0x62;
    public static final int DISPLAY_TEXT_ADDR = 0x3e;
    private static final int LCD_COMMAND = 0x80;
    private static final int LCD_WRITECHAR = 0x40;
    private static final int LCD_CMD_CLEARDISPLAY = 0x01;
    private static final int LCD_CMD_NEWLINE = 0xc0;
    private static final int REG_RED = 0x04;
    private static final int REG_GREEN = 0x03;
    private static final int REG_BLUE = 0x02;
    private final I2CBus bus;
    private final IO rgb;
    private final IO text;

    public GroveLCD(I2CBus bus) throws IOException, InterruptedException {
        this.bus = bus;
        I2CDevice rgbDevice = bus.getDevice(DISPLAY_RGB_ADDR);
        I2CDevice textDevice = bus.getDevice(DISPLAY_TEXT_ADDR);
        rgb = new IO(rgbDevice);
        text = new IO(textDevice);
        init();
    }

    private void init() throws IOException, InterruptedException {
        rgb.write(0, 0);
        rgb.write(1, 0);
        rgb.write(0x08, 0xaa);
        text.write(LCD_COMMAND, 0x08 | 0x04);
        text.write(LCD_COMMAND, 0x28);
        text.write(LCD_COMMAND, LCD_CMD_CLEARDISPLAY);
        Thread.sleep(50);
    }

    public void clearDisplay() throws IOException, InterruptedException {
        setRGB(0, 0, 0);
        text.write(LCD_COMMAND, LCD_CMD_CLEARDISPLAY);
    }

    public void setRGB(int r, int g, int b) throws IOException, InterruptedException {
        rgb.write(REG_RED, r);
        rgb.write(REG_GREEN, g);
        rgb.write(REG_BLUE, b);
        Thread.sleep(50);
    }

    public void setText(String textStr) throws IOException, InterruptedException {
        text.write(LCD_COMMAND, LCD_CMD_CLEARDISPLAY); // clear Display
        Thread.sleep(50);
        int count = 0;
        int row = 0;
        for (char c : textStr.toCharArray()) {
            if (c == '\n' || count == 16) {
                count = 0;
                row += 1;
                if (row == 2) {
                    break;
                }
                text.write(LCD_COMMAND, LCD_CMD_NEWLINE); // new line
                if (c == '\n') {
                    continue;
                }
            }
            count++;
            text.write(LCD_WRITECHAR, c); // Write character
        }
        Thread.sleep(50);
    }

    @Override
    public void close() throws Exception {
        clearDisplay();
    }
}
