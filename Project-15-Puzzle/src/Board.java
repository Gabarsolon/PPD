import java.util.ArrayDeque;
import java.util.Queue;

class Board {
    private int[][] array;
    private int N;
    int emptyRow;
    int emptyCol;
    boolean reached;
    int manhattan = 0;

    public Board(int[][] blocks) {
        N = blocks.length;
        array = new int[N][N];
        reached = true;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                array[i][j] = blocks[i][j];
                if (array[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
                if (array[i][j] != N * i + j + 1) {
                    if (!(i == N - 1 && j == N - 1)) {
                        reached = false;
                    }
                }
                int num = array[i][j];
                if (num == 0) {
                    continue;
                }
                int indManhattan = Math.abs(Main.correctRow[num - 1] - i)
                        + Math.abs(Main.correctCol[num - 1] - j);
                manhattan += indManhattan;
            }
        }
    }

    public int dimension() {
        return N;
    }

    public int manhattan() {
        return manhattan;
    }

    public boolean isGoal() {
        return reached;
    }

    // A utility function to count inversions in given
    // array 'arr[]'. Note that this function can be
    // optimized to work in O(n Log n) time. The idea
    // here is to keep code small and simple.
    private int getInvCount(int[] arr)
    {
        int inv_count = 0;
        for (int i = 0; i < N * N - 1; i++) {
            for (int j = i + 1; j < N * N; j++) {
                // count pairs(arr[i], arr[j]) such that
                // i < j but arr[i] > arr[j]
                if (arr[j] != 0 && arr[i] != 0
                        && arr[i] > arr[j])
                    inv_count++;
            }
        }
        return inv_count;
    }


    // find Position of blank from bottom
    private int findXPosition()
    {
        // start from bottom-right corner of matrix
        for (int i = N - 1; i >= 0; i--)
            for (int j = N - 1; j >= 0; j--)
                if (array[i][j] == 0)
                    return N - i;
        return -1;
    }


    public boolean isSolvable(){
        // Count inversions in given puzzle
        int[] arr = new int[N * N];
        int k = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                arr[k++] = array[i][j];

        int invCount = getInvCount(arr);

        // If grid is odd, return true if inversion
        // count is even.
        if (N % 2 == 1)
            return invCount % 2 == 0;
        else // grid is even
        {
            int pos = findXPosition();
            if (pos % 2 == 1)
                return invCount % 2 == 0;
            else
                return invCount % 2 == 1;
        }
    }


    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (that.array.length != this.array.length) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (that.array[i][j] != this.array[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        Queue<Board> q = new ArrayDeque<Board>();
        int firstIndex0 = 0;
        int secondIndex0 = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (array[i][j] == 0) {
                    firstIndex0 = i;
                    secondIndex0 = j;
                    break;
                }
            }
        }
        if (secondIndex0 > 0) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0, secondIndex0 - 1);
            q.add(new Board(newArr));
        }
        if (secondIndex0 < N - 1) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0, secondIndex0 + 1);
            q.add(new Board(newArr));
        }
        if (firstIndex0 > 0) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0 - 1, secondIndex0);
            q.add(new Board(newArr));
        }
        if (firstIndex0 < N - 1) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0 + 1, secondIndex0);
            q.add(new Board(newArr));
        }
        return q;
    }

    private int[][] getCopy() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                copy[i][j] = array[i][j];
            }
        }
        return copy;
    }

    private void exch(int[][] arr, int firstIndex, int secIndex, int firstIndex2, int secIndex2) {
        int temp = arr[firstIndex][secIndex];
        arr[firstIndex][secIndex] = arr[firstIndex2][secIndex2];
        arr[firstIndex2][secIndex2] = temp;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%4d", array[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
}