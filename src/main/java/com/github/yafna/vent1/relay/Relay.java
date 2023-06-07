package com.github.yafna.vent1.relay;


import com.github.yafna.vent1.dto.ButtonMatrix;
import com.github.yafna.vent1.dto.MButtonListener;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Relay implements MButtonListener, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Relay.class);
    private final Pin[] wpis = {RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23, RaspiPin.GPIO_24, RaspiPin.GPIO_25, RaspiPin.GPIO_29};
    private final List<GpioPinDigitalOutput> relay = new ArrayList<>();

    public Relay(GpioController gpioRelayLED2) {
        logger.debug(" Relay set up start");
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

    public int getSumLevels() {
        int res = 0;
        for (GpioPinDigitalOutput item : relay) {
            res += item.isHigh() ? 0 : 1;
        }
        return res;
    }

    public boolean[] getLevels() {
        boolean[] res = new boolean[relay.size()];
        for (int i = 0; i < relay.size(); i++) {
            res[i] = relay.get(i).isHigh();
        }
        return res;
    }

    public void setOnLevel(int level) {
        if (level < 0) {
            logger.warn("wrong relay state , can not be negative : " + level);
        } else {
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
    }

    @Override
    public void close() throws Exception {
        turnOff();
    }

    @Override
    public void buttonClicked(ButtonMatrix type) {

    }
}
