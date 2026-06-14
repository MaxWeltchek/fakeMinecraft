package minecraftclone.rendering;

import java.util.ArrayList;
public class Grid {
    private Points[][] vertices;
    private Points[] oneDimensionArrayVertices;
    private int[][] faces;
    private final Mesh gridMesh;

    public Grid(int width, int yCord, int numSquaresPerLine_) {
        int numVerticesPerLine = numSquaresPerLine_ + 1;
        int step = width/numSquaresPerLine_;
        Points[] vertices = new Points[(int) Math.pow(numSquaresPerLine_ + 1, 2)];

        for (int i = 0; i < numVerticesPerLine; i++) {
            for (int j = 0; j < numVerticesPerLine; j++) {
                vertices[j + i * numVerticesPerLine] = new Points(i * step, yCord, j * step);
            }
        }

        int[][] faces = new int[numSquaresPerLine_ * numSquaresPerLine_][4];
        for (int i = 0; i < numSquaresPerLine_ - 1; i++) {
            for (int j = 0; j < numSquaresPerLine_; j++) {
                int topLeft = i * numVerticesPerLine + j;
                faces[i * numSquaresPerLine_ + j] = new int[]{
                        topLeft,
                        topLeft + 1,
                        topLeft + 1 + numVerticesPerLine,
                        topLeft + numVerticesPerLine
                };            }
        }

        gridMesh = new Mesh(vertices, faces);
    }

    public Mesh getMesh() {
        return gridMesh;
    }
}
