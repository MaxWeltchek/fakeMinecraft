import java.util.ArrayList;

public class Vector {
    private double[] direction;

    public Vector(double x, double y, double z) {
        direction = new double[]{x,y,z};
    }

    public double[] getDirection() {
        return direction;
    }

    public static Vector addVectors(Vector[] vectors) {
        double x = 0, y = 0, z = 0;
        for (int i = 0; i < vectors.length; i++) {
            if (vectors[i] != null) {
                Vector vector = vectors[i];
                x += vector.getDirection()[0];
                y += vector.getDirection()[1];
                z += vector.getDirection()[2];
            }
        }
        return new Vector(x,y,z);
    }

    public void add(Vector vector) {
        direction[0] += vector.getDirection()[0];
        direction[1] += vector.getDirection()[1];
        direction[2] += vector.getDirection()[2];
    }

    public void truncate(double length) {
        double magnitude = Math.sqrt(Math.pow(direction[0],2) + Math.pow(direction[1],2) + Math.pow(direction[2],2));
        if (magnitude == 0) {
            return;
        }
        double scale = length/magnitude;
        direction[0] = direction[0] * scale;
        direction[1] = direction[1] * scale;
        direction[2] = direction[2] * scale;
    }

    public String toString() {
        return "x: " + direction[0] + "  y: " + direction[1] + "  z: " + direction[2];
    }
}
