import java.security.Key;
import java.util.Arrays;

public class Camera {

    //holds only focal length and camera coordinates
    private static final int focalLength = 1700;
    private static double[] coordinates = new double[] {0, 0, -50};
    private static double[] rotation = new double[]{0, 0, Math.PI};

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

    public static double getX() { return coordinates[0]; }
    public static double getY() { return coordinates[1]; }
    public static double getZ() { return coordinates[2]; }

    public static void setCoordinates(double x_, double y_, double z_) {
        coordinates = new double[]{x_, y_, z_};
    }

    public static double[] getRotation() {
        return rotation;
    }

    public static void setRotation(double xTheta, double yTheta) {
        rotation[1] = yTheta;
        rotation[0] = xTheta;
        rotation[2] = 0;
    }

    //localizes movement vector to be based on camera rotation instead of being locked to global axes, i.e. you move in the direction you are pointed
    public static void calculateMovement(double spin) {
        double totalAngle = rotation[1] + spin;

        double dx = Math.sin(totalAngle) * KeyboardHandling.cameraMoveDist;
        double dz = Math.cos(totalAngle) * KeyboardHandling.cameraMoveDist;

        coordinates[0] -= dx;
        coordinates[2] += dz;
    }

    public static Vector calculateMovementVector(double spin) {
        double totalAngle = rotation[1] + spin;

        double dx = Math.sin(totalAngle) * KeyboardHandling.cameraMoveDist;
        double dz = Math.cos(totalAngle) * KeyboardHandling.cameraMoveDist;

        return new Vector(-dx, 0, dz);
    }

    public static void updatePosition(Vector movement) {
        coordinates[0] += movement.getDirection()[0];
        coordinates[1] += movement.getDirection()[1];
        coordinates[2] += movement.getDirection()[2];
    }
}
