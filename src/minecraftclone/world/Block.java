package minecraftclone.world;

import minecraftclone.inventory.NoItemFoundException;
import minecraftclone.rendering.SpriteLoader;

import java.awt.*;
import java.io.IOException;

public class Block {
    private Cube cube;
    private String blockType;
    private Image sprite;

    public Block(Cube cube, String blockType) throws NoItemFoundException {
        this.cube = cube;
        this.blockType = blockType;
        try {
            sprite = SpriteLoader.generate(blockType);
        } catch (IOException e) {
            throw new NoItemFoundException("Sprite not found");
        }
    }
}
