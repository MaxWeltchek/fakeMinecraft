import java.util.Scanner;
import java.net.URL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class CommandListener {
    private volatile boolean running = false;
    private Thread listenerThread;

    public void start() throws InvalidCommand{
        running = true;

        listenerThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (running) {
                try {
                    String input = scanner.nextLine();
                    String[] parsedInput = input.split(" ");

                    if (parsedInput[0].equals("/give")) {
                        if (parsedInput.length == 1) {
                            throw new InvalidCommand("Missing argument at \"/give ____\" <--");
                        }
                        //if the command had 3 parts, /give, item, amount, set amount to 3rd part, if not set to 1
                        int quantity;
                        if (parsedInput.length >= 3) {
                            quantity = Integer.parseInt(parsedInput[2]);
                        } else {
                            quantity = 1;
                        }
                        if (quantity <= 0) {
                            throw new InvalidCommand("Invalid quantity");
                        }

                        //track how many items left
                        int itemsLeftToGrant = quantity;

                        for (int i = 0; i < 36; i++) {
                            if (!Main.inventoryCells.get(i).occupied() && itemsLeftToGrant > 0) { //if the slot is empty, and we have items left to give, give them more items in that slot
                                Main.inventoryCells.get(i).setItemInSlot(new Item(parsedInput[1], Main.inventoryCells.get(i).getCenterCoords(), (ItemRegistry.isStackable((parsedInput[1])) ? Math.min(itemsLeftToGrant, 64) : 1), ItemRegistry.isStackable(parsedInput[1])));
                                //if stackable then give up to 64 then move on
                                if (ItemRegistry.isStackable(parsedInput[1])) {
                                    itemsLeftToGrant -= 64;
                                } else {
                                    itemsLeftToGrant--;
                                }
                                //otherwise of the slot isn't empty but has the same lore and stackable (i.e. same item) do the same thing but stack them
                            } else if (Main.inventoryCells.get(i).occupied() && Main.inventoryCells.get(i).getItemInSlot().getLore().equals(parsedInput[1]) && itemsLeftToGrant > 0 && Main.inventoryCells.get(i).getItemInSlot().getAmount() < 64) {
                                int temp = Main.inventoryCells.get(i).getItemInSlot().getAmount();
                                Main.inventoryCells.get(i).getItemInSlot().setAmount(Math.min(64, Main.inventoryCells.get(i).getItemInSlot().getAmount() + itemsLeftToGrant));
                                itemsLeftToGrant -= Main.inventoryCells.get(i).getItemInSlot().getAmount() - temp;
                            }
                        }
                        if (itemsLeftToGrant > 0) { //if not all were given (inv full), say how many were given
                            throw new InventoryFullException(parsedInput[1], quantity - itemsLeftToGrant);
                        } else {
                            System.out.println("Gave " + quantity + " " + parsedInput[1].split("_")[0] + (parsedInput[1].split("_").length > 1 ? " " + parsedInput[1].split("_")[1] : "") + (quantity > 1 ? "s" : ""));
                        }

                    } else if (parsedInput[0].equals("/clear") && parsedInput.length == 1) { //if it's a clear command without a specific item argument
                        //track #of cleared items
                        int amountOfItemsCleared = 0;
                        for (int i = 0; i < 36; i++) {
                            //ternery to fix null pointer with short-circuiting
                            amountOfItemsCleared += (Main.inventoryCells.get(i).occupied() ? Main.inventoryCells.get(i).getItemInSlot().getAmount() : 0);
                            Main.inventoryCells.get(i).clearSlot();
                        }
                        //send cleared message
                        System.out.println("Cleared " + amountOfItemsCleared + " item" + (amountOfItemsCleared != 1 ? "s" : ""));
                    } else if (parsedInput[0].equals("/clear") && parsedInput.length == 2) { //same thing but with a specific item argument
                        int amountOfItemsCleared = 0;
                        for (int i = 0; i < 36; i++) {
                            if (Main.inventoryCells.get(i).occupied() && Main.inventoryCells.get(i).getItemInSlot().getLore().equals(parsedInput[1])) {
                                amountOfItemsCleared += Main.inventoryCells.get(i).getItemInSlot().getAmount();
                                Main.inventoryCells.get(i).clearSlot();
                            }
                        }
                        //build the cleared message to include lore, for loop is to handle multiple cases of lore1_lore2_lore3 etc
                        String[] parsedItemLore = parsedInput[1].split("_");
                        StringBuilder clearedMessage = new StringBuilder("Cleared " + amountOfItemsCleared);
                        for (String loreChunk : parsedItemLore) {
                            clearedMessage.append(" ").append(loreChunk);
                        }
                        //add plural if not 1
                        clearedMessage.append((amountOfItemsCleared != 0 ? "s" : ""));
                        System.out.println(clearedMessage);
                    } else if (parsedInput[0].equals("/help")) {
                        helpMessage();
                    } else if (parsedInput[0].equals("/addrecipe")) {
                        String recipeRegex = parsedInput[1];
                        try {
                            addRecipe(recipeRegex);
                            RecipeList.updateRecipeList();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (parsedInput[0].equals("/movecamera")) {
                        try {
                            if (parsedInput[1].equals("up")) {
                                if (parsedInput.length > 2) {
                                    Camera.updatePosition(new Vector(0, Integer.parseInt(parsedInput[2]), 0));
                                } else {
                                    Camera.updatePosition(new Vector(0, 30, 0));
                                }
                            } else if (parsedInput[1].equals("down")) {
                                if (parsedInput.length > 2) {
                                    Camera.updatePosition(new Vector(0, -Integer.parseInt(parsedInput[2]), 0));
                                } else {
                                    Camera.updatePosition(new Vector(0, -30, 0));
                                }
                            } else {
                                try {
                                    Integer.parseInt(parsedInput[1]);
                                    throw new InvalidCommand("Missing argument at \"/movecamera ____ " + parsedInput[1] + "\"");
                                } catch (NumberFormatException e) {
                                    throw new InvalidCommand("Valid move camera arguments are \"up\" or \"down\"");
                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new InvalidCommand("Not a number");
                        }
                    } else if (parsedInput[0].equals("/resetcamera")) {
                        try {
                            Camera.resetPos();
                        } catch (Exception e) {
                            throw new InvalidCommand();
                        }
                    } else {
                        throw new InvalidCommand();
                    }
                } catch (InvalidCommand | NoItemFoundException | InventoryFullException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Command failed " + e.getMessage());
                }
            }
            scanner.close();
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void stop() {
        running = false;
        if (listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
    }

    public void helpMessage() {
        System.out.println("Command List, arguments in [] are optional");
        System.out.println("/help (this menu)");
        System.out.println("/give item [quantity]");
        System.out.println("/clear [item]");
        System.out.println("/movecamera (up/down) [distance]");
        System.out.println("/resetcamera");
    }

    private void addRecipe(String regex) throws NoItemFoundException, IOException {
        int validity = validRecipe(regex);
        if (validity == 201) {
            System.out.println("Invalid Recipe (wrong command length)");
        } else if (validity == 202) {
            throw new NoItemFoundException("No item (result) found");
        } else if (validity == 203) {
            throw new RuntimeException("Invalid Quantity");
        } else if (validity != 200) {
            throw new RuntimeException("Unknown Error");
        } else {
            Path path = Path.of( "src", "recipes");
            Files.writeString(
                    path,
                    "\n",
                    StandardOpenOption.APPEND
            );
            Files.writeString(
                    path,
                    regex,
                    StandardOpenOption.APPEND
            );
            RecipeList.updateRecipeList();
            System.out.println("Recipe added");
        }
    }

    private int validRecipe(String regex) {
        String[] regexAsArray = regex.split(",");
        if (regexAsArray.length !=  11) {
            //invalid length
            return 201;
        }
        URL resource;
        for (int i = 0; i < 10; i++) {
            resource = Main.class.getClassLoader().getResource("resources/" + regexAsArray[0] + ".png");
            if (resource == null) {
                //result not found
                return 202;
            }
        }
        try {
            Integer.parseInt(regexAsArray[10]);
        } catch (NumberFormatException e) {
            return 203;
        }
        return 200;
    }
}
