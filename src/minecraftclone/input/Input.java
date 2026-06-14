package minecraftclone.input;

public class Input {
    private String action;
    private String lore;
    private int quantity;

    public Input(String action_, String lore_, int quantity_) {
        action = action_;
        lore = lore_;
        quantity = quantity_;
    }

    public String getAction() {
        return action;
    }

    public String getLore() {
        return lore;
    }

    public int getQuantity() {
        return quantity;
    }
}
