public class Cube extends Shapes{
    private final int[][] faces;
    private final Mesh mesh;
    public Cube(Points[] vertices) {
        super(vertices);
        faces = new int[][] {
                {0, 2, 3, 1},
                {4, 5, 7, 6},
                {0, 1, 5, 4},
                {2, 6, 7, 3},
                {0, 4, 6, 2},
                {1, 3, 7, 5}
        };
        mesh = new Mesh(vertices, faces);
    }

    public Cube(Points center, int radius) {
        Points[] vertices = new Points[8];
        int i = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    vertices[i++] = new Points(
                            center.getCoordinates()[0] + (x == 0 ? radius: -radius),
                            center.getCoordinates()[1] + (y == 0 ? radius: -radius),
                            center.getCoordinates()[2] + (z == 0 ? radius: -radius)
                    );
                }
            }
        }
        super(vertices);
        faces = new int[][] {
                {0, 2, 3, 1},
                {4, 5, 7, 6},
                {0, 1, 5, 4},
                {2, 6, 7, 3},
                {0, 4, 6, 2},
                {1, 3, 7, 5}
        };
        mesh = new Mesh(vertices, faces);
    }

    public Mesh getMesh() {
        return mesh;
    }
}
