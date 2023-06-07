package com.github.yafna.vent1;

import com.github.yafna.vent1.dto.ButtonMatrix;
import com.github.yafna.vent1.dto.MButtonListener;
import com.github.yafna.vent1.dto.MenuItem;
import com.github.yafna.vent1.humidity.DHT11;
import com.github.yafna.vent1.i2c.GroveLCD;
import com.github.yafna.vent1.i2c.pressure.DPS310;
import com.github.yafna.vent1.relay.Relay;

import java.io.IOException;

public class Controller implements MButtonListener {
    private GroveLCD lcd;
    private DPS310 dps1;
    private DPS310 dps2;
    private DHT11 humiditySensor;
    private Relay relay;
    public static MenuItem activeItem;
    public static MenuItem selectedItem;

    public Controller(GroveLCD lcd, DPS310 dps1, DPS310 dps2, DHT11 humiditySensor, Relay relay) {
        this.lcd = lcd;
        this.dps1 = dps1;
        this.dps2 = dps2;
        this.humiditySensor = humiditySensor;
        this.relay = relay;
        activeItem = MenuItem.MAIN_MENU;
        drawMenu();
    }

    private void drawMenu() {
        String displayStr = "";
        if (activeItem == MenuItem.DISPLAY_COLORS) {
            displayStr = " Press button to change color";
        }
        if (activeItem == MenuItem.TEMPERATURE) {
            displayStr = "temp " + humiditySensor.getTemperature() + "\n" +
                    " hum " + humiditySensor.getHumidity();
        }
        if (activeItem == MenuItem.PRESSURE) {
            displayStr = "1 pr: " + dps1.getPressure() + "\n" +
                    "2 pr: " + dps2.getPressure();
        }
        if (activeItem == MenuItem.RELAY) {
            displayStr = " " + relay.getSumLevels() + ": ";
            for (Boolean level : relay.getLevels()) {
                displayStr += level ? "_" : "*";
            }
            displayStr += "\n" + "<                     >";
        }
        if (activeItem == MenuItem.MAIN_MENU) {
            if (selectedItem == null) {
                selectedItem = MenuItem.TEMPERATURE;
            }
            if (selectedItem == MenuItem.TEMPERATURE) {
                displayStr = " # " + MenuItem.TEMPERATURE.getDisplayStr() +
                        "\n" + "  " + MenuItem.PRESSURE + "    >";
            }
            if (selectedItem == MenuItem.PRESSURE) {
                displayStr = " # " + MenuItem.PRESSURE.getDisplayStr() +
                        "\n" + "< " + MenuItem.RELAY + "    >";
            }
            if (selectedItem == MenuItem.RELAY) {
                displayStr = " # " + MenuItem.RELAY.getDisplayStr() +
                        "\n" + "< " + MenuItem.DISPLAY_COLORS + " >";
            }
            if (selectedItem == MenuItem.DISPLAY_COLORS) {
                displayStr = " " + MenuItem.RELAY.getDisplayStr() +
                        "\n" + "<# " + MenuItem.DISPLAY_COLORS;
            }
        }
        try {
            lcd.clearDisplay();
            lcd.setText(displayStr);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buttonClicked(ButtonMatrix type) {
        if (activeItem == MenuItem.MAIN_MENU) {
            if (type == ButtonMatrix.ENTER) {
                activeItem = selectedItem;
            }
            if (type == ButtonMatrix.LEFT) {
                if (selectedItem == MenuItem.TEMPERATURE) {
                    selectedItem = MenuItem.PRESSURE;
                } else if (selectedItem == MenuItem.PRESSURE) {
                    selectedItem = MenuItem.RELAY;
                } else if (selectedItem == MenuItem.RELAY) {
                    selectedItem = MenuItem.DISPLAY_COLORS;
                }
            }
            if (type == ButtonMatrix.RIGHT) {
                if (selectedItem == MenuItem.PRESSURE) {
                    selectedItem = MenuItem.TEMPERATURE;
                } else if (selectedItem == MenuItem.RELAY) {
                    selectedItem = MenuItem.PRESSURE;
                } else if (selectedItem == MenuItem.DISPLAY_COLORS) {
                    selectedItem = MenuItem.RELAY;
                }
            }
            drawMenu();
        }
        if (activeItem == MenuItem.TEMPERATURE) {
            if (type == ButtonMatrix.BACK) {
                activeItem = MenuItem.MAIN_MENU;
            }
            drawMenu();
        }
        if (activeItem == MenuItem.PRESSURE) {
            if (type == ButtonMatrix.BACK) {
                activeItem = MenuItem.MAIN_MENU;
            }
            drawMenu();
        }
        if (activeItem == MenuItem.RELAY) {
            if (type == ButtonMatrix.BACK) {
                activeItem = MenuItem.MAIN_MENU;
            }
            if (type == ButtonMatrix.LEFT) {
                relay.setOnLevel(relay.getSumLevels() - 1);
            }
            if (type == ButtonMatrix.RIGHT) {
                relay.setOnLevel(relay.getSumLevels() + 1);
            }
            drawMenu();
        }
        if (activeItem == MenuItem.DISPLAY_COLORS) {
            try {
                if (type == ButtonMatrix.BACK) {
                    activeItem = MenuItem.MAIN_MENU;
                    drawMenu();
                }
                if (type == ButtonMatrix.ENTER) {
                    lcd.setRGB(0, 255, 0);
                }
                if (type == ButtonMatrix.LEFT) {
                    lcd.setRGB(0, 0, 255);
                }
                if (type == ButtonMatrix.RIGHT) {
                    lcd.setRGB(255, 0, 255);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
