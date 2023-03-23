package com.github.yafna.vent1;

//import com.pi4j.context.Context;
//import com.pi4j.io.gpio.digital.DigitalOutput;
//import com.pi4j.io.gpio.digital.DigitalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Relay {
//    private static final Logger logger = LoggerFactory.getLogger(Relay.class);
//    private final Context pi4j;
//    private final int[] bcms = {5, 6, 13, 19, 26, 21};
//    private List<DigitalOutput> relay = new ArrayList<>();
//
//    public Relay(Context pi4j) {
//        this.pi4j = pi4j;
//        init();
//    }
//
//    private void init() {
//        Arrays.stream(bcms).forEach(bcm -> {
//            var output = pi4j.dout().create(bcm);
//            output.config().shutdownState(DigitalState.HIGH);
//            output.addListener((s) -> logger.debug(String.valueOf(s)));
//            relay.add(output);
//        });
//    }
//
//    public void turnOff() {
//        for (int i = relay.size() - 1; i >= 0; --i) {
//            relay.get(i).state(DigitalState.LOW);
//        }
//    }
//
//    public void setOnLevel(int level) {
//        if (level > relay.size()) {
//            logger.warn("level if higher then number of relays available");
//            level = relay.size();
//        }
//        turnOff();
//        for (int i = 0; i < level; ++i) {
//            relay.get(i).state(DigitalState.HIGH);
//        }
//    }
}
