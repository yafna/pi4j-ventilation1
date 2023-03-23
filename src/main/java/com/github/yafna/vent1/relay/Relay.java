package com.github.yafna.vent1.relay;

//import com.pi4j.context.Context;
//import com.pi4j.io.gpio.digital.DigitalOutput;
//import com.pi4j.io.gpio.digital.DigitalState;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.GpioUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Relay implements AutoCloseable{
    private static final Logger logger = LoggerFactory.getLogger(Relay.class);
    private final Pin[] wpis = {RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23, RaspiPin.GPIO_24, RaspiPin.GPIO_25, RaspiPin.GPIO_29};
    private final List<GpioPinDigitalOutput> relay = new ArrayList<>();

    public Relay() {
        logger.debug(" Relay set up start");
        GpioUtil.enableNonPrivilegedAccess();
        GpioController gpioRelayLED2 = GpioFactory.getInstance();
        Arrays.stream(wpis).forEach(wpi -> {
            GpioPinDigitalOutput item = gpioRelayLED2.provisionDigitalOutputPin(wpi, "Relay " + wpi.getName(), PinState.LOW);
            item.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
            item.high();
            relay.add(item);
        });
        logger.debug(" Relay set up finished");
    }

    public void turnOff() {
        logger.info(" Relay  turn off");
        for (int i = relay.size() - 1; i >= 0; --i) {
            relay.get(i).high();
        }
    }

    public void setOnLevel(int level) {
        logger.info(" Relay set on level {}", level);
        if (level > relay.size()) {
            logger.warn("level if higher then number of relays available");
            level = relay.size();
        }
        turnOff();
        for (int i = 0; i < level; ++i) {
            relay.get(i).low();
        }
    }

    @Override
    public void close() throws Exception {
        turnOff();
    }
}
