package minecraftclone.inventory;

public class InventoryFullException extends RuntimeException {
    public InventoryFullException(String message) {
        super(message);
    }
    public InventoryFullException(String item ,int granted) {
        super(granted == 0 ? "Inventory full" : "Failed to grant all items, gave " + granted + " " + item.split("_")[0] + (item.split("_").length > 1 ? " " + item.split("_")[1] : "") + (granted > 1 ? "s" : "") + " instead");
    }
}
