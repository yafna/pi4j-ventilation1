package com.github.yafna.vent1.pressure;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.wiringpi.GpioUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Pressure {
    int MEAS_CFG = 0x08;
    int MEAS_CFG_TMP_RDY = 0x20;
    int FIFO_STS = 0x0B;
    int INT_STS = 0x0A;
    int TMP_CFG = 0x07;
    int CFG_REG = 0x09;
    int RESET = 0x0C;

    long prsC00;
    long prsC10;
    long prsC01;
    long prsC11;
    long prsC20;
    long prsC21;
    long prsC30;

    double tempCoef0Half = 0.0;
    int tempCoef1 = 0;
    double tempRawSc = 0.0;
    Map<Integer, Map.Entry<Double, String>> oversampling = new HashMap<Integer, Map.Entry<Double, String>>() {{
        put(1, new SimpleImmutableEntry<>(524288.0, "0000")); // 1  single
        put(2, new SimpleImmutableEntry<>(1572864.0, "0001"));  // 2 times
        put(4, new SimpleImmutableEntry<>(3670016.0, "0010"));  // 4 times
        put(8, new SimpleImmutableEntry<>(7864320.0, "0011"));  // 8 times
        put(16, new SimpleImmutableEntry<>(253952.0, "0100")); // 16 times  - must be used with the bit shift config in the corresponded field in the 0x09
        put(32, new SimpleImmutableEntry<>(516096.0, "0101"));   //32 - must be used with the bit shift config in the corresponded field in the 0x09
        put(64, new SimpleImmutableEntry<>(1040384.0, "0110"));  //64 - must be used with the bit shift config in the corresponded field in the 0x09
        put(128, new SimpleImmutableEntry<>(2088960.0, "0111"));  //128  - must be used with the bit shift config in the corresponded field in the 0x09
    }};
    Map.Entry<Double, String> tempOversampling;
    Map.Entry<Double, String> prsOversampling;

    public void run() {
        try (Scanner in = new Scanner(System.in)) {
            GpioUtil.export(pin, GpioUtil.DIRECTION_OUT);
            I2CBus bus = null;
            I2CDevice dev = null;
            try {
                bus = I2CFactory.getInstance(1);
                dev = bus.getDevice(0x77);
                IO io = new IO(dev);
                //do not start until rdy
                int check = 0;
                while (check < 192) {  //192
                    Thread.sleep(500);
                    io.write(MEAS_CFG);
                    check = io.read();
                    System.out.println("check = " + Integer.toString(check, 2));
                }

                io.write(0x28);
                int sensorType = io.read();
                boolean internalSensor = Integer.toString(sensorType, 2).charAt(0) == '0';
                System.out.println("internalSensor = " + internalSensor);

                tempOversampling = oversampling.get(16);
                prsOversampling = oversampling.get(16);
                String s = "";
                while (!("exit").equalsIgnoreCase(s)) {
                    s = in.nextLine();
                    if ("9".equalsIgnoreCase(s)) {
                        io.write(0x06, Integer.parseInt("0" + "010" + prsOversampling.getValue(), 2));
                        io.write(0x07, Integer.parseInt("" + (internalSensor ? 0 : 1) + "010" + tempOversampling.getValue(), 2));
                        io.write(TMP_CFG);
                        System.out.println("temp check = " + Integer.toString(io.read(), 2));
                        io.write(CFG_REG, 0x0C);
                        io.write(CFG_REG);
                        System.out.println(" check = " + Integer.toString(io.read(), 2));
                    }
                    if ("0".equalsIgnoreCase(s)) {
                        io.write(INT_STS);
                        System.out.println("int = " + Integer.toString(io.read(), 2));
                    }
                    if ("1".equalsIgnoreCase(s)) {
                        io.write(MEAS_CFG);
                        int t = io.read();
                        System.out.println("check = " + ((t & MEAS_CFG_TMP_RDY) > 0) + "   " + Integer.toString(t, 2));
                    }
                    if ("2".equalsIgnoreCase(s)) {
                        io.write(FIFO_STS);
                        System.out.println("fifo = " + Integer.toString(io.read(), 2));
                    }
                    if ("3".equalsIgnoreCase(s)) {
                        io.write(MEAS_CFG, 0x02);
                        int[] t = new int[3];
                        io.write(0x03);
                        for (int i = 0; i < t.length; i++) {
                            t[i] = io.read();
                        }
                        int temRaw = complement(t[0] << 16 | t[1] << 8 | t[2], 24);
                        System.out.println("temRaw = " + temRaw);
                        tempRawSc = temRaw / tempOversampling.getKey();
                        double tempValue = tempCoef0Half + tempCoef1 * tempRawSc;
                        System.out.println("temperature  = " + tempValue);

                        System.out.println(" =----------------------------------------- ");
                        io.write(MEAS_CFG, 0x01);
                        Thread.sleep(1000);
                        io.write(0x00);
                        for (int i = 0; i < t.length; i++) {
                            t[i] = io.read();
                            System.out.println(" " + t[i] + " " + Integer.toString(t[i], 2));
                        }
                        int prsRaw = complement(t[0] << 16 | t[1] << 8 | t[2], 24);
                        double prsRawSc = prsRaw / prsOversampling.getKey();

                        double prsValue  = prsC00 + prsRawSc*(prsC10  + prsRawSc*(prsC20 + prsRawSc*prsC30)) + tempRawSc * prsC01 + tempRawSc*prsRawSc*(prsC11 + prsRawSc *prsC21);
                        System.out.println("pressure  = " + prsValue);
                    }
                    if ("5".equalsIgnoreCase(s)) {
                        io.write(RESET, 0x80);
                    }
                    if ("50".equalsIgnoreCase(s)) { //soft reset
                        io.write(RESET, 9);
                    }

                    if ("6".equalsIgnoreCase(s)) {
                        int[] coef = new int[18];
                        io.write(0x10);
                        for (int i = 0; i < coef.length; i++) {
                            coef[i] = io.read();
                            System.out.println("coef[i] = " + coef[i] + "   " + Integer.toString(coef[i], 2));
                        }
                        tempCoef0Half = complement((coef[0] << 4) | (coef[1] >> 4), 12) / 2.0;
                        System.out.println("m_c0Half = " + tempCoef0Half);
                        tempCoef1 = complement((coef[1] & 0x0F) << 8 | coef[2], 12);
                        System.out.println("m_c1  = " + tempCoef1);

                        prsC00 = complement(coef[3] << 12 | coef[4] << 4 | (coef[5] >> 4 ) & 0x0F, 20);
                        prsC10 = complement((coef[5] & 0x0F) << 16 | coef[6] << 8 | coef[7], 20);
                        prsC01 = complement(coef[8] << 8 | coef[9], 16);
                        prsC11 = complement(coef[10] << 8 | coef[11], 16);
                        prsC20 = complement(coef[12] << 8 | coef[13], 16);
                        prsC21 = complement(coef[14] << 8 | coef[15], 16);
                        prsC30 = complement(coef[16] << 8 | coef[17], 16);
                    }
                }
            } catch (InterruptedException | IOException | I2CFactory.UnsupportedBusNumberException e) {
                e.printStackTrace();
            } finally {
                try {
                    bus.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    long complement(long raw, long length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }

    int complement(int raw, int length) {
        if (raw > (1 << (length - 1))) {
            raw -= 1 << length;
        }
        return raw;
    }
}
