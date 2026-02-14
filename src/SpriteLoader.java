import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class SpriteLoader {

    //generate image based on lore from sprite, 80x80 pixels
    public static Image generate(String lore) {
        try (InputStream is = SpriteLoader.class.getResourceAsStream("resources/" + lore + ".png")) {
            if (is == null) {
                throw new NoItemFoundException("Item not found: " + lore);
            }

            return ImageIO.read(is).getScaledInstance(80,80, 2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //generate the crafting inventory
    public static Image generateCraftingInventory() {
        try (InputStream is = SpriteLoader.class.getResourceAsStream("resources/crafting_table.png")) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + "crafting table");
            }

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

