import mpi.*;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Main {
    public static int N;
    public static int[] correctRow;
    public static int[] correctCol;

    static void initCorrectRowsCols(int N) {
        correctRow = new int[N * N];
        int z = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                correctRow[z++] = i;
            }
        }
        z = 0;
        correctCol = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                correctCol[z++] = j;
            }
        }
    }

    public static int[][] generateRandomBlocks(int N) {
        int[][] blocks = new int[N][N];

        var possibleValues = IntStream.range(0, N * N).boxed().collect(Collectors.toList());
        Collections.shuffle(possibleValues);
        int indexInPossibleValues = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = possibleValues.get(indexInPossibleValues++);

        return blocks;
    }

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
        int rank = MPI.COMM_WORLD.getRank();
        int totalNumberOfProcesses = MPI.COMM_WORLD.getSize();
        System.out.println("Hi! I am the process of rank " + rank);

        long start = System.currentTimeMillis();
        N = 4;
        int[][] blocks = {
                {7, 14, 12, 5,},
                {9, 2, 13, 4,},
                {1, 11, 15, 0,},
                {3, 10, 8, 6,}
        };
        initCorrectRowsCols(N);

        Board initial = new Board(blocks);
        initial.initBoard();

        if (!initial.isSolvable()) {
            if (rank == 0) System.out.println("The board is not solvable");
            return;
        }

        if (rank == 0) {
            System.out.println("The board is solvable, solving it right naw!!!...");
            // solve the puzzle
            Solver solver = new Solver(initial);

            long end = System.currentTimeMillis();
            System.out.println("time taken " + (end - start) + " milli seconds");

            // print solution to standard output
            System.out.println("Minimum number of moves = " + solver.moves());
            Stack<Board> stack = new Stack<Board>();
            for (Board board : solver.solution())
                stack.push(board);
            while (!stack.isEmpty()) {
                System.out.println(stack.pop());

            }
        } else {
            while (true) {
                int[][] arr = new int[N][N];
                MPI.COMM_WORLD.recv(arr, N*N, MPI.INT, 0, MPI.ANY_TAG);
                int manhattan = Board.computeManhattan(arr);
                MPI.COMM_WORLD.send(manhattan, 1, MPI.INT, 0, 0);
            }
        }

        MPI.Finalize();
    }
}