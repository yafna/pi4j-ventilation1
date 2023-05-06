package com.github.yafna.vent1.humidity;

import com.github.yafna.vent1.dto.AtomicFloat;
import com.pi4j.wiringpi.Gpio;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class DhtQuery implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DhtQuery.class);
    private final int pin;
    private static final int MAXTIMINGS = 85;
    private final int[] dht11_dat = {0, 0, 0, 0, 0};
    private final AtomicFloat temperature = new AtomicFloat((float) -1.0);
    private final AtomicFloat humidity = new AtomicFloat((float) -1.0);
    private final AtomicInteger counter = new AtomicInteger(0);

    public DhtQuery(int pin) {
        this.pin = pin;
    }

    @Override
    public void run() {
        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pin, Gpio.HIGH);
        Gpio.pinMode(pin, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(pin) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(pin);

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                dht11_dat[j / 8] <<= 1;
                if (counter > 16) {
                    dht11_dat[j / 8] |= 1;
                }
                j++;
            }
        }
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
        if (j >= 40 && checkParity()) {
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (h > 100) {
                h = dht11_dat[0]; // for DHT11
            }
            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (c > 125) {
                c = dht11_dat[2]; // for DHT11
            }
            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }
            humidity.set(h);
            temperature.set(c);
            log.debug("DTH11 Humidity = " + h + " Temperature = " + c);
            counter.set(0);
        } else {
            log.info("DTH11 Data not good, skip , step {}", counter.incrementAndGet());
            if (counter.get() > 10000) {
                counter.set(100);
            }
        }
    }

    public Float getTemperature() {
        if (counter.get() < 50) {
            return temperature.get();
        } else {
            log.warn("DTH111 Data is outdated ");
            return null;
        }
    }

    public Float getHumidity() {
        if (counter.get() < 50) {
            return humidity.get();
        } else {
            log.warn("DTH111 Data is outdated ");
            return null;
        }
    }

    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

}
