package com.github.yafna.vent1;

import com.github.yafna.raspberry.grovepi.GrovePi;
import com.github.yafna.raspberry.grovepi.devices.GroveTemperatureAndHumiditySensor;
import com.github.yafna.raspberry.grovepi.pi4j.GrovePi4J;
import com.github.yafna.vent1.humidity.DHT11;
import com.github.yafna.vent1.pressure.DPS310;
import com.github.yafna.vent1.relay.Relay;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
                if (Gpio.wiringPiSetup() == -1) {
            log.error(" ==>>  GPIO wiringPi SETUP FAILED");
            throw new IllegalStateException("setup failed");
        }
//        try( Relay relay = new Relay()) {
//            Thread.sleep(1000);
//            relay.setOnLevel(1);
//            Thread.sleep(2000);
//            relay.setOnLevel(2);
//            Thread.sleep(2000);
//            relay.setOnLevel(4);
//            Thread.sleep(2000);
//            relay.setOnLevel(6);
//            Thread.sleep(2000);
//            relay.turnOff();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        log.warn("dps310 loading ");
        try {
            DPS310 dps = new DPS310();
            dps.run();
        }catch (Exception e){
            e.printStackTrace();
        }
//        try {
//            DPS310 dps = new DPS310(RaspiPin.GPIO_09);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try {
//            DPS310 dps = new DPS310(RaspiPin.GPIO_00);
//        }catch (Exception e){
//            e.printStackTrace();
//        }try {
//            DPS310 dps = new DPS310(RaspiPin.GPIO_02);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        if (Gpio.wiringPiSetup() == -1) {
//            log.error(" ==>>  GPIO wiringPi SETUP FAILED");
//            throw new IllegalStateException("setup failed");
//        }
//        Pin wpi1 = RaspiPin.GPIO_07;
//        Pin wpi2 = RaspiPin.GPIO_30;
//        try (DHT11 dht = new DHT11(0)) {
//            for (int i = 0; i < 30; i++) {
//                Thread.sleep(5000);
//                log.warn("{}, ttt {}", i, dht.getTemperature());
//                log.warn("{}, hhh {}", i, dht.getHumidity());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getLocalizedMessage(), e);
//        }
    }
}
