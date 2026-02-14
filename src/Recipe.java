import java.util.Arrays;

public class Recipe {
    private final String result;
    private final String[] recipe;
    private final int quantity;
    private final boolean stackable;

    public Recipe(String result_, String[] recipe_, int quantity_, boolean stackable_) {
        result = result_;
        recipe = recipe_;
        quantity = quantity_;
        stackable = stackable_;
    }

    public String getResult() {return result;}
    public int getQuantity() {return quantity;}
    public boolean isStackable() {return stackable;}
    public boolean checkRecipe(String[] input) {return Arrays.equals(input, recipe);}
    //debug purposes
    public String[] queryRecipe() {return recipe;}
}
