package minecraftclone.input;

import minecraftclone.Main;
import minecraftclone.inventory.InventoryCell;
import minecraftclone.logging.LogEntry;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class KeyboardHandling implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override

    public void keyPressed(KeyEvent e) {
        //8 is backspace, then delete slot
        if (e.getKeyCode() == 8) {
            for (InventoryCell cell : Main.inventoryCells) {
                if (Main.mouseCoords[0] > cell.getCenterCoords()[0] - Main.itemDimension / 2 && Main.mouseCoords[0] < cell.getCenterCoords()[0] + Main.itemDimension / 2 && Main.mouseCoords[1] > cell.getCenterCoords()[1] - Main.itemDimension / 2 && Main.mouseCoords[1] < cell.getCenterCoords()[1] + Main.itemDimension / 2) {
                    cell.clearSlot();
                    break;
                }
            }
            //shift down
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            Main.shiftPressed = true;
        } else if (e.getKeyChar() == 'p') {
            //emergency escape for infinite loop
            Main.panicFlag = !Main.panicFlag;
        } else if (e.getKeyChar() == 'e') {
            Main.inFocus = (Main.inFocus + 1) % Main.numScreens;
            try {
                Main.logger.writeLog(new LogEntry("SYSTEM/INFO", "In Focus Switched: " + ((Main.inFocus == 1) ? "World" : "Inventory")));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        //rotation debug
        else if (e.getKeyChar() == 't') {
            Main.zRotation += Math.PI / 12.0;
        } else if (e.getKeyChar() == 'y') {
            Main.zRotation -= Math.PI / 12.0;
        }
        //movement logic
        else if (e.getKeyChar() == 'w') {
            Main.moveForward = true;
        } else if (e.getKeyChar() == 's') {
            Main.moveBack = true;
        } else if (e.getKeyChar() == 'a') {
            Main.moveLeft = true;
        } else if (e.getKeyChar() == 'd') {
            Main.moveRight = true;
        } else if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            if (!Main.jumping) {
                Main.jumping = true;
                }
        } else if (e.getKeyChar() == 'g') {
            Main.mouseListener.resetFakeMouse();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //shift up
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            Main.shiftPressed = false;
        } else if (e.getKeyChar() == 'w') {
            Main.moveForward = false;
        } else if (e.getKeyChar() == 's') {
            Main.moveBack = false;
        } else if (e.getKeyChar() == 'a') {
            Main.moveLeft = false;
        } else if (e.getKeyChar() == 'd') {
            Main.moveRight = false;
        }
    }
}
