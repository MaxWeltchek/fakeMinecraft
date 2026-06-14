package minecraftclone.rendering;

import minecraftclone.Main;
import minecraftclone.util.Vector;

import java.io.IOException;
import java.util.Arrays;

public class Camera {

    //holds only focal length and camera coordinates
    private static final int focalLength = 1700;
    private static final double[] coordinates = new double[] {0, 0, -50};
    private static final double[] rotation = new double[]{0, 0, Math.PI};

    public Camera() {
    }

    public static int getFocalLength() {
        return focalLength;
    }

    public static double[] getCoordinates() {
        return coordinates;
    }

    public static Points getLocation() {
        return new Points(coordinates[0], coordinates[1], coordinates[2]);
    }

    public static double[] getRotation() {
        return rotation;
    }

    public static void setRotation(double xTheta, double yTheta) {
        rotation[1] = yTheta;
        rotation[0] = xTheta;
        rotation[2] = 0;
    }

    //returns the results of a movement as a vector instead of applying immediately
    //still localizes vector to be in the direction you are pointing
    public static Vector calculateMovementVector(double spin) {
        double totalAngle = rotation[1] + spin;

        double dx = Math.sin(totalAngle) * Main.cameraMoveDist;
        double dz = Math.cos(totalAngle) * Main.cameraMoveDist;

        return new Vector(-dx, 0, dz);
    }

    public static void resetPos() {
        coordinates[0] = 0;
        coordinates[1] = 0;
        coordinates[2] = 0;
        rotation[0] = 0;
        rotation[1] = 0;
        rotation[2] = Math.PI;
    }

    public static void updatePosition(Vector movement) throws IOException {
        coordinates[0] += movement.getDirection()[0];
        coordinates[1] += movement.getDirection()[1];
        coordinates[2] += movement.getDirection()[2];}
}
