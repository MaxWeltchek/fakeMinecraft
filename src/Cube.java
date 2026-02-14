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
}
