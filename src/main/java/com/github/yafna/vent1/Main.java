package com.github.yafna.vent1;

import com.github.yafna.vent1.buttons.FourBlock;
import com.github.yafna.vent1.humidity.DHT11;
import com.github.yafna.vent1.i2c.GroveLCD;
import com.github.yafna.vent1.i2c.pressure.DPS310;
import com.github.yafna.vent1.relay.Relay;
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
             GroveLCD lcd = new GroveLCD(i2c);
             DPS310 dps1 = new DPS310(i2c, DPS310.DPS310_ADDRESS_1);
             DPS310 dps2 = new DPS310(i2c, DPS310.DPS310_ADDRESS_2);
             DHT11 humiditySensor = new DHT11(0);
             Relay relay = new Relay(gpioController)) {

            Controller controller = new Controller(lcd, dps1, dps2, humiditySensor, relay);
            fB.addListener(controller);

            String exitline = "";
            while (!"exit".equalsIgnoreCase(exitline)) {
                Thread.sleep(200);
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
}

