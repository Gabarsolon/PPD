import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int noOfRowsFirstMatrix = 9;
    static int noOfColumnsFirstMatrix = 9;
    static int noOfRowsSecondMatrix = 9;
    static int noOfColumnsSecondMatrix = 9;
    static int[][] firstMatrix = new int[noOfRowsFirstMatrix][noOfColumnsFirstMatrix];
    static int[][] secondMatrix = new int[noOfRowsSecondMatrix][noOfColumnsSecondMatrix];
    static int[][] resultMatrix = new int[noOfRowsFirstMatrix][noOfColumnsSecondMatrix];
    static Random rand = new Random();
    static void addRandomNumbersToMatrix(int[][] matrix) {
        int noOfRows = matrix.length;
        int noOfColumns = matrix[0].length;

        for (int rowIndex = 0; rowIndex < noOfRows; rowIndex++)
            for(int columnIndex = 0; columnIndex < noOfColumns; columnIndex++)
                matrix[rowIndex][columnIndex] = rand.nextInt(10);
    }
    static void printMatrix(int[][] matrix){
        for(int[] row : matrix)
            System.out.println(Arrays.toString(row));
        System.out.println();
    }
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.print("Input the number of rows for the first matrix: ");
        noOfRowsFirstMatrix = scanner.nextInt();

        System.out.print("Input the number of columns for the first matrix: ");
        noOfColumnsFirstMatrix = scanner.nextInt();

        System.out.print("Input the number of rows for the second matrix: ");
        noOfRowsSecondMatrix = scanner.nextInt();

        System.out.print("Input the number of columns for the second matrix: ");
        noOfColumnsSecondMatrix = scanner.nextInt();

        addRandomNumbersToMatrix(firstMatrix);
        addRandomNumbersToMatrix(secondMatrix);

        printMatrix(firstMatrix);
        printMatrix(secondMatrix);
    }
}