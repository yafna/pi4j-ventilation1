package com.github.yafna.vent1.buttons;

import com.github.yafna.vent1.dto.MButtonListener;
import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FourBlock implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(FourBlock.class);
    private final Pin[] wpis = {RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_06};
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private GpioPinDigitalOutput l2;
    private GpioPinDigitalOutput l1;
    private GpioPinDigitalInput r1;
    private GpioPinDigitalInput r2;
private ButtonStateChecker checker;
    public FourBlock(GpioController gpioController) {
        JButton btn = new JButton("ddd");
        btn.addActionListener(actionEvent -> {

        });
        logger.debug(" Button matrix set up start");
        l2 = gpioController.provisionDigitalOutputPin(wpis[3]);
        l1 = gpioController.provisionDigitalOutputPin(wpis[2]);

        r1 = gpioController.provisionDigitalInputPin(wpis[1], PinPullResistance.PULL_DOWN);
        r2 = gpioController.provisionDigitalInputPin(wpis[0], PinPullResistance.PULL_DOWN);
        logger.debug(" Button matrix set up finished");

        checker = new ButtonStateChecker(l2, l1, r1, r2);
        scheduler.scheduleAtFixedRate(checker, 1, 50, TimeUnit.MILLISECONDS);
    }

    public void addListener(MButtonListener listener) {
checker.addListener(listener);
    }

    public void removeListener(MButtonListener listener) {
        checker.removeListener(listener);
    }

    @Override
    public void close() throws Exception {
        scheduler.shutdown();
    }
}