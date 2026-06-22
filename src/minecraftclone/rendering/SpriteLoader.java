package minecraftclone.rendering;

import minecraftclone.inventory.NoItemFoundException;
import minecraftclone.logging.LogHeaderType;
import minecraftclone.logging.LogEntry;
import minecraftclone.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class SpriteLoader {
    private final static String NAME = "SPRITELOADER";

    //generate image based on lore from sprite, 80x80 pixels
    public static Image generate(String lore) throws IOException {
        Logger.writeLog(new LogEntry(NAME, LogHeaderType.INFO, "Attempting to load sprite \"" + lore + "\""));
        try (InputStream is = SpriteLoader.class.getResourceAsStream("/resources/" + lore + ".png")) {
            if (is == null) {
                throw new NoItemFoundException("Item not found: " + lore);
            }

            Logger.writeLog(new LogEntry(NAME, LogHeaderType.INFO, "Loaded sprite \"" + lore + "\""));
            return ImageIO.read(is).getScaledInstance(80,80, 2);
        } catch (IOException e) {
            Logger.writeLog(new LogEntry(NAME, LogHeaderType.ERROR, "Failed to load sprite \"" + lore + "\""));
            Logger.writeLog(new LogEntry(NAME, LogHeaderType.ERROR, "Error code: " + e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String lore) {
        return SpriteLoader.class.getResource("/resources/" + lore + ".png") != null;
    }

    //generate the crafting inventory
    public static Image generateCraftingInventory() throws IOException {
        Logger.writeLog(new LogEntry(NAME, LogHeaderType.INFO, "Attempting to load sprite \"Crafting Inventory\""));
        try (InputStream is = SpriteLoader.class.getResourceAsStream("/resources/crafting_table.png")) {
            if (is == null) {
                Logger.writeLog(new LogEntry(NAME, LogHeaderType.ERROR, "Resource Not Found \"Crafting Inventory\""));
                throw new RuntimeException("Resource not found: " + "crafting table");
            }

            Logger.writeLog(new LogEntry(NAME, LogHeaderType.INFO, "Loaded Sprite \"Crafting Inventory\""));
            return ImageIO.read(is).getScaledInstance(880,830, 2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image generateInventory() {
        try (InputStream is = SpriteLoader.class.getResourceAsStream("/resources/" + "inventory" + ".png")) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + "inventory");
            }

            return ImageIO.read(is).getScaledInstance(960,660, 2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
