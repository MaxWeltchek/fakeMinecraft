package minecraftclone.rendering;

public class Points {
    public static final double NEAR_CLIP_Z = 0.01;
    //contains xyz coordinates for a given point
    private double[] coordinates;

    public Points(double x_, double y_, double z_) {
        coordinates = new double[]{x_, y_, z_};
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    //rasterizes the coordinates of a point to be projected onto a 2d plane, based on focal length and distance from camera
    private double[] transposeToXY() {
        //creates location to store rasterized coordinates
        double[] XYCoordinates = new double[2];

        /*/ clipping (confusing to me)
        because of how transforming the coordinates work the z coordinate of an object is always its distance away from the camera in
        the horizontal plane (ignoring camera height), this means if the z coordinate is too small we set other coords to be
        "infinite" and thus unrendered. the exact value can be changed
        Potential improvements:
        instead of calculating clipping at render time, calculate if an object should be clipped before drawing, skip that vertex, vertex
        pair, or line if it should be to improve performance
         */
        if (coordinates[2] <= NEAR_CLIP_Z) {
            XYCoordinates[0] = Double.MAX_VALUE;
            XYCoordinates[1] = Double.MAX_VALUE;
            return XYCoordinates;
        }

        //calculates coordinates in the XY plane, based on focal length and distance from the camera
        XYCoordinates[0] = coordinates[0] * (Camera.getFocalLength() / coordinates[2]);
        XYCoordinates[1] = coordinates[1] * (Camera.getFocalLength() / coordinates[2]);
        return XYCoordinates;
    }

    //full casting method, both rasterizes and translates from 0,0 origin to 400, 400 java canvas origin
    public int[] castToXY() {
        double[] tempDouble = transposeToXY();
        int[] tempInt = {(int) tempDouble[0], (int) tempDouble[1]};
        tempInt[0] += 400;
        tempInt[1] = (tempInt[1] *-1) + 400;
        return tempInt;
    }

    //moves a given point in a given direction by a given distance
    public void moveZ(double dist) { coordinates[2] += dist; }

    public void moveY(double dist) { coordinates[1] += dist; }

    public void moveX(double dist) { coordinates[0] += dist; }

    //rotation methods in each axis, uses standard 3d rotation matrices and multiples them
    public void rotateXAxis(double theta, double[] center) {
        for (int i = 0; i < 3; i++) {
            coordinates[i] -= center[i];
        }
        double[][] rotationMatrix = new double[3][3];
        rotationMatrix[0] = new double[]{1.0, 0.0, 0.0};
        rotationMatrix[1] = new double[]{0.0, Math.cos(theta), -1.0 * Math.sin(theta)};
        rotationMatrix[2] = new double[]{0.0, Math.sin(theta), Math.cos(theta)};
        coordinates = multiplyMatrix(rotationMatrix, coordinates);
        for (int i = 0; i < 3; i++) {
            coordinates[i] += center[i];
        }
    }

    public void rotateYAxis(double theta, double[] center) {
        for (int i = 0; i < 3; i++) {
            coordinates[i] -= center[i];
        }
        double[][] rotationMatrix = new double[3][3];
        rotationMatrix[0] = new double[]{Math.cos(theta), 0.0, Math.sin(theta)};
        rotationMatrix[1] = new double[]{0.0, 1.0, 0.0};
        rotationMatrix[2] = new double[]{-1.0 * Math.sin(theta), 0.0, Math.cos(theta)};
        coordinates = multiplyMatrix(rotationMatrix, coordinates);
        for (int i = 0; i < 3; i++) {
            coordinates[i] += center[i];
        }
    }

    public void rotateZAxis(double theta, double[] center) {
        for (int i = 0; i < 3; i++) {
            coordinates[i] -= center[i];
        }
        double[][] rotationMatrix = new double[3][3];
        rotationMatrix[0] = new double[]{Math.cos(theta), -1.0 * Math.sin(theta), 0.0};
        rotationMatrix[1] = new double[]{Math.sin(theta), Math.cos(theta), 0.0};
        rotationMatrix[2] = new double[]{0.0, 0.0, 1.0};
        coordinates = multiplyMatrix(rotationMatrix, coordinates);
        for (int i = 0; i < 3; i++) {
            coordinates[i] += center[i];
        }
    }

    //helper matrix multiplication method
    //where transformationMatrix is a 3x3 transformation matrix
    private double[] multiplyMatrix(double[][] transformationMatrix, double[] coordinates) {
        double[] tempCords = new double[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tempCords[i] += transformationMatrix[i][j] * coordinates[j];
            }
        }
        return tempCords;
    }

    public void setX(double x_) {
        coordinates[0] = x_;
    }

    public void setY(double y_) {
        coordinates[1] = y_;
    }

    public void setZ(double z_) {
        coordinates[2] = z_;
    }

}
