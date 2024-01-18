import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

class Solver {
    private int N;
    private int minMoves;


    private class Node implements Comparable<Node> {
        private Board board;
        private int moves;
        private Node prevNode;

        public Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.prevNode = prev;
        }

        public int compareTo(Node that) {
            int thisPriority = this.moves + this.board.manhattan();
            int thatPriority = that.moves + that.board.manhattan();
            if (thisPriority < thatPriority) {
                return -1;
            } else if (thisPriority > thatPriority) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private Node lastNode;
    private boolean solvable;

    public Solver(Board initial) throws ExecutionException, InterruptedException {
        N = initial.dimension();
        PriorityBlockingQueue<Node> pq = new PriorityBlockingQueue<>();
        pq.add(new Node(initial, 0, null));

        ExecutorService executor = Executors.newFixedThreadPool(4);

        long start = System.currentTimeMillis();

        executor.execute(() -> {
            while (true) {
                Node removed = pq.poll();
                if (removed.board.isGoal()) {
                    minMoves = removed.moves;
                    lastNode = removed;
                    solvable = true;

                    System.out.println("Minimum number of moves = " + moves());
                    Stack<Board> stack = new Stack<Board>();
                    for (Board board : solution())
                        stack.push(board);
                    while (!stack.isEmpty()) {
                        System.out.println(stack.pop());
                    }

                    long end = System.currentTimeMillis();
                    System.out.println("time taken " + (end - start) + " milli seconds");

                    executor.shutdownNow();

                    break;
                }

                Iterable<Board> neighbors = null;
                try {
                    neighbors = removed.board.neighbors();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (Board board : neighbors) {
                    if (removed.prevNode != null && removed.prevNode.board.equals(board)) {
                        continue;
                    }
                    pq.add(new Node(board, removed.moves + 1, removed));
                }
            }
        });
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return minMoves;
    }

    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }
        Stack<Board> stack = new Stack<Board>();
        Node node = lastNode;
        while (true) {
            if (node == null) break;
            Board board = node.board;
            node = node.prevNode;
            stack.push(board);
        }
        return stack;
    }


}