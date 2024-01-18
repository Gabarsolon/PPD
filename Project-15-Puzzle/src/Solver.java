import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

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
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(new Node(initial, 0, null));
        while (true) {
            Node removed = pq.poll();
            if (removed.board.isGoal()) {
                minMoves = removed.moves;
                lastNode = removed;
                solvable = true;
                break;
            }

            Iterable<Board> neighbors = removed.board.neighbors();
            for (Board board : neighbors) {
                if (removed.prevNode != null && removed.prevNode.board.equals(board)) {
                    continue;
                }
                pq.add(new Node(board, removed.moves + 1, removed));
            }
        }
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