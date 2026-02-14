import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

//we keep a list of all unstackable items, if an item isn't in the list, then it's stackable
public class ItemRegistry {
    private static final HashMap<String, Boolean> registry = new HashMap<>();

    static {
        Scanner scan = new Scanner(Objects.requireNonNull(ItemRegistry.class.getResourceAsStream("/unstackable")));
        while (scan.hasNextLine()) {
            registry.put(scan.nextLine(), false);
        }
    }

    public static boolean isStackable(String itemLore) {
        return registry.getOrDefault(itemLore, true);
    }
}
