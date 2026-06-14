package minecraftclone.rendering;

import java.util.Arrays;

public class Mesh {

    private Points[] vertices;
    //stored for use in setting rotation rather than adding rotation, generally updated when an object is transposed semi-permanently
    private Points[] originalVertices;
    //2d array of faces, each sub array has lists of indices in vertices array that belong together on one face
    private int[][] faces;
    //coordinates of the geometric center of the object, calculated by averaging the xyz coordinates of every vertex
    private double[] center;

    public Mesh(Points[] vertices_, int[][] faces_) {
        vertices = vertices_;
        //creates original vertices array that isn't a pointer and thus isn't updated when vertices get altered by additive rotation
        originalVertices = new Points[vertices_.length];
        for (int i = 0; i < vertices_.length; i++) {
            double[] coordinates = vertices_[i].getCoordinates();
            originalVertices[i] = new Points(coordinates[0], coordinates[1], coordinates[2]);
        }
        faces = faces_;
        //calculates center coordinates
        center = new double[3];
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < vertices.length; j++) {
                center[i] += vertices[j].getCoordinates()[i];
            }
        }
        for (int i = 0; i < 3; i ++) {
            center[i] /= vertices.length;
        }
    }

    public int[][] getFaces() {
        return faces;
    }

    public Points[] getVertices() {
        return vertices;
    }

    public Points getCenter() {
        return new Points(center[0], center[1], center[2]);
    }

    //applies rotation to all vertices of an object by a certain theta (radians)
    public void rotateX(double theta) {
        for (Points vertex : vertices) {
            vertex.rotateXAxis(theta, center);
        }
    }

    public void rotateY(double theta) {
        for (Points vertex : vertices) {
            vertex.rotateYAxis(theta, center);
        }
    }

    public void rotateZ(double theta) {
        for (Points vertex : vertices) {
            vertex.rotateZAxis(theta, center);
        }
    }

    //resets vertices to their locations pre rotation, this doesn't reset translational movement
    public void resetVertices() {
        for (int i = 0; i < vertices.length; i++) {
            double[] originalCoords = originalVertices[i].getCoordinates();
            vertices[i].setX(originalCoords[0]);
            vertices[i].setY(originalCoords[1]);
            vertices[i].setZ(originalCoords[2]);

        }
    }

    //updates center
    public void updateCenter() {
        for (int i = 0; i < 3; i ++) {
            center[i] = 0;
            for (int j = 0; j < vertices.length; j++) {
                center[i] += vertices[j].getCoordinates()[i];
            }
        }
        for (int i = 0; i < 3; i ++) {
            center[i] /= vertices.length;
        }
    }

    //sets rotation either in XY directions or XYZ
    public void setXYZRotation(double thetaX, double thetaY, double thetaZ) {
        resetVertices();
        rotateX(thetaX);
        rotateY(thetaY);
        rotateZ(thetaZ);
    }

    public void setRotationAroundAPoint(double thetaX, double thetaY, double thetaZ, Points point) {
        resetVertices();
        double[] temp = new double[3];
        System.arraycopy(center, 0, temp, 0, 3);
        center = point.getCoordinates();
        rotateX(thetaX);
        rotateY(thetaY);
        rotateZ(thetaZ);
        center = temp;
    }

    //transposes every vertex in an object by a certain distance, + or - in a standard 3d system, then updates the center to be consistent
    public void transposeX(int dist) {
        resetVertices();
        for (Points vertex : vertices) {
            vertex.moveX(dist);
        }
        originalVertices = new Points[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            double coordinates[] = vertices[i].getCoordinates();
            originalVertices[i] = new Points(coordinates[0], coordinates[1], coordinates[2]);
        }
        updateCenter();
    }

    public void transposeY(int dist) {
        resetVertices();
        for (Points vertex : vertices) {
            vertex.moveY(dist);
        }
        originalVertices = new Points[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            double coordinates[] = vertices[i].getCoordinates();
            originalVertices[i] = new Points(coordinates[0], coordinates[1], coordinates[2]);
        }
        updateCenter();
    }

    public void transposeZ(int dist) {
        resetVertices();
        for (Points vertex : vertices) {
            vertex.moveZ(dist);
        }
        originalVertices = new Points[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            double coordinates[] = vertices[i].getCoordinates();
            originalVertices[i] = new Points(coordinates[0], coordinates[1], coordinates[2]);
        }
        updateCenter();
    }

    //camera movement is simulated by moving the world around the camera, this does that by first resetting the vertices to their original locations, then
    //pushing each vertex in the xyx system by the inverse amount that the camera is away from the origin, this movement makes the cameras theoretical position
    //at the origin, even though its stored value is not. because of this we do not rotate the object around the camera but rather the origin
    public void updatePositionBasedOnCamera(Points cameraPos) {
        resetVertices();

        for (Points vertex : vertices) {
            vertex.moveX(-Camera.getCoordinates()[0]);
            vertex.moveY(-Camera.getCoordinates()[1]);
            vertex.moveZ(-Camera.getCoordinates()[2]);
        }
        double[] origin = {0,0,0};
        double[] temp = center.clone();
        center = origin;
        rotateY(Camera.getRotation()[1]);
        rotateX(Camera.getRotation()[0]);
        center = temp;
    }
}
