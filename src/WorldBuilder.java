import java.util.ArrayList;
import java.util.Random;

public class WorldBuilder {
    private int seed;

    public WorldBuilder() {
        Random random = new Random();
        String temp = "";
        for (int i = 0; i < 8; i++) {
            temp += (random.nextInt(9) + 1);
        }
        int seed = Integer.parseInt(temp);
    }

    public boolean validSeed(int seed) {
        int numDigits = 0;
        while (seed > 0) {
            numDigits++;
            seed/=10;
        }
        return (numDigits == 8);
    }

    public void flatWorld(ArrayList<Cube> cubes) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cubes.add(new Cube(new Points(i*10, 0, j*10), 5));
            }
        }
    }
}
