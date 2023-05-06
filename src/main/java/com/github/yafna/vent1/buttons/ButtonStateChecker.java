package com.github.yafna.vent1.buttons;

import com.github.yafna.vent1.dto.ButtonMatrix;
import com.github.yafna.vent1.dto.MButtonListener;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ButtonStateChecker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ButtonStateChecker.class);
    private final GpioPinDigitalOutput l2;
    private final GpioPinDigitalOutput l1;
    private final GpioPinDigitalInput r1;
    private final GpioPinDigitalInput r2;
    private int state = -1;

    private List<MButtonListener> listeners = new ArrayList<>();

    public ButtonStateChecker(GpioPinDigitalOutput l2, GpioPinDigitalOutput l1, GpioPinDigitalInput r1, GpioPinDigitalInput r2) {
        this.l2 = l2;
        this.l1 = l1;
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public void run() {
        int checkedState = Math.max(readLine(l1, new Integer[]{1, 3}), readLine(l2, new Integer[]{2, 4}));
        if (checkedState > -1) {
            stateArrived(checkedState);
        }
        state = checkedState;
    }

    private Integer readLine(GpioPinDigitalOutput pin, Integer[] ints) {
        pin.high();
        int res = -1;
        if (r1.getState().isHigh()) {
            res = ints[0];
            logger.info(" button pressed " + ints[0]);
        }
        if (r2.getState().isHigh()) {
            res = ints[1];
            logger.info("button pressed " + ints[1]);
        }
        pin.low();
        return res;
    }

    // pressed new button or pressed same button after some checks
    private void stateArrived(int newState) {
        if (state != newState) {
            state = newState;
            listeners.forEach(listener -> listener.buttonClicked(ButtonMatrix.getByInt(newState)));
        }
    }

    public void addListener(MButtonListener listener) {
        synchronized (this) {
            listeners.add(listener);
            logger.debug("Added listener " + listener.getClass());
        }
    }

    public void removeListener(MButtonListener listener) {
        synchronized (this) {
            if (!listeners.contains(listener)) {
                listeners.remove(listener);
                logger.debug("Removed listener " + listener.getClass());
            } else {
                logger.warn("Listener has not been found, can not remove : " + listener.getClass());
            }
        }
    }
}
