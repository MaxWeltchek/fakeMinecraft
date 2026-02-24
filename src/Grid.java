import java.util.ArrayList;
public class Grid {
    private Points[][] vertices;
    private Points[] oneDimensionArrayVertices;
    private int[][] faces;
    private Mesh gridMesh;

//    public Grid(int width, int yCoord, int numSquaresPerLine_) {
//        int step = width/numSquaresPerLine_;
//        Points[][] vertices = new Points[numSquaresPerLine_ + 1][numSquaresPerLine_ + 1];
//        for (int i = 0; i < numSquaresPerLine_ + 1; i ++) {
//            for (int j = 0; j <= numSquaresPerLine_; j++) {
//                vertices[i][j] = new Points(i * step, yCoord, j* step);
//            }
//        }
//        ArrayList<Points> temp = new ArrayList<>();
//        for (int i = 0; i <= numSquaresPerLine_; i++) {
//            for (int j = 0; j <= numSquaresPerLine_; j++) {
//                temp.add(vertices[i][j]);
//            }
//        }
//        Points[] oneDimensionArrayVertices = new Points[temp.size()];
//        for (int i = 0; i < temp.size(); i++) {
//            oneDimensionArrayVertices[i] = temp.get(i);
//        }
//
//
//        int[][] faces = new int[numSquaresPerLine_ * numSquaresPerLine_][4];
//        for (int i = 0; i < numSquaresPerLine_; i++) {
//            for (int j = 0; j < numSquaresPerLine_; j++) {
//                faces[i + j * numSquaresPerLine_] = new int[]{temp.indexOf(vertices[i][j]), temp.indexOf(vertices[i][j + 1]), temp.indexOf(vertices[i + 1][j + 1]), temp.indexOf(vertices[i + 1][j])};
//            }
//        }
//
//        gridMesh = new Mesh(oneDimensionArrayVertices, faces);
//    }

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
                int tl = i * numVerticesPerLine + j;
                faces[i * numSquaresPerLine_ + j] = new int[]{
                        tl,
                        tl + 1,
                        tl + 1 + numVerticesPerLine,
                        tl + numVerticesPerLine
                };            }
        }

        gridMesh = new Mesh(vertices, faces);
    }

    public Mesh getMesh() {
        return gridMesh;
    }
}