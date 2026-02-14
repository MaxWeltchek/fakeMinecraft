import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandling implements KeyListener {
    public static int cameraMoveDist = 30;
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
            Main.swap = true;
            Main.inFocus = (Main.inFocus + 1) % Main.numScreens;
        }
        //rotation debug
        else if (e.getKeyChar() == 't') {
            Main.zRotation += Math.PI / 12.0;
        } else if (e.getKeyChar() == 'y') {
            Main.zRotation -= Math.PI / 12.0;
        }
        //movement logic
        else if (e.getKeyChar() == 'w') {
            Camera.calculateMovement(0);
            Main.updatePosition = true;
        } else if (e.getKeyChar() == 's') {
            Camera.calculateMovement(Math.PI);
            Main.updatePosition = true;
        } else if (e.getKeyChar() == 'a') {
            Camera.calculateMovement(Math.PI/2.0);
            Main.updatePosition = true;
        } else if (e.getKeyChar() == 'd') {
            Camera.calculateMovement(Math.PI/-2.0);
            Main.updatePosition = true;
        } else if (e.getKeyChar() == 'z') {
            Camera.setCoordinates(Camera.getCoordinates()[0], Camera.getCoordinates()[1] - cameraMoveDist, Camera.getCoordinates()[2]);
            Main.updatePosition = true;
        } else if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            Camera.setCoordinates(Camera.getCoordinates()[0], Camera.getCoordinates()[1] + cameraMoveDist, Camera.getCoordinates()[2]);
            Main.updatePosition = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //shift up
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            Main.shiftPressed = false;
        }
    }
}
