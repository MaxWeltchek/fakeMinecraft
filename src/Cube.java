public class Cube extends Shapes{
    private int[][] faces;
    public Cube(Points[] vertices) {
        super(vertices);
        faces = new int[][] {
                {0, 1, 2 ,3},
                {3, 2, 6, 7},
                {7, 6, 5, 4},
                {4, 5, 1, 0},
                {0, 3, 7, 4},
                {1, 2, 6, 5}
        };
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
                {0, 1, 2 ,3},
                {3, 2, 6, 7},
                {7, 6, 5, 4},
                {4, 5, 1, 0},
                {0, 3, 7, 4},
                {1, 2, 6, 5}
        };
    }
}
