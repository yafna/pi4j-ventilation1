package com.github.yafna.vent1.humidity;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DHT11 implements AutoCloseable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DHT11.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final DhtQuery updater;

    public DHT11(int pin) {
        if (Gpio.wiringPiSetup() == -1) {
            log.error(" ==>>  GPIO wiringPi SETUP FAILED");
            throw new IllegalStateException("DHT11 setup failed");
        }
        GpioUtil.export(pin, GpioUtil.DIRECTION_OUT);
        updater = new DhtQuery(pin);
        scheduler.scheduleAtFixedRate(updater, 2, 1000, TimeUnit.MILLISECONDS);
    }

    public Float getTemperature() {
        return updater.getTemperature();
    }

    public Float getHumidity() {
        return updater.getHumidity();
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}