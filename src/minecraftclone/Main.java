package minecraftclone;

import minecraftclone.crafting.RecipeList;
import minecraftclone.input.CommandListener;
import minecraftclone.input.Input;
import minecraftclone.input.KeyboardHandling;
import minecraftclone.input.MouseHandling;
import minecraftclone.inventory.InventoryFullException;
import minecraftclone.inventory.Item;
import minecraftclone.inventory.ItemRegistry;
import minecraftclone.logging.LogEntry;
import minecraftclone.logging.Logger;
import minecraftclone.player.Player;
import minecraftclone.rendering.Camera;
import minecraftclone.rendering.Grid;
import minecraftclone.rendering.Mesh;
import minecraftclone.rendering.Points;
import minecraftclone.rendering.SpriteLoader;
import minecraftclone.util.Vector;
import minecraftclone.world.Cube;
import minecraftclone.world.WorldBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;


public class Main {
    //stuff for canvas
    public static Canvas canvas;
    public static BufferStrategy bs;
    public static BufferedImage baseState;
    public static Image inventory;
    public static Image craftingInventory;
    public static final int itemDimension = 80;
    public static Item heldItem;
    public static boolean craftingGridUpdated = true;
    public static ArrayList<Item> allItems = new ArrayList<>();
    public static int[] mouseCoords = new int[2];
    public static boolean shiftPressed;
    public static int mouseX, mouseY;
    public static int[] fakeMouseCoords;
    public static final int circleRadius = 4;
    public static double zRotation = 0;
    public static boolean panicFlag = false;
    //0 is world, 1 is craftingInv
    public static int inFocus = 0;
    public static boolean jumping = false;
    public static int jumpFrame = 0;
    public static final int totalJumpFrames = 59;
    public static final int numScreens = 2;
    public static volatile boolean swap = false;
    public static final int cameraMoveDist = 1;
    public static final double jumpDist = 10;
    public static int frameCount = 0;
    public static final long bootTime = System.currentTimeMillis();
    private static long lastFrameTime;
    public static Logger logger = new Logger();
    public static ConcurrentLinkedQueue<Input> interactions = new ConcurrentLinkedQueue<>();
    public static MouseHandling mouseListener = new MouseHandling();
    public static KeyboardHandling keyListener = new KeyboardHandling();
    public static Robot mouseMover;

    public static WorldBuilder worldBuilder = new WorldBuilder();
    public static ArrayList<Cube> blocks = new ArrayList<>();

    static {
        try {
            mouseMover = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }


    //movement variables
    public static boolean moveForward;
    public static boolean moveLeft;
    public static boolean moveBack;
    public static boolean moveRight;
    public static boolean moveUp;
    public static boolean moveDown;

    public static Mesh cube;
    public static Mesh pyramid;
    public static Grid groundGrid;

    public static Player max = new Player("Maxz");


    public static void main(String[] args) throws InterruptedException, IOException, FontFormatException, AWTException {
        canvas = new Canvas();
        canvas.setSize(new Dimension(880, 830));
        JFrame frame = new JFrame("inventory");
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.createBufferStrategy(3);
        //listeners
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addKeyListener(keyListener);
        canvas.requestFocusInWindow();

        //making shapes

        Points[] cubeVertices = {
                new Points(-50, 50, 10),
                new Points(-50, -50, 10),
                new Points(50, -50, 10),
                new Points(50, 50, 10),
                new Points(-50, 50, 110),
                new Points(-50, -50, 110),
                new Points(50, -50, 110),
                new Points(50, 50, 110)
        };

        int[][] cubeFaces = {
                {0, 1, 2 ,3},
                {3, 2, 6, 7},
                {7, 6, 5, 4},
                {4, 5, 1, 0},
                {0, 3, 7, 4},
                {1, 2, 6, 5}
        };

        Points[] pyramidVertices = {
                new Points(0, 110, 60),
                new Points(-50, 10, 10),
                new Points(50, 10, 10),
                new Points(-50, 10, 110),
                new Points(50, 10, 110)
        };

        int[][] pyramidFaces = {
                {0, 1, 2},
                {0, 2, 4},
                {0, 4, 3},
                {0, 3, 1},
                {1, 2, 4, 3}
        };

        cube = new Mesh(cubeVertices, cubeFaces);
        pyramid = new Mesh(pyramidVertices, pyramidFaces);
        groundGrid = new Grid(1000, -100, 50);

        //make base state
        baseState = new BufferedImage(880, 830, BufferedImage.TYPE_INT_RGB);
        Graphics baseStatePen = baseState.getGraphics();
        baseStatePen.setColor(Color.WHITE);
        baseStatePen.fillRect(0,0,880,830);
        baseStatePen.dispose();

        Thread.sleep(500);

        bs = canvas.getBufferStrategy();
        craftingInventory = SpriteLoader.generateCraftingInventory();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ArrayList<LogEntry> entries = new ArrayList<>();
            entries.add(new LogEntry("SYSTEM/INFO", "Runtime Statistics: Frame Count (" + frameCount + ")"));
            entries.add(new LogEntry("SYSTEM/INFO", "Runtime Statistics: Uptime (" + formatTime(System.currentTimeMillis()- bootTime) + ")"));
            entries.add(new LogEntry("SYSTEM/INFO", "Runtime Statistics: Average Frame Rate: " + frameCount/((System.currentTimeMillis() - bootTime)/1000.0)));
            entries.add(new LogEntry("SYSTEM/INFO", "Shutting down..."));
            for (LogEntry entry : entries) {
                try {
                    logger.writeLog(entry);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        //load items
        max.inventory.setItemInSlot(0, new Item("iron_ingot", max.inventory.getCenterCoords(0), 9, true));
        max.inventory.setItemInSlot(1, new Item("copper_ingot", max.inventory.getCenterCoords(1), 1, true));
        max.inventory.setItemInSlot(2, new Item("diamond", max.inventory.getCenterCoords(2), 10, true));
        max.inventory.setItemInSlot(3, new Item("diamond", max.inventory.getCenterCoords(3), 3, true));
        max.inventory.setItemInSlot(4, new Item("stick", max.inventory.getCenterCoords(4), 1, true));
        max.inventory.setItemInSlot(5, new Item("iron_chestplate", max.inventory.getCenterCoords(5), 1, false));
        max.inventory.setItemInSlot(6, new Item("iron_chestplate", max.inventory.getCenterCoords(6), 1, false));
        max.inventory.setItemInSlot(7, new Item("iron_block", max.inventory.getCenterCoords(7), 1, true));

        //make world
        worldBuilder.flatWorld(blocks);

        //setup command listener
        CommandListener commandListener = new CommandListener();
        commandListener.start();

        //actual rendering with scheduled frames
        ScheduledExecutorService renderer = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> renderTask = renderer.scheduleAtFixedRate(() -> {
            try {
                Main.frameTick();
            } catch (IOException | FontFormatException e) {
                try {
                    logger.writeLog(new LogEntry("SYSTEM/INFO", "FATAL: An unknown error occurred"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, 0, 14, TimeUnit.MILLISECONDS);
        Thread.sleep(Integer.MAX_VALUE);

    }

    public static void frameTick() throws IOException, FontFormatException {

        Graphics pen = bs.getDrawGraphics();
        pen.setFont(Font.createFont(Font.PLAIN, Objects.requireNonNull(Main.class.getResourceAsStream("/resources/minecraft.ttf"))).deriveFont(18f));
        {

            //crafting grid loop
            if (inFocus == 1) {
                //if crafting grid has at least one non-null value TODO: make better than stupid spaghetti code
                //index 45 is the crafting result slot
                if (craftingGridUpdated && (max.inventory.isOccupied(36) || max.inventory.isOccupied(37) || max.inventory.isOccupied(38) || max.inventory.isOccupied(39) || max.inventory.isOccupied(40) || max.inventory.isOccupied(41) || max.inventory.isOccupied(42) || max.inventory.isOccupied(43) || max.inventory.isOccupied(44))) {
                    //store all crafting grid values in a tempCrafting array to compare with known unstackable
                    String[] tempCrafting = new String[9];
                    for (int i = 36; i <= 44; i++) {
                        if (max.inventory.isOccupied(i))
                            tempCrafting[i - 36] = max.inventory.getItemInSlot(i).getLore();
                    }
                    //find the location of the recipe in the recipe list, -1 if it doesn't exist
                    int locationOfRecipe = RecipeList.recipeExists(tempCrafting);
                    //if it exists, put it in the slot otherwise clear the slot to get rid of past results
                    if (locationOfRecipe != -1) {
                        max.inventory.setItemInSlot(45, new Item(RecipeList.recipeList.get(locationOfRecipe).getResult(), max.inventory.getCenterCoords(45), RecipeList.recipeList.get(locationOfRecipe).getQuantity(), RecipeList.recipeList.get(locationOfRecipe).isStackable()));
                    } else {
                        max.inventory.clearSlot(45);
                    }
                    craftingGridUpdated = false;
                } else if (craftingGridUpdated) { //if no items in grid get rid of result
                    max.inventory.clearSlot(45);
                    craftingGridUpdated = false;
                }

                while (!interactions.isEmpty()) {
                    executeInputs(interactions.poll());
                }


                //draw sprite and numbers for all images
                pen.drawImage(craftingInventory, 0, 0, null);
                pen.setColor(Color.BLACK);
                Item[] tempCopyOfAllItemsInInventory = max.inventory.getAllItems();
                for (Item item : tempCopyOfAllItemsInInventory) {
                    if (item != null) {
                        pen.drawImage(item.getSprite(), item.getCoords()[0] - itemDimension / 2, item.getCoords()[1] - itemDimension / 2, null);
                        if (item.getAmount() > 1)
                            pen.drawString(item.getAmount() + "", item.getCoords()[0] + 25, item.getCoords()[1] + 38);
                    }
                }
                if (heldItem != null) {
                    pen.drawImage(heldItem.getSprite(), heldItem.getCoords()[0] - itemDimension / 2, heldItem.getCoords()[1] - itemDimension / 2, null);
                    if (heldItem.getAmount() > 1)
                        pen.drawString(heldItem.getAmount() + "", heldItem.getCoords()[0] + 25, heldItem.getCoords()[1] + 38);
                    heldItem.move(mouseCoords.clone());
                }
                bs.show();
            }
            //world loop
            else if (inFocus == 0) {
                clearScreen(pen);

//                        draw(pen, groundGrid.getMesh());
                for (Cube cube : blocks) {
                    draw(pen, cube.getMesh());
                    cube.getMesh().updatePositionBasedOnCamera(Camera.getLocation());
                }


                bs.show();

                //sets rotation
                cube.updatePositionBasedOnCamera(Camera.getLocation());
//                        groundGrid.getMesh().updatePositionBasedOnCamera(Camera.getLocation());

                //movement
                Vector[] movementVectors = new Vector[4];
                Vector jumpVector = null;
                Vector totalMovementVector = new Vector(0,0,0);
                if (moveForward) {
                    movementVectors[0] = Camera.calculateMovementVector(0);
                }
                if (moveLeft) {
                    movementVectors[1] = Camera.calculateMovementVector(Math.PI/2.0);
                }
                if (moveBack) {
                    movementVectors[2] = Camera.calculateMovementVector(Math.PI);
                }
                if (moveRight) {
                    movementVectors[3] = Camera.calculateMovementVector(Math.PI/-2.0);
                }

                if (jumping) {
                    if (jumpFrame/30 == 0) {
                        jumpVector = new Vector(0, jumpDist/((totalJumpFrames+1)/2.0), 0);
                        jumpFrame++;
                    } else if (jumpFrame == totalJumpFrames) {
                        jumpVector = new Vector(0, -jumpDist/((totalJumpFrames+1)/2.0), 0);
                        jumping = false;
                        jumpFrame = 0;
                    } else if (jumpFrame/30 == 1) {
                        jumpVector = new Vector(0, -jumpDist/((totalJumpFrames+1)/2.0), 0);
                        jumpFrame++;
                    }
                }

                totalMovementVector = Vector.addVectors(movementVectors);
                totalMovementVector.truncate(cameraMoveDist);
                if (jumpVector != null) {
                    totalMovementVector.add(jumpVector);
                }
                Camera.updatePosition(totalMovementVector);

                if (!panicFlag) {
                    mouseListener.updateFakeMousePosition(mouseMover, canvas);
                }

                frameCount++;
            }
        }
    }

    public static void decrementCraftingSlots() {
        for (int i = 36; i <= 44; i++) {
            if (max.inventory.isOccupied(i)) {
                max.inventory.getItemInSlot(i).decrement();
                if (max.inventory.getItemInSlot(i).getAmount() <= 0) {
                    max.inventory.clearSlot(i);
                }
            }
        }
    }

    //clears screen to 20,20,20 RGB background
    public static void clearScreen(Graphics pen) {
        pen.drawImage(baseState, 0, 0, null);
    }

    public static void clearScreen() {
        Graphics pen = bs.getDrawGraphics();
        pen.drawImage(baseState, 0, 0, null);
    }

    //draws a line in order of points on a face then from the first to the last point on a face, repeated for all faces
    public static void draw(Graphics pen, Mesh object) {
        for (int i = 0; i < object.getFaces().length; i++) {
            for (int j = 0 ; j < object.getFaces()[i].length-1; j++) {
                Points point1 = object.getVertices()[object.getFaces()[i][j]];
                Points point2 = object.getVertices()[object.getFaces()[i][j + 1]];
                if (point1.getCoordinates()[2] <= Points.NEAR_CLIP_Z || point2.getCoordinates()[2] <= Points.NEAR_CLIP_Z) {
                    continue;
                }
                int[] tempCoordinates1 = point1.castToXY();
                int[] tempCoordinates2 = point2.castToXY();
                drawLine(pen, tempCoordinates1, tempCoordinates2);

            }
            Points closingPoint1 = object.getVertices()[object.getFaces()[i][0]];
            Points closingPoint2 = object.getVertices()[object.getFaces()[i][object.getFaces()[i].length - 1]];
            if (closingPoint1.getCoordinates()[2] > Points.NEAR_CLIP_Z && closingPoint2.getCoordinates()[2] > Points.NEAR_CLIP_Z) {
                drawLine(pen, closingPoint1.castToXY(), closingPoint2.castToXY());
            }
        }
    }

    //draws a single vertex
    public static void drawPoint(Graphics pen, Mesh object) {
        for (int i = 0; i < object.getVertices().length; i++) {
            if (object.getVertices()[i].getCoordinates()[2] <= Points.NEAR_CLIP_Z) {
                continue;
            }
            int[] projectedPoint = object.getVertices()[i].castToXY();
            pen.fillOval(projectedPoint[0] - circleRadius, projectedPoint[1] - circleRadius, circleRadius * 2, circleRadius * 2);
        }
    }

    //draws a line between two points
    public static void drawLine(Graphics pen, int[] coords1, int[] coords2) {
        pen.drawLine(coords1[0], coords1[1], coords2[0], coords2[1]);
    }
    
    public static void executeInputs(Input input) throws IOException {
        switch (input.getAction()) {
            case ("give"):
                give(input.getLore(), input.getQuantity());
                break;
            case ("clear"):
                clear(input.getLore());
                break;
            default:
        }
    }
    
    public static void give(String item, int quantity) throws InventoryFullException, IOException {
        //track how many items left
        int itemsLeftToGrant = quantity;

        for (int i = 0; i < 36; i++) {
            if (!max.inventory.isOccupied(i) && itemsLeftToGrant > 0) { //if the slot is empty, and we have items left to give, give them more items in that slot
                max.inventory.setItemInSlot(i, new Item(item, max.inventory.getCenterCoords(i), (ItemRegistry.isStackable((item)) ? Math.min(itemsLeftToGrant, 64) : 1), ItemRegistry.isStackable(item)));
                //if stackable then give up to 64 then move on
                if (ItemRegistry.isStackable(item)) {
                    itemsLeftToGrant -= 64;
                } else {
                    itemsLeftToGrant--;
                }
                //otherwise of the slot isn't empty but has the same lore and stackable (i.e. same item) do the same thing but stack them
            } else if (max.inventory.isOccupied(i) && max.inventory.getItemInSlot(i).getLore().equals(item) && itemsLeftToGrant > 0 && max.inventory.getItemInSlot(i).getAmount() < 64) {
                int temp = max.inventory.getItemInSlot(i).getAmount();
                max.inventory.getItemInSlot(i).setAmount(Math.min(64, max.inventory.getItemInSlot(i).getAmount() + itemsLeftToGrant));
                itemsLeftToGrant -= max.inventory.getItemInSlot(i).getAmount() - temp;
            }
        }
        if (itemsLeftToGrant > 0) { //if not all were given (inv full), say how many were given
            throw new InventoryFullException(item, quantity - itemsLeftToGrant);
        } else {
            logger.writeLog(new LogEntry("INTERACTIONS/INFO", "Gave " + quantity + " " + item.split("_")[0] + (item.split("_").length > 1 ? " " + item.split("_")[1] : "") + (quantity > 1 ? "s" : "")));
            System.out.println("Gave " + quantity + " " + item.split("_")[0] + (item.split("_").length > 1 ? " " + item.split("_")[1] : "") + (quantity > 1 ? "s" : ""));
        }
    }

    public static void clear(String item) throws IOException {
        if (item == null) { //if no specified item, clear everything
            //track #of cleared items
            int amountOfItemsCleared = 0;
            for (int i = 0; i < 36; i++) {
                //ternery to fix null pointer with short-circuiting
                amountOfItemsCleared += (Main.max.inventory.isOccupied(i) ? Main.max.inventory.getItemInSlot(i).getAmount() : 0);
                Main.max.inventory.clearSlot(i);
            }
            //send cleared message
            System.out.println("Cleared " + amountOfItemsCleared + " item" + (amountOfItemsCleared != 1 ? "s" : ""));
            logger.writeLog(new LogEntry("INTERACTIONS/INFO", "Cleared " + amountOfItemsCleared + " item" + (amountOfItemsCleared != 1 ? "s" : "")));
        } else {
            int amountOfItemsCleared = 0;
            for (int i = 0; i < 36; i++) {
                if (Main.max.inventory.isOccupied(i) && Main.max.inventory.getItemInSlot(i).getLore().equals(item)) {
                    amountOfItemsCleared += Main.max.inventory.getItemInSlot(i).getAmount();
                    Main.max.inventory.clearSlot(i);
                }
            }
            //build the cleared message to include lore, for loop is to handle multiple cases of lore1_lore2_lore3 etc
            String[] parsedItemLore = item.split("_");
            StringBuilder clearedMessage = new StringBuilder("Cleared " + amountOfItemsCleared);
            for (String loreChunk : parsedItemLore) {
                clearedMessage.append(" ").append(loreChunk);
            }
            clearedMessage.append((amountOfItemsCleared != 0 ? "s" : ""));
            System.out.println(clearedMessage);
        }
    }

    public static String formatTime(long time) {
        long totalSeconds = time / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;
        long hours = ((totalSeconds / 3600) % 24);

        return String.format("[%02d:%02d:%02d]", hours, minutes, seconds);
    }
}
