package com.github.yafna.vent1.i2c;

import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class IO {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IO.class);
    private final I2CDevice device;

    public IO(I2CDevice device) {
        this.device = device;
    }

    public int getDeviceAddress() {
        return device.getAddress();
    }

    public byte[] read(byte[] buffer) throws IOException {
        device.read(buffer, 0, buffer.length);
        log.info("[Pi4J I2C read]{} {}", device.getAddress(), Arrays.toString(buffer));
        return buffer;
    }

    public int read() throws IOException {
        final int read = device.read();
        log.info("[Pi4J I2C read]{} {}", device.getAddress(), read);
        return read;
    }

    public void write(int... command) throws IOException {
        byte[] buffer = new byte[command.length];
        for (int i = 0; i < command.length; i++) {
            buffer[i] = (byte) command[i];
        }
        log.info("[Pi4J I2C write]{} {}", device.getAddress(), Arrays.toString(buffer));
        device.write(buffer, 0, command.length);
    }

}
