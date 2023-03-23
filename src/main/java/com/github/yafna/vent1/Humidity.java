package com.github.yafna.vent1;

//import com.pi4j.context.Context;
//import com.pi4j.io.i2c.I2C;
//import com.pi4j.io.i2c.I2CConfig;
//import com.pi4j.io.i2c.I2CProvider;
//import com.pi4j.io.i2c.I2CRegister;
//import com.pi4j.io.spi.Spi;
//import com.pi4j.io.spi.SpiProvider;
//import com.pi4j.util.StringUtil;
//import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Humidity  {
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Humidity.class);
//    public static final int dht_temp_cmd = 40;
//    private static final int I2C_BUS = 20;
//    private static final int I2C_DEVICE = 10;
//
//    private final Context pi4j;
//    private I2C i2c;
//    private I2CRegister register;
//    public Humidity(Context pi4j) {
//        this.pi4j = pi4j;
//        init();
//    }
//
//    private void init() {
//        // create SPI config
//        var config  = Spi.newConfigBuilder(pi4j)
//                .id("my-spi-device")
//                .name("My SPI Device")
//                .address(I2C_DEVICE)
//                .baud(Spi.DEFAULT_BAUD)
//                .build();
//
//        // get a SPI I/O provider from the Pi4J context
//        SpiProvider spiProvider = pi4j.provider("pigpio-spi");
//
//        // use try-with-resources to auto-close SPI when complete
//        try (var spi = spiProvider.create(config);) {
////            String data = "THIS IS A TEST";
//            byte[] buffer = new byte[4];
//            buffer[0]=40;
//            buffer[1]=10;
//            logger.warn("TH : [Pi4J IO write] " + Arrays.toString(buffer));
//            // open SPI communications
//            spi.open();
//
//            // write data to the SPI channel
//            spi.write(buffer);
//
//            // take a breath to allow time for the SPI
//            // data to get updated in the SPI device
//            Thread.sleep(600);
//
//            // read data back from the SPI channel
////            ByteBuffer buffer = spi.readByteBuffer(data.length());
//
//            logger.warn("--------------------------------------");
//            logger.warn("--------------------------------------");
//            logger.warn("SPI [WRITE] :");
//            logger.warn("  [BYTES]  0x" +  Arrays.toString(buffer));
//            logger.warn("SPI [READ] :");
////            logger.warn("  [BYTES]  0x" + StringUtil.toHexString(buffer.array()));
////            logger.warn("  [STRING] " + new String(buffer.array()));
//            logger.warn("--------------------------------------");
//
//            // read data back from the SPI channel
////            ByteBuffer writeBuffer = ByteBuffer.wrap("Hello World!".getBytes(StandardCharsets.US_ASCII));
//            byte[] readBuffer = new byte[9];
//            spi.read( readBuffer);
//
//            logger.warn("--------------------------------------");
//            logger.warn("SPI [TRANSFER] :");
//            logger.warn("  [BYTES]  0x" +  Arrays.toString(readBuffer));
////            logger.warn("  [STRING] " + new String(readBuffer));
//            logger.warn("--------------------------------------");
//            logger.warn("--------------------------------------");
//
//            // SPI channel will be closed when this block goes
//            // out of scope by the AutoCloseable interface on SPI
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        I2CConfig config = I2C.newConfigBuilder(pi4j)
////                .id("timehumm")
////                .name("My I2C Bus")
////                .bus(I2C_BUS)
////                .device(I2C_DEVICE)
////                .build();
//////        this.device = pi4j.create(config);
////        I2CProvider i2CProvider = pi4j.provider("pigpio-spi");
////
////        // use try-with-resources to auto-close I2C when complete
////        i2c = i2CProvider.create(config);
////        // we will be reading and writing to register address 0x01
////        register = i2c.register(I2C_DEVICE);
//    }
//
//    public double getTemperature() throws InterruptedException, IOException {
//        write(dht_temp_cmd, 10, 0, 0);
//        Thread.sleep(1600);
//        byte[] data = read(new byte[9]);
//        double res = ByteBuffer.wrap(new byte[]{data[4], data[3], data[2], data[1]}).getFloat();
//        return res;
//    }
//
//    public double getHumidity() throws IOException, InterruptedException {
//        write(dht_temp_cmd, 10, 0, 0);
//        Thread.sleep(1600);
//        byte[] data = read(new byte[9]);
//        double res = ByteBuffer.wrap(new byte[]{data[8], data[7], data[6], data[5]}).getFloat();
//        return res;
//    }
//
//    private void write(int... command) {
//        byte[] buffer = new byte[command.length];
//        for (int i = 0; i < command.length; i++) {
//            buffer[i] = (byte) command[i];
//        }
//        logger.warn("TH : [Pi4J IO write] " + Arrays.toString(buffer));
//        register.write(buffer, 0, command.length);
//    }
//
//    private byte[] read(byte[] buffer) {
//        register.read(buffer, 0, buffer.length);
//        logger.warn("TH : [Pi4J IO read] " + Arrays.toString(buffer));
//        return buffer;
//    }
//
//    @Override
//    public void close() throws Exception {
//        i2c.close();
//    }
}
