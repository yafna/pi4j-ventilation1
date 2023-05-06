package com.github.yafna.vent1.i2c.pressure;

import com.github.yafna.vent1.i2c.IO;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DPS310 implements AutoCloseable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DPS310.class);
    public static int DPS310_ADDRESS_1 = 0x77;
    public static int DPS310_ADDRESS_2 = 0x76;
    public static int MEAS_CFG = 0x08;
    int MEAS_CFG_TMP_RDY = 0x20;
    int FIFO_STS = 0x0B;
    int INT_STS = 0x0A;
    int TMP_CFG = 0x07;
    int CFG_REG = 0x09;
    int RESET = 0x0C;

    private long prsC00;
    private long prsC10;
    private long prsC01;
    private long prsC11;
    private long prsC20;
    private long prsC21;
    private long prsC30;

    private double tempCoef0Half = 0.0;
    private int tempCoef1 = 0;
    private double tempRawSc = 0.0;
    private Map<Integer, Map.Entry<Double, String>> oversampling = new HashMap<Integer, Map.Entry<Double, String>>() {{
        put(1, new SimpleImmutableEntry<>(524288.0, "0000")); // 1  single
        put(2, new SimpleImmutableEntry<>(1572864.0, "0001"));  // 2 times
        put(4, new SimpleImmutableEntry<>(3670016.0, "0010"));  // 4 times
        put(8, new SimpleImmutableEntry<>(7864320.0, "0011"));  // 8 times
        put(16, new SimpleImmutableEntry<>(253952.0, "0100")); // 16 times  - must be used with the bit shift config in the corresponded field in the 0x09
        put(32, new SimpleImmutableEntry<>(516096.0, "0101"));   //32 - must be used with the bit shift config in the corresponded field in the 0x09
        put(64, new SimpleImmutableEntry<>(1040384.0, "0110"));  //64 - must be used with the bit shift config in the corresponded field in the 0x09
        put(128, new SimpleImmutableEntry<>(2088960.0, "0111"));  //128  - must be used with the bit shift config in the corresponded field in the 0x09
    }};
    private Map.Entry<Double, String> tempOversampling;
    private Map.Entry<Double, String> prsOversampling;
    private IO io;
    private long[] coef;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private DpsQuery updater;
    private boolean internalSensor;

    public DPS310(I2CBus i2c, int address) throws IOException {
        I2CDevice device = i2c.getDevice(address);
        io = new IO(device);
        init();
        run();
    }

    private void init() {
        try {
            //do not start until rdy
            int check = 0;
            while (check < 192) {  //192
                Thread.sleep(500);
                io.write(MEAS_CFG);
                check = io.read();
                log.warn("check = " + Integer.toString(check, 2));
            }

            io.write(0x28);
            internalSensor = Integer.toString(io.read(), 2).charAt(0) == '0';

            tempOversampling = oversampling.get(16);
            prsOversampling = oversampling.get(16);
            io.write(0x06, Integer.parseInt("0" + "010" + prsOversampling.getValue(), 2));
            io.write(0x07, Integer.parseInt("" + (internalSensor ? 0 : 1) + "010" + tempOversampling.getValue(), 2));
            io.write(TMP_CFG);
            io.write(CFG_REG, 0x0C);
            io.write(CFG_REG);
            io.write(MEAS_CFG);
            updateCoeff();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        updater = new DpsQuery(io, tempOversampling.getKey(), prsOversampling.getKey(), tempCoef0Half, tempCoef1,
                prsC00, prsC10, prsC01, prsC11, prsC20, prsC21, prsC30);
        scheduler.scheduleAtFixedRate(updater, 2, 1000, TimeUnit.MILLISECONDS);
    }

    // send reset command to sensor,
    // set up new oversampling,
    // read calibration coefficients,
    // run background reading

    // oversamplingKey :  default 16,
    // -1 is not to change this value
    public void stopAndRestart(int oversamplingKey) {
        try {
            scheduler.shutdownNow();
            io.write(RESET, 9);
            io.write(RESET, 0x80); // fifo flush
            if (oversampling.containsKey(oversamplingKey)) {
                tempOversampling = oversampling.get(oversamplingKey);
                prsOversampling = oversampling.get(oversamplingKey);
                io.write(0x06, Integer.parseInt("0" + "010" + prsOversampling.getValue(), 2));
                io.write(0x07, Integer.parseInt("" + (internalSensor ? 0 : 1) + "010" + tempOversampling.getValue(), 2));
                io.write(TMP_CFG);
                io.write(CFG_REG, 0x0C);
                io.write(CFG_REG);
                io.write(MEAS_CFG);
            }
            updateCoeff();
            run();
        } catch (IOException e) {
            log.error("Failed to reset and restart pressure collection , {} ", e.getLocalizedMessage(), e);
        }
    }

    private void updateCoeff() throws IOException {
        coef = new long[18];
        io.write(0x10);
        for (int i = 0; i < coef.length; i++) {
            coef[i] = io.read();
        }
        tempCoef0Half = complementl((coef[0] << 4) | (coef[1] >> 4), 12) / 2.0;
        tempCoef1 = (int) complementl((coef[1] & 0x0F) << 8 | coef[2], 12);
        prsC00 = complementl(coef[3] << 12 | coef[4] << 4 | (coef[5] >> 4) & 0x0F, 20);
        prsC10 = complementl((coef[5] & 0x0F) << 16 | coef[6] << 8 | coef[7], 20);
        prsC01 = complementl(coef[8] << 8 | coef[9], 16);
        prsC11 = complementl(coef[10] << 8 | coef[11], 16);
        prsC20 = complementl(coef[12] << 8 | coef[13], 16);
        prsC21 = complementl(coef[14] << 8 | coef[15], 16);
        prsC30 = complementl(coef[16] << 8 | coef[17], 16);
    }

    long complementl(long raw, long length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }

    public Float getTemperature() {
        return updater.getTemperature();
    }

    public Float getPressure() {
        return updater.getPressure();
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }
}
