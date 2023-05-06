package com.github.yafna.vent1.i2c.pressure;

import com.github.yafna.vent1.dto.AtomicFloat;
import com.github.yafna.vent1.humidity.DhtQuery;
import com.github.yafna.vent1.i2c.IO;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DpsQuery implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DhtQuery.class);
    private final IO io;
    float tempOversampling;
    float prsOversampling;
    private final float tempCoef0Half;
    private final int tempCoef1;

    private final long prsC00;
    private final long prsC10;
    private final long prsC01;
    private final long prsC11;
    private final long prsC20;
    private final long prsC21;
    private final long prsC30;
    private final AtomicFloat temperature = new AtomicFloat((float) -1.0);
    private final AtomicFloat pressure = new AtomicFloat((float) -1.0);

    public DpsQuery(IO io, double tempOversampling, double prsOversampling, double tempCoef0Half, int tempCoef1,
                    long prsC00, long prsC10, long prsC01, long prsC11, long prsC20, long prsC21, long prsC30) {
        this.io = io;
        this.tempOversampling = (float) tempOversampling;
        this.prsOversampling = (float) prsOversampling;
        this.tempCoef0Half = (float) tempCoef0Half;
        this.tempCoef1 = tempCoef1;
        this.prsC00 = prsC00;
        this.prsC10 = prsC10;
        this.prsC01 = prsC01;
        this.prsC11 = prsC11;
        this.prsC20 = prsC20;
        this.prsC21 = prsC21;
        this.prsC30 = prsC30;
    }

    @Override
    public void run() {
        try {
            io.write(DPS310.MEAS_CFG, 0x02);
            int[] t = new int[3];
            io.write(0x03);
            for (int i = 0; i < t.length; i++) {
                t[i] = io.read();
            }
            long temRaw = complement(t[0] << 16 | t[1] << 8 | t[2], 24);
            float tempRawSc = temRaw / tempOversampling;
            float tempValue = tempCoef0Half + tempCoef1 * tempRawSc;
            temperature.set(tempValue);
            log.debug("dps {} temperature = {} ", io.getDeviceAddress(), tempValue);

            io.write(DPS310.MEAS_CFG, 0x01);
            Thread.sleep(500);
            io.write((byte) 0x00);
            for (int i = 0; i < t.length; i++) {
                t[i] = io.read();
            }
            int prsRaw = complement(t[0] << 16 | t[1] << 8 | t[2], 24);
            float prsRawSc = (float) (prsRaw / prsOversampling);
            float prsValue = prsC00 + prsRawSc * (prsC10 + prsRawSc * (prsC20 + prsRawSc * prsC30)) + tempRawSc * prsC01 + tempRawSc * prsRawSc * (prsC11 + prsRawSc * prsC21);
            pressure.set(prsValue);
            log.debug("dps {} pressure  = {}", io.getDeviceAddress(), prsValue);
        } catch (IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public Float getTemperature() {
        return temperature.get();
    }

    public Float getPressure() {
        return pressure.get();
    }

    private int complement(int raw, int length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }
}
