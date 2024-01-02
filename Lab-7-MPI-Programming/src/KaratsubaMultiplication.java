import mpi.*;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KaratsubaMultiplication {
    static int DEGREE_THRESHOLD = 2;

    static int[] multiplySequential(int[] p1, int[] p2) {
        int p1Degree = p1.length - 1;
        int p2Degree = p2.length - 1;


        if (p1Degree < DEGREE_THRESHOLD || p2Degree < DEGREE_THRESHOLD) {
            return RegularMultiplication.sequential(p1, p2);
        }

        int[] D0 = Arrays.copyOfRange(p1, 0, p1Degree / 2);
        int[] D1 = Arrays.copyOfRange(p1, p1Degree / 2, p1.length);

        int[] E0 = Arrays.copyOfRange(p2, 0, p2Degree / 2);
        int[] E1 = Arrays.copyOfRange(p2, p2Degree / 2, p2.length);


        int[] D0E0 = multiplySequential(D0, E0);
        int[] MID = multiplySequential(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1));
        int[] D1E1 = multiplySequential(D1, E1);

        int len = Math.max(p1Degree, p2Degree) / 2;

        int[] r1 = Utils.addZerosToPolynomial(D1E1, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(MID, D1E1), D0E0), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), D0E0);

        return result;
    }

    static void workerForPararllelMultiply(int rank) throws MPIException {
        int[] sizes = new int[3];
        Status mpiStatus = MPI.COMM_WORLD.recv(sizes, 3, MPI.INT, MPI.ANY_SOURCE, 0);
        int parent = mpiStatus.getSource();

        int remainingNumberOfProcesses = sizes[0];
        int polynomial1Length = sizes[1];
        int polynomial2Length = sizes[2];

        int[] polynomial1 = new int[polynomial1Length];
        MPI.COMM_WORLD.recv(polynomial1, polynomial1Length, MPI.INT, parent, 0);

        int[] polynomial2 = new int[polynomial2Length];
        MPI.COMM_WORLD.recv(polynomial2, polynomial2Length, MPI.INT, parent, 0);

        int[] product = multiplyParallel(polynomial1, polynomial2, rank, remainingNumberOfProcesses);

        MPI.COMM_WORLD.bSend(product, product.length, MPI.INT, parent, 0);
    }

    static int[] multiplyParallel(int[] p1, int[] p2, int rank, int numberOfProcesses) throws MPIException {
        int p1Degree = p1.length - 1;
        int p2Degree = p2.length - 1;

        if (p1Degree < DEGREE_THRESHOLD || p2Degree < DEGREE_THRESHOLD) {
            return RegularMultiplication.sequential(p1, p2);
        }

        int len = Math.max(p1Degree, p2Degree) / 2;

        int[] D0 = Arrays.copyOfRange(p1, 0, p1Degree / 2);
        int[] D1 = Arrays.copyOfRange(p1, p1Degree / 2, p1.length);

        int[] E0 = Arrays.copyOfRange(p2, 0, p2Degree / 2);
        int[] E1 = Arrays.copyOfRange(p2, p2Degree / 2, p2.length);

        int[] D0E0 = null;
        int[] MID = null;
        int[] D1E1 = null;
        if (numberOfProcesses >= 3) {
            int firstChild = rank + numberOfProcesses / 3;
            int[] sizesForFirstChild = new int[3];
            //number of remaining processes
            sizesForFirstChild[0] = numberOfProcesses - 2;
            //size of first polynomial
            sizesForFirstChild[1] = D0.length;
            //size of the second polynomial
            sizesForFirstChild[2] = E0.length;
            MPI.COMM_WORLD.bSend(sizesForFirstChild, 3, MPI.INT, firstChild, 0);
            System.out.println(D0.length);
            MPI.COMM_WORLD.bSend(D0, D0.length, MPI.INT, firstChild, 0);
            MPI.COMM_WORLD.bSend(E0, E0.length, MPI.INT, firstChild, 0);

            int secondChild = rank + numberOfProcesses / 3 + 1;
            int[] sizesForSecondChild = new int[3];
            //number of remaining processes
            sizesForSecondChild[0] = numberOfProcesses - 2;
            //size of first polynomial
            sizesForSecondChild[1] = D1.length;
            //size of the second polynomial
            sizesForSecondChild[2] = E1.length;
            MPI.COMM_WORLD.bSend(sizesForSecondChild, 3, MPI.INT, secondChild, 0);
            MPI.COMM_WORLD.bSend(D1, D1.length, MPI.INT, secondChild, 0);
            MPI.COMM_WORLD.bSend(E1, E1.length, MPI.INT, secondChild, 0);

            MID = multiplySequential(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1));
            int D0E0_length = D0.length + E0.length - 1;
            D0E0 = new int[D0E0_length];
            MPI.COMM_WORLD.recv(D0E0, D0E0_length, MPI.INT, firstChild, MPI.ANY_TAG);

            int D1E1_length = D1.length + E1.length - 1;
            D1E1 = new int[D1E1_length];
            MPI.COMM_WORLD.recv(D1E1, D1E1_length, MPI.INT, secondChild, MPI.ANY_TAG);

        } else if (numberOfProcesses == 2) {
            int child = rank + 1;
            int[] sizes = new int[3];
            sizes[0] = 1;
            sizes[1] = D0.length;
            sizes[2] = E0.length;
            MPI.COMM_WORLD.bSend(sizes, 3, MPI.INT, child, 0);
            MPI.COMM_WORLD.bSend(D0, D0.length, MPI.INT, child, 0);
            MPI.COMM_WORLD.bSend(E0, E0.length, MPI.INT, child, 0);

            MID = multiplySequential(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1));
            D1E1 = multiplySequential(D1, E1);
            int D0E0_length = D0.length + E0.length - 1;
            D0E0 = new int[D0E0_length];
            MPI.COMM_WORLD.recv(D0E0, D0E0_length, MPI.INT, child, MPI.ANY_TAG);

        } else {
            D0E0 = multiplySequential(D0, E0);
            MID = multiplySequential(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1));
            D1E1 = multiplySequential(D1, E1);
        }

        int[] r1 = Utils.addZerosToPolynomial(D1E1, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(MID, D1E1), D0E0), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), D0E0);

        return result;
    }
}
