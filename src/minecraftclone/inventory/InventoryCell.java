package minecraftclone.inventory;

public class InventoryCell {
    private final int[] centerCoords;
    private Item itemInSlot;

    public InventoryCell(int[] coords) {
        centerCoords = coords;
        itemInSlot = null;
    }

    public boolean occupied() {
        return itemInSlot != null;
    }

    public Item getItemInSlot() {
        return itemInSlot;
    }

    public int[] getCenterCoords() {
        return centerCoords;
    }

    public void setItemInSlot(Item item) {
        itemInSlot = item;
    }

    public void clearSlot() {
        itemInSlot = null;
    }
}
