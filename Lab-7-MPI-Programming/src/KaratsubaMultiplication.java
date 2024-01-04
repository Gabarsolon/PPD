import mpi.*;

import java.util.Arrays;

public class KaratsubaMultiplication {
    static int DEGREE_THRESHOLD = 32;

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

    static boolean hasChildren(int nrProcs, int level) {
        return ((nrProcs >> level) > 1);
    }

    static int rightChild(int nrProcs, int myId, int level) {
        return myId + (nrProcs >> (level + 1));
    }

    static int[] getParentAndLevel(int nrProcs, int myId) {
        int level = 0;
        while (nrProcs > 1) {
            nrProcs >>= 1;
            ++level;
        }
        int parentId = myId;
        while ((parentId & 1) == 0) {
            parentId >>= 1;
            --level;
        }
        --parentId;
        return new int[]{parentId, level};
    }


    static int[] multiplyParallel(int[] p1, int[] p2, int rank, int numberOfProcesses, int level) throws MPIException {
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
//        System.out.println("Processor of rank " + rank + " is workin' baby!");

        if (hasChildren(numberOfProcesses, level)) {
            int childId = rightChild(numberOfProcesses, rank, level);
            int[] sizesForFirstChild = new int[2];
            //size of first polynomial
            sizesForFirstChild[0] = D0.length;
            //size of the second polynomial
            sizesForFirstChild[1] = E0.length;
            MPI.COMM_WORLD.bSend(sizesForFirstChild, 2, MPI.INT, childId, 0);
            MPI.COMM_WORLD.bSend(D0, D0.length, MPI.INT, childId, 0);
            MPI.COMM_WORLD.bSend(E0, E0.length, MPI.INT, childId, 0);

            MID = multiplyParallel(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1), rank, numberOfProcesses, level + 1);
            D1E1 = multiplyParallel(D1, E1, rank, numberOfProcesses, level + 1);

            int D0E0_length = D0.length + E0.length - 1;
            D0E0 = new int[D0E0_length];
            MPI.COMM_WORLD.recv(D0E0, D0E0_length, MPI.INT, childId, MPI.ANY_TAG);
        } else {
            D0E0 = multiplyParallel(D0, E0, rank, numberOfProcesses, level + 1);
            MID = multiplyParallel(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1), rank, numberOfProcesses, level + 1);
            D1E1 = multiplyParallel(D1, E1, rank, numberOfProcesses, level + 1);
        }

        int[] r1 = Utils.addZerosToPolynomial(D1E1, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(MID, D1E1), D0E0), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), D0E0);

        return result;
    }
}
