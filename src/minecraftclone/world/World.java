package minecraftclone.world;

import minecraftclone.player.Player;

import java.util.ArrayList;

public class World {
    private ArrayList<Cube> cubes;
    private ArrayList<Player> players;

    public World(ArrayList<Cube> cubes, ArrayList<Player> players) {
        this.cubes = cubes;
        this.players = players;
    }
}
