///
///
/// classes: item (has coordinates, UUID, description, sprite, recipe), mouseHandling, helper class to assign sprites from resources folder into
/// bufferedImages for the item class.
///
/// premise: inventory + crafting screen with a hotkey
///
/// when "picking up" an item, start a animation event which will update the scale and relative position per frame until complete
/// ,this should be something in a item class like a method called animation(double scaleChange, int[] translation)
///
/// for crafting: store some basic unstackable, no easy way to add all, they are all shaped, and you just check if the crafting grid
/// int\[3]\[3] (values are UUIDs, at the end we can pull the actual item lore from a lore arrayList where UUID is index) if it is
/// there is some given return value. Actually add a recipe instance variable to each object (null if none), and just reference that
/// only check when there is an update in the crafting grid, when there is it's simply a O(n^2) where n is # of items.
/// there is only about 1000 items in the base game and far less in my program so we can throw compute at this problem
/// IMPORTANT: cannot check every frame since that would destroy performance, I believe an implementation would be to have a
/// "grid changed" bool flag, if the craftingGrid\[]\[] gets updated we set to true, after checking for possible unstackable we set to false.
///
/// if the "ghost" result is grabbed, just clear the crafting grid and make a new item (delete all items that are in the crafting grid, they are consumed)
/// edge cases like milk buckets in cakes will be ignored currently.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
    public static final int cameraMoveDist = 5;
    public static final double jumpDist = 10;


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

    public static final int[][] allSquareCenterCoords = new int[][]{
            //top inventory row indices 0-8
            {80, 460}, {170, 460}, {260, 460}, {350, 460}, {440, 460}, {530, 460}, {620, 460}, {710, 460}, {800, 460},
            //middle inventory row indices 9-17
            {80, 550}, {170, 550}, {260, 550}, {350, 550}, {440, 550}, {530, 550}, {620, 550}, {710, 550}, {800, 550},
            //bottom inventory row indices 18-26
            {80, 640}, {170, 640}, {260, 640}, {350, 640}, {440, 640}, {530, 640}, {620, 640}, {710, 640}, {800, 640},
            //hotbar, indices 27-35
            {80, 750}, {170, 750}, {260, 750}, {350, 750}, {440, 750}, {530, 750}, {620, 750}, {710, 750}, {800, 750},
            //all crafting grid squares are indices 36-44
            //crafting grid top row
            {190, 125}, {280, 125}, {370, 125},
            //crafting grid middle row
            {190, 215}, {280, 215}, {370, 215},
            //crafting grid bottom row
            {190, 305}, {280, 305}, {370, 305},
            //crafting output slot index 45
            {660, 215}
    };

    public static ArrayList<InventoryCell> inventoryCells = new ArrayList<>();


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
        MouseHandling mouseListener = new MouseHandling();
        KeyboardHandling keyListener = new KeyboardHandling();
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addKeyListener(keyListener);
        canvas.requestFocusInWindow();

        //initialize all inventory cells
        for (int[] coords : allSquareCenterCoords) {
            inventoryCells.add(new InventoryCell(coords));
        }

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

        Points[] gridVertices = {
                new Points(-100, -60, -100),
                new Points(-100, -60, 0),
                new Points(-100, -60, 100),
                new Points(0, -60, -100),
                new Points(0, -60, 0),
                new Points(0, -60, 100),
                new Points(100, -60, -100),
                new Points(100, -60, 0),
                new Points(100, -60, 100)
        };

        int[][] gridFaces = {
                {0, 1, 4, 3},
                {3, 4, 7, 6},
                {1, 2, 5, 4},
                {4, 5, 8, 7}
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

        Robot mouseMover = new Robot();

        Thread.sleep(500);

        bs = canvas.getBufferStrategy();
        craftingInventory = SpriteLoader.generateCraftingInventory();

        //load items
        inventoryCells.get(0).setItemInSlot(new Item("iron_ingot", inventoryCells.get(0).getCenterCoords(), 9, true));
        inventoryCells.get(1).setItemInSlot(new Item("copper_ingot", inventoryCells.get(1).getCenterCoords(), 1, true));
        inventoryCells.get(2).setItemInSlot(new Item("diamond", inventoryCells.get(2).getCenterCoords(), 10, true));
        inventoryCells.get(3).setItemInSlot(new Item("diamond", inventoryCells.get(3).getCenterCoords(), 3, true));
        inventoryCells.get(4).setItemInSlot(new Item("stick", inventoryCells.get(4).getCenterCoords(), 1, true));
        inventoryCells.get(5).setItemInSlot(new Item("iron_chestplate", inventoryCells.get(5).getCenterCoords(), 1, false));
        inventoryCells.get(6).setItemInSlot(new Item("iron_chestplate", inventoryCells.get(6).getCenterCoords(), 1, false));
        inventoryCells.get(7).setItemInSlot(new Item("iron_block", inventoryCells.get(7).getCenterCoords(), 1, true));

        //set in focus
        Graphics pen = bs.getDrawGraphics();

        //setup command listener
        CommandListener commandListener = new CommandListener();
        commandListener.start();

        pen.setFont(Font.createFont(Font.PLAIN, Objects.requireNonNull(Main.class.getResourceAsStream("resources/minecraft.ttf"))).deriveFont(18f));

        while (true) {

            //crafting grid loop
            if (inFocus == 1) {
                //if crafting grid has at least one non-null value TODO: make better than stupid spaghetti code
                //index 45 is the crafting result slot
                if (craftingGridUpdated && (inventoryCells.get(36).occupied() || inventoryCells.get(37).occupied() || inventoryCells.get(38).occupied() || inventoryCells.get(39).occupied() || inventoryCells.get(40).occupied() || inventoryCells.get(41).occupied() || inventoryCells.get(42).occupied() || inventoryCells.get(43).occupied() || inventoryCells.get(44).occupied())) {
                    //store all crafting grid values in a tempCrafting array to compare with known unstackable
                    String[] tempCrafting = new String[9];
                    for (int i = 36; i <= 44; i++) {
                        if (inventoryCells.get(i).occupied())
                            tempCrafting[i - 36] = inventoryCells.get(i).getItemInSlot().getLore();
                    }
                    //find the location of the recipe in the recipe list, -1 if it doesn't exist
                    int locationOfRecipe = RecipeList.recipeExists(tempCrafting);
                    //if it exists, put it in the slot otherwise clear the slot to get rid of past results
                    if (locationOfRecipe != -1) {
                        inventoryCells.get(45).setItemInSlot(new Item(RecipeList.recipeList.get(locationOfRecipe).getResult(), inventoryCells.get(45).getCenterCoords(), RecipeList.recipeList.get(locationOfRecipe).getQuantity(), RecipeList.recipeList.get(locationOfRecipe).isStackable()));
                    } else {
                        inventoryCells.get(45).clearSlot();
                    }
                    craftingGridUpdated = false;
                } else if (craftingGridUpdated) { //if no items in grid get rid of result
                    inventoryCells.get(45).clearSlot();
                    craftingGridUpdated = false;
                }


                //draw sprite and numbers for all images
                pen.drawImage(craftingInventory, 0, 0, null);
                pen.setColor(Color.BLACK);
                for (InventoryCell cell : inventoryCells) {
                    if (cell.occupied()) {
                        pen.drawImage(cell.getItemInSlot().getSprite(), cell.getItemInSlot().getCoords()[0] - itemDimension / 2, cell.getItemInSlot().getCoords()[1] - itemDimension / 2, null);
                        if (cell.getItemInSlot().getAmount() > 1)
                            pen.drawString(cell.getItemInSlot().getAmount() + "", cell.getItemInSlot().getCoords()[0] + 25, cell.getItemInSlot().getCoords()[1] + 38);
                    }
                }
                if (heldItem != null) {
                    pen.drawImage(heldItem.getSprite(), heldItem.getCoords()[0] - itemDimension / 2, heldItem.getCoords()[1] - itemDimension / 2, null);
                    if (heldItem.getAmount() > 1)
                        pen.drawString(heldItem.getAmount() + "", heldItem.getCoords()[0] + 25, heldItem.getCoords()[1] + 38);
                    heldItem.move(mouseCoords.clone());
                }
                bs.show();

                if (swap) {
                    swap = false;
                    break;
                }

                // ~100fps hard cap, likely lower
                Thread.sleep(10);
            }
            //world loop
            else if (inFocus == 0) {
                long lastTime = System.currentTimeMillis();
                while (true) {
                    long now = System.currentTimeMillis();
                    // rudimentary frame limiting
                    if (now - lastTime >= 1) {
                        clearScreen(pen);

                        draw(pen, groundGrid.getMesh());


                        bs.show();

                        //sets rotation
                        cube.updatePositionBasedOnCamera(Camera.getLocation());
                        groundGrid.getMesh().updatePositionBasedOnCamera(Camera.getLocation());

                        //movement
                        Vector[] movementVectors = new Vector[6];
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
//                        if (moveUp) {
//                            movementVectors[4] = new Vector(0, cameraMoveDist, 0);
//                        }
//                        if (moveDown) {
//                            movementVectors[5] = new Vector(0, -cameraMoveDist, 0);
//                        }

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
                        System.out.println(totalMovementVector);

                        if (!panicFlag) {
                            mouseListener.updateFakeMousePosition(mouseMover, canvas);
                        }

                        //swap logic
                        if (swap) {
                            swap = false;
                            break;
                        }

                        lastTime = now;
                        //cpu overhead
                        // ~100fps hard cap, likely lower
                        Thread.sleep(10);
                    }
                }
            }
        }
    }
    public static void decrementCraftingSlots() {
        for (int i = 36; i <= 44; i++) {
            if (inventoryCells.get(i).occupied()) {
                inventoryCells.get(i).getItemInSlot().decrement();
                if (inventoryCells.get(i).getItemInSlot().getAmount() <= 0) {
                    inventoryCells.get(i).clearSlot();
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
                int[] tempCoordinates1 = object.getVertices()[object.getFaces()[i][j]].castToXY();
                int[] tempCoordinates2 = object.getVertices()[object.getFaces()[i][j+1]].castToXY();
                drawLine(pen, tempCoordinates1, tempCoordinates2);

            }
            drawLine(pen, object.getVertices()[object.getFaces()[i][0]].castToXY(), object.getVertices()[object.getFaces()[i][object.getFaces()[i].length-1]].castToXY());
        }
    }

    //draws a single vertex
    public static void drawPoint(Graphics pen, Mesh object) {
        for (int i = 0; i < object.getVertices().length; i++)
            pen.fillOval(object.getVertices()[i].castToXY()[0] - circleRadius, object.getVertices()[i].castToXY()[1] - circleRadius,circleRadius*2, circleRadius*2);
    }

    //draws a line between two points
    public static void drawLine(Graphics pen, int[] coords1, int[] coords2) {
        pen.drawLine(coords1[0], coords1[1], coords2[0], coords2[1]);
    }
}