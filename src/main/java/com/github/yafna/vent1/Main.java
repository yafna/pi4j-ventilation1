package com.github.yafna.vent1;

import com.github.yafna.vent1.humidity.DHT11;
import org.slf4j.LoggerFactory;

public class Main {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try (DHT11 dht = new DHT11(8)) {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(5000);
                logger.warn("{}, ttt {}", i, dht.getTemperature());
                logger.warn("{}, hhh {}", i, dht.getHumidity());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}
