package minecraftclone.input;

import minecraftclone.Main;
import minecraftclone.inventory.InventoryCell;
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
//        if (Main.heldItem != null)
//            System.out.println("Held item: " + Main.heldItem.getLore());
//        else
//            System.out.println("no held item");

        boolean shouldStack = false; //do something with that pls

        //shift down handling TODO: add auto stacking when shift clicking
        if (Main.shiftPressed) {
            int firstAvailable = -1;
            int nearestCell = findNearestCell(new int[]{e.getX(), e.getY()});

            //if the square picked is an inventory cell find the first hotbar slot free
            if (nearestCell <= 26) {
                for (int i = 35; i >= 27; i--) {
                    if (!Main.inventoryCells.get(i).occupied()) {
                        firstAvailable = i;
                        break;
                    }
                }
            } else if (nearestCell <= 35) { //if the square picked is a hotbar slot, put it in the first (furthest top and left) inventory slot free
                for (int i = 0; i <= 26; i++) {
                    if (!Main.inventoryCells.get(i).occupied()) {
                        firstAvailable = i;
                        break;
                    }
                }
            } else { //if it's a crafting grid square, try hotbar then inventory with same logic as above
                //tries hotbar
                for (int i = 35; i >= 27; i--) {
                    if (!Main.inventoryCells.get(i).occupied()) {
                        firstAvailable = i;
                        break;
                    }
                }
                if (firstAvailable == -1) { //if no free hotbar slots try inventory
                    for (int i = 0; i <= 26; i++) {
                        if (!Main.inventoryCells.get(i).occupied()) {
                            firstAvailable = i;
                            break;
                        }
                    }
                }
            }

            //do nothing if no available slots
            if (firstAvailable == -1) {
                return;
            }

            //sets new item in that cell, moves it, then clears original cell
            //unsure if this is less overhead than checking if nothing would happen in the first place (i.e. firstAvailable == nearestCell)
            if (Main.inventoryCells.get(nearestCell).occupied()) {
                Main.inventoryCells.get(firstAvailable).setItemInSlot(Main.inventoryCells.get(nearestCell).getItemInSlot().clone());
                Main.inventoryCells.get(firstAvailable).getItemInSlot().move(Main.inventoryCells.get(firstAvailable).getCenterCoords());
                Main.inventoryCells.get(nearestCell).clearSlot();

            }
            //short circuit instead of giant if case
        }

        //if not holding anything
        else if (Main.heldItem == null) {
            for (InventoryCell cell : Main.inventoryCells) {
                Item item;
                if (cell.occupied()) {
                    item = cell.getItemInSlot();
                } else {
                    continue;
                }
                //if inside the item box
                if (e.getX() > item.getCoords()[0] - Main.itemDimension / 2 && e.getX() < item.getCoords()[0] + Main.itemDimension / 2 && e.getY() > item.getCoords()[1] - Main.itemDimension / 2 && e.getY() < item.getCoords()[1] + Main.itemDimension / 2) {
                    //if left click
                    if (findNearestCell(new int[]{e.getX(), e.getY()}) == 45) {
                        Main.decrementCraftingSlots();
                        Main.craftingGridUpdated = true;
                    } else if (findNearestCell(new int[]{e.getX(), e.getY()}) >= 36 && findNearestCell(new int[]{e.getX(), e.getY()}) < 45) {
                        Main.craftingGridUpdated = true;
                    }
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        //clear the best slot
                        Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).setItemInSlot(null);
                        Main.heldItem = item;
                        break;
                    } else if (e.getButton() == MouseEvent.BUTTON3) { //split the stack if right click
                        Main.heldItem = Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).getItemInSlot().clone();
                        Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).getItemInSlot().setAmount(Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).getItemInSlot().getAmount() / 2);
                        Main.heldItem.setAmount((Main.heldItem.getAmount() / 2) + (Main.heldItem.getAmount() %2));

                        if (Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).getItemInSlot().getAmount() == 0) {
                            Main.inventoryCells.get(findNearestCell(new int[]{e.getX(), e.getY()})).clearSlot();
                        }
                    }
                }
            }
        } else { //if holding something
            //find the "best point" which is just the cell the item should snap to
            int bestPoint = findNearestCell(new int[]{e.getX(), e.getY()});

            //set flag if we update crafting grid to save compute (slow craft checking alg)
            if (bestPoint >= 36 && bestPoint < 45) {
                Main.craftingGridUpdated = true;
            } //or if we are holding something we can't click on crafting output slot
            else if (bestPoint == 45) {
                if (Main.inventoryCells.get(45).occupied() && Main.heldItem.getLore().equals(Main.inventoryCells.get(45).getItemInSlot().getLore())) {
                    Main.heldItem.setAmount(Main.heldItem.getAmount() + Main.inventoryCells.get(45).getItemInSlot().getAmount());
                    Main.decrementCraftingSlots();
                    Main.craftingGridUpdated = true;
                }
                return;
            }
            //if that cell is empty then update it with the held item, then clear the held item
            if (!Main.inventoryCells.get(bestPoint).occupied()) {
                Main.inventoryCells.get(bestPoint).setItemInSlot(Main.heldItem);
                Main.heldItem.move(Main.allSquareCenterCoords[bestPoint]);
                Main.heldItem = null;
            }
            //if the cell is occupied, AND the held item and item occupying the slot are the same, just add the stack sizes (if stackable)
            else if (Main.inventoryCells.get(bestPoint).occupied() && (Main.inventoryCells.get(bestPoint).getItemInSlot().getLore().equals(Main.heldItem.getLore())) && Main.heldItem.isStackable()) {
                //if adding the two would make the stack >64, set the slot to 64 and hand to leftover
                if (Main.heldItem.getAmount() + Main.inventoryCells.get(bestPoint).getItemInSlot().getAmount() > 64) {
                    Main.heldItem.setAmount((Main.inventoryCells.get(bestPoint).getItemInSlot().getAmount() + Main.heldItem.getAmount()) - 64);
                    Main.inventoryCells.get(bestPoint).getItemInSlot().setAmount(64);
                } else {
                    Main.inventoryCells.get(bestPoint).getItemInSlot().setAmount(Main.inventoryCells.get(bestPoint).getItemInSlot().getAmount() + Main.heldItem.getAmount());
                    Main.heldItem = null;
                }
            }
            //finally if its just occupied and not the same (stackable) swap them
            else if (Main.inventoryCells.get(bestPoint).occupied() && !Main.inventoryCells.get(bestPoint).getItemInSlot().getLore().equals(Main.heldItem.getLore())) {
                Item temp = Main.heldItem.clone();
                Main.heldItem = Main.inventoryCells.get(bestPoint).getItemInSlot();
                Main.inventoryCells.get(bestPoint).setItemInSlot(temp);
                Main.inventoryCells.get(bestPoint).getItemInSlot().move(Main.inventoryCells.get(bestPoint).getCenterCoords());
            }
        }
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
        for (int i = 0; i < Main.inventoryCells.size(); i++) {
            double temp = computeSquaredDistance(Main.inventoryCells.get(i).getCenterCoords(), coords);
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
