public class Utils {
    public static int[] flattenMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] flattened = new int[rows * cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, flattened, i * cols, cols);
        }

        return flattened;
    }

    public static int[][] reshapeMatrix(int[] flatMatrix, int rows, int cols) {
        int[][] reshaped = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(flatMatrix, i * cols, reshaped[i], 0, cols);
        }

        return reshaped;
    }
}
