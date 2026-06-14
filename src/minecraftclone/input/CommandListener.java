package minecraftclone.input;

import minecraftclone.Main;
import minecraftclone.crafting.Recipe;
import minecraftclone.crafting.RecipeList;
import minecraftclone.inventory.InventoryFullException;
import minecraftclone.inventory.NoItemFoundException;
import minecraftclone.logging.LogEntry;
import minecraftclone.rendering.Camera;
import minecraftclone.rendering.SpriteLoader;
import minecraftclone.util.Vector;

import java.util.Arrays;
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
                    Main.logger.writeLog(new LogEntry("COMMANDLISTENER/INFO", "input: \"" + input + "\""));

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

                        if (!SpriteLoader.exists(parsedInput[1])) {
                            throw new NoItemFoundException("Item: " + parsedInput[1] + " not found");
                        }
                        Main.interactions.add(new Input("give", parsedInput[1], quantity));
                    } else if (parsedInput[0].equals("/clear") && parsedInput.length == 1) { //if it's a clear command without a specific item argument
                        Main.interactions.add(new Input("clear", null, -1));
                    } else if (parsedInput[0].equals("/clear") && parsedInput.length == 2) { //same thing but with a specific item argument
                        if (!SpriteLoader.exists(parsedInput[1])) {
                            throw new NoItemFoundException("Item: " + parsedInput[1] + " not found");
                        }
                       Main.interactions.add(new Input("clear", parsedInput[1], -1));
                    } else if (parsedInput[0].equals("help")) {
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
                        if (parsedInput.length < 2) {
                            throw new InvalidCommand("Missing Parameters");
                        }
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
                                Main.logger.writeLog(new LogEntry("CAMERA/INFO", "Position updated, " + Arrays.toString(Camera.getCoordinates())));
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
                        Camera.resetPos();
                    } else if (parsedInput[0].equals("/kill")){
                        if (parsedInput.length != 1) {
                            StringBuilder str = new StringBuilder();
                            for (int i = 1; i < parsedInput.length; i++) {
                                str.append(parsedInput[i]);
                                if (i == parsedInput.length-2)
                                    str.append(" ");
                            }
                            throw new InvalidCommand("Extraneous Parameters at \"..." + str + "\"");
                        } else {
                            System.out.println("Killing");
                            System.exit(0);
                        }
                    } else if (parsedInput[0].equals("/recipes")) {
                        if (parsedInput.length == 1) {
                            for (Recipe recipe : RecipeList.recipeList) {
                                System.out.println(recipe.getResult());
                            }
                        } else {
                            StringBuilder temp = new StringBuilder();
                            for (int i = 1; i < parsedInput.length; i++) {
                                temp.append(parsedInput[i]);
                                temp.append(" ");
                            }
                            temp = new StringBuilder(temp.substring(0, temp.length() - 1));
                            throw new InvalidCommand("Extraneous Parameter At \"..." + temp + "\"");
                        }
                    } else {
                        throw new InvalidCommand();
                    }
                } catch (InvalidCommand | NoItemFoundException | InventoryFullException e) {
                    System.out.println(e.getMessage());
                    try {
                        Main.logger.writeLog(new LogEntry("COMMANDLISTENER/ERROR", e.getMessage()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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
