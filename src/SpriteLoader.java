import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class SpriteLoader {

    //generate image based on lore from sprite, 80x80 pixels
    public static Image generate(String lore) throws IOException {
        Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "Attempting to load sprite \"" + lore + "\""));
        try (InputStream is = SpriteLoader.class.getResourceAsStream("resources/" + lore + ".png")) {
            if (is == null) {
                throw new NoItemFoundException("Item not found: " + lore);
            }

            Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "Loaded sprite \"" + lore + "\""));
            return ImageIO.read(is).getScaledInstance(80,80, 2);
        } catch (IOException e) {
            Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "[ERROR] Failed to load sprite \"" + lore + "\""));
            Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "[ERROR CODE] " + e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String lore) {
        return SpriteLoader.class.getResource("resources/" + lore + ".png") != null;
    }

    //generate the crafting inventory
    public static Image generateCraftingInventory() throws IOException {
        Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "Attempting to load sprite \"Crafting Inventory\""));
        try (InputStream is = SpriteLoader.class.getResourceAsStream("resources/crafting_table.png")) {
            if (is == null) {
                Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "Resource Not Found \"Crafting Inventory\""));
                throw new RuntimeException("Resource not found: " + "crafting table");
            }

            Main.logger.writeLog(new LogEntry("SPRITELOADER/INFO", "Loaded Sprite \"Crafting Inventory\""));
            return ImageIO.read(is).getScaledInstance(880,830, 2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image generateInventory() {
        try (InputStream is = SpriteLoader.class.getResourceAsStream("resources/" + "inventory" + ".png")) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + "inventory");
            }

            return ImageIO.read(is).getScaledInstance(960,660, 2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

