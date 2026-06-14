package minecraftclone.inventory;

import minecraftclone.Main;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Inventory {
    private final ArrayList<InventoryCell> inventoryCells;
    private static final int[][] allSquareCenterCoords = new int[][]{
            //top inventory row indices 0-8
            {80, 460}, {170, 460}, {260, 460}, {350, 460}, {440, 460}, {530, 460}, {620, 460}, {710, 460}, {800, 460},
            //middle inventory row indices 9-17
            {80, 550}, {170, 550}, {260, 550}, {350, 550}, {440, 550}, {530, 550}, {620, 550}, {710, 550}, {800, 550},
            //bottom inventory row indices 18-26
            {80, 640}, {170, 640}, {260, 640}, {350, 640}, {440, 640}, {530, 640}, {620, 640}, {710, 640}, {800, 640},
            //hotbar, indices 27-35
            {80, 750}, {170, 750}, {260, 750}, {350, 750}, {440, 750}, {530, 750}, {620, 750}, {710, 750}, {800, 750},
            //all crafting grid squares are indices 36-44
            //crafting grid top row
            {190, 125}, {280, 125}, {370, 125},
            //crafting grid middle row
            {190, 215}, {280, 215}, {370, 215},
            //crafting grid bottom row
            {190, 305}, {280, 305}, {370, 305},
            //crafting output slot index 45
            {660, 215}
    };

    public Inventory() {
        inventoryCells = new ArrayList<InventoryCell>();
        for (int i = 0; i < allSquareCenterCoords.length; i++) {
            inventoryCells.add(new InventoryCell(allSquareCenterCoords[i]));
        }
    }

    public void setItemInSlot(int slot, Item item) {
        inventoryCells.get(slot).setItemInSlot(item);
    }

    public int size() {
        return inventoryCells.size();
    }

    public void clearSlot(int slot) {
        setItemInSlot(slot, null);
    }

    public Item getItemInSlot(int slot) {
        return inventoryCells.get(slot).getItemInSlot();
    }

    public boolean isOccupied(int slot) {
        return inventoryCells.get(slot).getItemInSlot() != null;
    }

    public int[] getCenterCoords(int slot) {
        return inventoryCells.get(slot).getCenterCoords();
    }

    public Item[] getAllItems() {
        Item[] allItems = new Item[inventoryCells.size()];
        for (int i = 0; i < inventoryCells.size(); i++) {
            allItems[i] = inventoryCells.get(i).getItemInSlot();
        }
        return allItems;
    }

    public void moveItems(int x, int y, MouseEvent button) {
        //shift down handling TODO: add auto stacking when shift clicking
        if (Main.shiftPressed) {
            int firstAvailable = -1;
            int nearestCell = findNearestCell(new int[]{x, y});

            //if the square picked is an inventory cell find the first hotbar slot free
            if (nearestCell <= 26) {
                for (int i = 35; i >= 27; i--) {
                    if (canMoveIntoSpot(i, nearestCell)) {
                        firstAvailable = i;
                        break;
                    }
                }
            } else if (nearestCell <= 35) { //if the square picked is a hotbar slot, put it in the first (furthest top and left) inventory slot free
                for (int i = 0; i <= 26; i++) {
                    if (canMoveIntoSpot(i, nearestCell)) {
                        firstAvailable = i;
                        break;
                    }
                }
            } else { //if it's a crafting grid square, try hotbar then inventory with same logic as above
                //tries hotbar
                for (int i = 35; i >= 27; i--) {
                    if (canMoveIntoSpot(i, nearestCell)) {
                        firstAvailable = i;
                        break;
                    }
                }
                if (firstAvailable == -1) { //if no free hotbar slots try inventory
                    for (int i = 0; i <= 26; i++) {
                        if (canMoveIntoSpot(i, nearestCell)) {
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
            if (this.isOccupied(nearestCell)) {
                moveItems(nearestCell, firstAvailable);
//                this.setItemInSlot(firstAvailable, this.getItemInSlot(nearestCell).clone());
//                this.getItemInSlot(firstAvailable).move(this.getCenterCoords(firstAvailable));
//                this.clearSlot(nearestCell);
            }
            //short circuit instead of giant if case
        }

        //if not holding anything
        else if (Main.heldItem == null) {
            for (Item item : this.getAllItems()) {
                if (item == null) {
                    continue;
                }
                //if inside the item box
                if (x > item.getCoords()[0] - Main.itemDimension / 2 && x < item.getCoords()[0] + Main.itemDimension / 2 && y > item.getCoords()[1] - Main.itemDimension / 2 && y < item.getCoords()[1] + Main.itemDimension / 2) {
                    //if left click
                    if (findNearestCell(new int[]{x, y}) == 45) {
                        Main.decrementCraftingSlots();
                        Main.craftingGridUpdated = true;
                    } else if (findNearestCell(new int[]{x, y}) >= 36 && findNearestCell(new int[]{x, y}) < 45) {
                        Main.craftingGridUpdated = true;
                    }
                    if (button.getButton() == MouseEvent.BUTTON1) {
                        //clear the best slot
                        this.setItemInSlot(findNearestCell(new int[]{x, y}), null);
                        Main.heldItem = item;
                        break;
                    } else if (button.getButton() == MouseEvent.BUTTON3) { //split the stack if right click
                        Main.heldItem = this.getItemInSlot(findNearestCell(new int[]{x, y})).clone();
                        this.getItemInSlot(findNearestCell(new int[]{x, y})).setAmount(this.getItemInSlot(findNearestCell(new int[]{x, y})).getAmount() / 2);
                        Main.heldItem.setAmount((Main.heldItem.getAmount() / 2) + (Main.heldItem.getAmount() %2));

                        if (this.getItemInSlot(findNearestCell(new int[]{x, y})).getAmount() == 0) {
                            this.clearSlot(findNearestCell(new int[]{x, y}));
                        }
                    }
                }
            }
        } else { //if holding something
            //find the "best point" which is just the cell the item should snap to
            int bestPoint = findNearestCell(new int[]{x, y});

            //set flag if we update crafting grid to save compute (slow craft checking alg)
            if (bestPoint >= 36 && bestPoint < 45) {
                Main.craftingGridUpdated = true;
            } //or if we are holding something we can't click on crafting output slot
            else if (bestPoint == 45) {
                if (this.isOccupied(45) && Main.heldItem.getLore().equals(this.getItemInSlot(45).getLore())) {
                    Main.heldItem.setAmount(Main.heldItem.getAmount() + this.getItemInSlot(45).getAmount());
                    Main.decrementCraftingSlots();
                    Main.craftingGridUpdated = true;
                }
                return;
            }
            //if that cell is empty then update it with the held item, then clear the held item
            if (!this.isOccupied(bestPoint)) {
                this.setItemInSlot(bestPoint, Main.heldItem);
                Main.heldItem.move(this.getCenterCoords(bestPoint));
                Main.heldItem = null;
            }
            //if the cell is occupied, AND the held item and item occupying the slot are the same, just add the stack sizes (if stackable)
            else if (this.isOccupied(bestPoint) && (this.getItemInSlot(bestPoint).getLore().equals(Main.heldItem.getLore())) && Main.heldItem.isStackable()) {
                //if adding the two would make the stack >64, set the slot to 64 and hand to leftover
                if (Main.heldItem.getAmount() + this.getItemInSlot(bestPoint).getAmount() > 64) {
                    Main.heldItem.setAmount((this.getItemInSlot(bestPoint).getAmount() + Main.heldItem.getAmount()) - 64);
                    this.getItemInSlot(bestPoint).setAmount(64);
                } else {
                    this.getItemInSlot(bestPoint).setAmount(this.getItemInSlot(bestPoint).getAmount() + Main.heldItem.getAmount());
                    Main.heldItem = null;
                }
            }
            //finally if its just occupied and not the same (stackable) swap them
            else if (this.isOccupied(bestPoint) && !this.getItemInSlot(bestPoint).getLore().equals(Main.heldItem.getLore())) {
                Item temp = Main.heldItem.clone();
                Main.heldItem = this.getItemInSlot(bestPoint);
                this.setItemInSlot(bestPoint, temp);
                this.getItemInSlot(bestPoint).move(this.getCenterCoords(bestPoint));
            }
        }
    }

    private void moveItems(int cell1, int cell2) {
        if (!isOccupied(cell2)) {
            getItemInSlot(cell1).move(getCenterCoords(cell2));
            setItemInSlot(cell2, getItemInSlot(cell1));
            clearSlot(cell1);
        } else if (!ItemRegistry.isStackable(getItemInSlot(cell1).getLore())) {
            getItemInSlot(cell1).move(getCenterCoords(cell2));
            setItemInSlot(cell2, getItemInSlot(cell1));
            clearSlot(cell1);
        } else {
            int sum = getItemInSlot(cell1).getAmount() + getItemInSlot(cell2).getAmount();
            if (sum <= 64) {
                getItemInSlot(cell1).move(getCenterCoords(cell2));
                setItemInSlot(cell2, getItemInSlot(cell1));
                getItemInSlot(cell2).setAmount(sum);
                clearSlot(cell1);
            } else {
                int transferAmount = 64 - getItemInSlot(cell2).getAmount();
                getItemInSlot(cell1).setAmount(getItemInSlot(cell1).getAmount() - transferAmount);
                getItemInSlot(cell2).setAmount(64);
            }
        }
    }

    private boolean canMoveIntoSpot(int target, int source) {
        if (!isOccupied(target)) {
            return true;
        }

        Item targetItem = getItemInSlot(target);
        Item sourceItem = getItemInSlot(source);

        return sourceItem != null
                && targetItem.getLore().equals(sourceItem.getLore())
                && ItemRegistry.isStackable(sourceItem.getLore());
    }

    private int findNearestCell(int[] coords) {
        double bestDistance = Integer.MAX_VALUE;
        int bestPoint = -1;
        for (int i = 0; i < this.size(); i++) {
            double temp = computeSquaredDistance(this.getCenterCoords(i), coords);
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

}
