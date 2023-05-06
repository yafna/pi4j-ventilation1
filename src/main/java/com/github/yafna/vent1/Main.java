package com.github.yafna.vent1;

import com.github.yafna.vent1.buttons.FourBlock;
import com.github.yafna.vent1.i2c.GroveLCD;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);
    private static final int GROVEPI_ADDRESS = 4;

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
        if (Gpio.wiringPiSetup() == -1) {
            log.error(" ==>>  GPIO wiringPi SETUP FAILED");
            throw new IllegalStateException("setup failed");
        }
        Scanner scanner = new Scanner(System.in);

        GpioUtil.enableNonPrivilegedAccess();
        GpioController gpioController = GpioFactory.getInstance();
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        try (FourBlock fB = new FourBlock(gpioController);
             GroveLCD lcd = new GroveLCD(i2c)) {
            fB.addListener(lcd);

            String exitline = "";
            while (!"exit".equalsIgnoreCase(exitline)) {
                System.out.println("Please input a line");
                exitline = scanner.nextLine();
            }
//            Random r = new Random();
//            lcd.setRGB(0,0, 255);
//            for (int i = 0; i < 300; i++) {
//                lcd.setRGB(r.nextInt(254), r.nextInt(254), r.nextInt(254));
//                Thread.sleep(r.nextInt(300, 354));
//            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//        log.warn("dps310 loading ");
//        try (DPS310 dps1 = new DPS310(i2c, DPS310.DPS310_ADDRESS_1);
//             DPS310 dps2 = new DPS310(i2c, DPS310.DPS310_ADDRESS_2);
//             Relay relay = new Relay(gpioController)) {
//            DHT11 humiditySensor = new DHT11(0);
//            relay.setOnLevel(6);
//            Thread.sleep(2000);
//            relay.turnOff();
//            for (int i = 0; i < 3; ++i) {
//                log.warn("dps1 temp {} pressure {}", dps1.getTemperature(), dps1.getPressure());
//                log.warn("dps2 temp {} pressure {}", dps2.getTemperature(), dps2.getPressure());
//                log.warn("dht11 temp {} hum {}", humiditySensor.getTemperature(), humiditySensor.getHumidity());
//                Thread.sleep(2000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
}

