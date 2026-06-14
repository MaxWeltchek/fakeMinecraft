package minecraftclone.inventory;

public class InventoryCell {
    private final int[] centerCoords;
    private Item itemInSlot;

    public InventoryCell(int[] coords) {
        centerCoords = coords;
        itemInSlot = null;
    }

    protected Item getItemInSlot() {
        return itemInSlot;
    }

    protected int[] getCenterCoords() {
        return centerCoords;
    }

    protected void setItemInSlot(Item item) {
        itemInSlot = item;
    }

    protected void clearSlot() {
        itemInSlot = null;
    }
}
