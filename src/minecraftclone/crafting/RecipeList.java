package minecraftclone.crafting;

import minecraftclone.inventory.ItemRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RecipeList {
    public static ArrayList<Recipe> recipeList =  new ArrayList<>();

    static {
        try {
            updateRecipeList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateRecipeList() throws FileNotFoundException {
        recipeList.clear();
        Scanner scan = new Scanner(new File("src/recipes"));
        while (scan.hasNextLine()) {
            String[] parsed = scan.nextLine().split(",");
            String[] tempRecipe = new String[9];
            for (int i = 0; i < 9; i++) {
                if (parsed[i+1].equals("empty")) {
                    tempRecipe[i] = null;
                } else {
                    tempRecipe[i] = parsed[i+1];
                }
            }


            recipeList.add(new Recipe(parsed[0], tempRecipe, Integer.parseInt(parsed[10]), ItemRegistry.isStackable(parsed[0])));
        }
    }

    public static int recipeExists(String[] input) {
        for (int i = 0; i < recipeList.size(); i++) {
            if (recipeList.get(i).checkRecipe(input)) {
                return i;
            }
        }
        return -1;
    }
}
