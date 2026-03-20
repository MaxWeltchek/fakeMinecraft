import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class WorldBuilder {

    public WorldBuilder() {
        Random random = new Random();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            temp.append(random.nextInt(9) + 1);
        }
        int seed = Integer.parseInt(temp.toString());
    }

    public boolean validSeed(int seed) {
        int numDigits = 0;
        while (seed > 0) {
            numDigits++;
            seed/=10;
        }
        return (numDigits == 8);
    }

    public void flatWorld(ArrayList<Cube> cubes) throws IOException {
        Main.logger.writeLog(new LogEntry("WORLDBUILDER", "Flat world creation started"));
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cubes.add(new Cube(new Points(i*10, -7, j*10), 5));
            }
        }
        Main.logger.writeLog(new LogEntry("WORLDBUILDER", "Flat world generated (" + (System.currentTimeMillis() - startTime) + "ms)"));
    }
}
