import mpi.MPIException;

import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

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

    public Solver(Board initial) throws MPIException {
        N = initial.dimension();
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(new Node(initial, 0, null));
        while (true) {
            Node removed = pq.poll();
            if (removed.board.isGoal()) {
                minMoves = removed.moves;
                lastNode = removed;
                break;
            }

            Iterable<Board> neighbors = removed.board.neighbors();
            for (Board board : neighbors) {
                System.out.println(board.toString());
                if (removed.prevNode != null && removed.prevNode.board.equals(board)) {
                    continue;
                }
                pq.add(new Node(board, removed.moves + 1, removed));
            }
        }
    }

    public int moves() {
        return minMoves;
    }

    public Iterable<Board> solution() {
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