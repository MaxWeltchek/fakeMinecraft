package minecraftclone.input;

import minecraftclone.Main;
import minecraftclone.inventory.Item;
import minecraftclone.rendering.Camera;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandling implements MouseListener, MouseMotionListener {
    private final int maxStack = 64;
    private boolean firstMove = true;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Main.max.inventory.moveItems(e.getX(), e.getY(), e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Main.mouseCoords[0] = e.getX();
        Main.mouseCoords[1] = e.getY();
        Main.mouseY = e.getY();
        Main.mouseX = e.getX();

        if (Main.inFocus == 0) {
            if (firstMove) {
                Main.fakeMouseCoords = new int[]{0, 0};
                firstMove = false;
            } else {
                Camera.setRotation(-(Main.fakeMouseCoords[1]) / 400.0, -(Main.fakeMouseCoords[0]) / 400.0);
            }
        }

    }

    private int findNearestCell(int[] coords) {
        double bestDistance = Integer.MAX_VALUE;
        int bestPoint = -1;
        for (int i = 0; i < Main.max.inventory.size(); i++) {
            double temp = computeSquaredDistance(Main.max.inventory.getCenterCoords(i), coords);
            if (temp < bestDistance) {
                bestDistance = temp;
                bestPoint = i;
            }
        }
        return bestPoint;
    }

    private double computeSquaredDistance(int[] coords1, int[] coords2) {
        return Math.pow(coords2[0] - coords1[0], 2) + Math.pow(coords2[1] - coords1[1], 2);
    }

    public void updateFakeMousePosition(Robot mouseMover, Canvas canvas) {

        if (firstMove) {
            return;
        }

        int currentRealMouseX = Main.mouseX + (int) canvas.getLocationOnScreen().getX();
        int currentRealMouseY = Main.mouseY + (int) canvas.getLocationOnScreen().getY() - 61;
        int[] realMouseDistanceMoved = new int[]{currentRealMouseX-400, currentRealMouseY-400};
        Main.fakeMouseCoords[0] += realMouseDistanceMoved[0];
        Main.fakeMouseCoords[1] += realMouseDistanceMoved[1];
        mouseMover.mouseMove((int) canvas.getLocationOnScreen().getX() + 400, (int) canvas.getLocationOnScreen().getY() + 400);
    }

    public void resetFakeMouse() {
        Main.fakeMouseCoords[0] = 0;
        Main.fakeMouseCoords[1] = 0;
    }
}
