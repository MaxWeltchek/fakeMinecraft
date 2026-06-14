package minecraftclone.player;

import minecraftclone.inventory.Inventory;
import minecraftclone.inventory.Item;

public class Player {
    public final String userName;
    public final Inventory inventory;
    private int health;
    private boolean alive;

    public Player(String name) {
        inventory = new Inventory();
        userName = name;
        health = 20;
        alive = true;
    }

    public void hurt(int damage) {
        health -= damage;
        if (health <= 0) {
            kill();
        }
    }

    public void kill() {
        health = 0;
        alive = false;
    }
}
