import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int noOfRowsFirstMatrix = 9;
    static int noOfColumnsFirstMatrix = 9;
    static int noOfRowsSecondMatrix = 9;
    static int noOfColumnsSecondMatrix = 9;
    static int[][] firstMatrix;
    static int[][] secondMatrix;
    static int[][] resultMatrix;
    static Random rand = new Random();

    static void addRandomNumbersToMatrix(int[][] matrix) {
        int noOfRows = matrix.length;
        int noOfColumns = matrix[0].length;

        for (int rowIndex = 0; rowIndex < noOfRows; rowIndex++)
            for (int columnIndex = 0; columnIndex < noOfColumns; columnIndex++)
                matrix[rowIndex][columnIndex] = rand.nextInt(10);
    }

    static void printMatrix(int[][] matrix) {
        for (int[] row : matrix)
            System.out.println(Arrays.toString(row));
        System.out.println();
    }

    static int computeElementOfResultingMatrix(int rowFromFirstMatrix, int columnFromSecondMatrix) {
        int sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix = 0;

        for (int index = 0; index < noOfRowsSecondMatrix; index++)
            sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix
                    += firstMatrix[rowFromFirstMatrix][index] * secondMatrix[index][columnFromSecondMatrix];

        return sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix;
    }

    static void matrixProduct() {
        for (int i = 0; i < noOfRowsFirstMatrix; i++)
            for(int j=0; j<noOfColumnsSecondMatrix; j++)
                resultMatrix[i][j] = computeElementOfResultingMatrix(i,j);
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

        firstMatrix = new int[noOfRowsFirstMatrix][noOfColumnsFirstMatrix];
        secondMatrix = new int[noOfRowsSecondMatrix][noOfColumnsSecondMatrix];
        resultMatrix = new int[noOfRowsFirstMatrix][noOfColumnsSecondMatrix];

        addRandomNumbersToMatrix(firstMatrix);
        addRandomNumbersToMatrix(secondMatrix);

        matrixProduct();

        System.out.println("First matrix: ");
        printMatrix(firstMatrix);

        System.out.println("Second matrix: ");
        printMatrix(secondMatrix);

        System.out.println("Result matrix: ");
        printMatrix(resultMatrix);
    }
}