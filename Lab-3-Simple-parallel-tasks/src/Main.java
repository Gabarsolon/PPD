import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static int noOfRowsFirstMatrix = 9;
    static int noOfColumnsFirstMatrix = 9;
    static int noOfRowsSecondMatrix = 9;
    static int noOfColumnsSecondMatrix = 9;
    static int[][] firstMatrix;
    static int[][] secondMatrix;
    static int[][] resultMatrix;
    static Random rand = new Random();
    static int numberOfTasks = 4;
    static int numberOfThreadsForThreadPool = 10;
    static List<List<Pair>> tasks = new ArrayList<>();

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

    static void computeElementOfResultingMatrix(int rowFromFirstMatrix, int columnFromSecondMatrix) {
        int sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix = 0;

        for (int index = 0; index < noOfRowsSecondMatrix; index++)
            sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix
                    += firstMatrix[rowFromFirstMatrix][index] * secondMatrix[index][columnFromSecondMatrix];

        resultMatrix[rowFromFirstMatrix][columnFromSecondMatrix] =
                sumOfProductsBetweenRowFromFirstMatrixAndColumnFromSecondMatrix;
    }

    static void taskExecution(int taskIndex) {
        int i = taskIndex / noOfRowsFirstMatrix;
        int j = taskIndex % noOfColumnsSecondMatrix;

        while (i < noOfRowsFirstMatrix) {
            computeElementOfResultingMatrix(i, j);
            j += numberOfTasks;
            if (j >= noOfColumnsSecondMatrix) {
                i = i + j / noOfColumnsSecondMatrix;
                j = j % noOfColumnsSecondMatrix;
            }
        }
    }

    static void matrixProductSerialized() {
        for (int i = 0; i < noOfRowsFirstMatrix; i++)
            for (int j = 0; j < noOfColumnsSecondMatrix; j++)
                computeElementOfResultingMatrix(i, j);
    }

    static void matrixProductWithThreads() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int taskIndex = 0; taskIndex < numberOfTasks; taskIndex++) {
            int finalTaskIndex = taskIndex;
            threads.add(new Thread(() -> taskExecution(finalTaskIndex)));
        }

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static void matrixProductWithThreadPool() {
        var executorService = Executors.newFixedThreadPool(numberOfThreadsForThreadPool);

        for (int taskIndex = 0; taskIndex < numberOfTasks; taskIndex++) {
            int finalTaskIndex = taskIndex;
            executorService.execute((() -> taskExecution(finalTaskIndex)));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Timeout reached. Some tasks may still be running.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var scanner = new Scanner(System.in);

        System.out.print("Input the number of rows for the first matrix: ");
        noOfRowsFirstMatrix = scanner.nextInt();

        System.out.print("Input the number of columns for the first matrix: ");
        noOfColumnsFirstMatrix = scanner.nextInt();

        //the number of rows from the second matrix must be equal to the number of column
        //in the first in order to have a valid matrix multiplication
        noOfRowsSecondMatrix = noOfRowsFirstMatrix;

        System.out.print("Input the number of columns for the second matrix: ");
        noOfColumnsSecondMatrix = scanner.nextInt();

        System.out.print("Input the number of tasks: ");
        numberOfTasks = scanner.nextInt();

        System.out.print("Input the number of threads for the thread pool: ");
        numberOfThreadsForThreadPool = scanner.nextInt();

        firstMatrix = new int[noOfRowsFirstMatrix][noOfColumnsFirstMatrix];
        secondMatrix = new int[noOfRowsSecondMatrix][noOfColumnsSecondMatrix];
        resultMatrix = new int[noOfRowsFirstMatrix][noOfColumnsSecondMatrix];

        addRandomNumbersToMatrix(firstMatrix);
        addRandomNumbersToMatrix(secondMatrix);

//        generateTasks();

        long start = System.nanoTime();
        matrixProductWithThreads();
        long end = System.nanoTime();

        System.out.printf("Matrix product with threads finished in: %dms\n", (end - start) / 1000000);

        start = System.nanoTime();
        matrixProductWithThreadPool();
        end = System.nanoTime();

        System.out.printf("Matrix product with thread pool finished in: %dms\n", (end - start) / 1000000);

        start = System.nanoTime();
        matrixProductSerialized();
        end = System.nanoTime();

        System.out.printf("Matrix product serialized in: %dms\n", (end - start) / 1000000);

//        System.out.println("Result matrix: ");
//        printMatrix(resultMatrix);
    }
}