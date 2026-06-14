package minecraftclone.inventory;

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
}
