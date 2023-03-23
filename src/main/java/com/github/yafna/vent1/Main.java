package com.github.yafna.vent1;

import com.github.yafna.vent1.relay.Relay;
import org.slf4j.LoggerFactory;

public class Main {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
       try( Relay relay = new Relay()) {
           Thread.sleep(1000);
           relay.setOnLevel(1);
           Thread.sleep(2000);
           relay.setOnLevel(2);
           Thread.sleep(2000);
           relay.setOnLevel(4);
           Thread.sleep(2000);
           relay.setOnLevel(6);
           Thread.sleep(2000);
           relay.turnOff();
       } catch (Exception e) {
           e.printStackTrace();
       }
//        try (DHT11 dht = new DHT11(8)) {
//            for (int i = 0; i < 10; i++) {
//                Thread.sleep(5000);
//                logger.warn("{}, ttt {}", i, dht.getTemperature());
//                logger.warn("{}, hhh {}", i, dht.getHumidity());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getLocalizedMessage(), e);
//        }
    }
}
