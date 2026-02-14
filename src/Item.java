import java.awt.*;

public class Item {
    private String lore;
    private final Image sprite;
    private int[] centerCoords;
    private int amount;
    private boolean stackable;

    public Item(String lore_, int[] centerCoords_, int amount_, boolean stackable_) {
        lore = lore_;
        sprite = SpriteLoader.generate(lore_);
        centerCoords = centerCoords_;
        amount = amount_;
        stackable = stackable_;
    }

    public Item(String lore_, Image sprite_, int[] centerCoords_, int amount_, boolean stackable_) {
        lore = lore_;
        sprite = sprite_;
        centerCoords = centerCoords_;
        amount = amount_;
        stackable = stackable_;
    }

    //clone object instead of make pointer
    public Item clone() {
        return new Item(lore, sprite, centerCoords.clone(), amount, stackable);
    }

    //getters/setters
    public Image getSprite() {return sprite;}
    public int[] getCoords() {return centerCoords;}
    public String getLore() {return lore;}
    public void move(int[] coords) {
        centerCoords = coords;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int a) {
        amount = a;
    }
    public boolean isStackable() {return stackable;}
    public void decrement() {amount--;}
}
