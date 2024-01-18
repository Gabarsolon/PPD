import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Main {
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

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // create initial board from file
        Scanner in = new Scanner(new InputStreamReader(System.in));
        System.out.print("Input the board size (N): ");
        int N = in.nextInt();
        int[][] blocks = generateRandomBlocks(N);
        initCorrectRowsCols(N);

        Board initial = new Board(blocks);

        if(!initial.isSolvable()){
            System.out.println("Not solvable");
            return;
        }
        System.out.println("solvable");
        // solve the puzzle
        Solver solver = new Solver(initial);

        long end = System.currentTimeMillis();
        System.out.println("time taken " + (end - start) + " milli seconds");

        // print solution to standard output
        if (!solver.isSolvable())
            System.out.println("No solution possible");
        else {
            System.out.println("Minimum number of moves = " + solver.moves());
            Stack<Board> stack = new Stack<Board>();
            for (Board board : solver.solution())
                stack.push(board);
            while (!stack.isEmpty()) {
                System.out.println(stack.pop());
            }
        }
    }
}