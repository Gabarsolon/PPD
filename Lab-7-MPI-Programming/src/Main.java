import mpi.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    static int MAX_COEFFICIENT = 1000;
    static int MAX_DEGREE = 1000;

    static boolean PRINT_OUTPUT_FLAG = false;
    static String ALGORITHM = "regular";

    static void printPolynomial(int[] polynomial) {
        var stringBuilder = new StringBuilder("%d".formatted(polynomial[0]));

        for (int i = 1; i < polynomial.length; i++) {
            stringBuilder.append(" + %d%s".formatted(polynomial[i], "x^%d".formatted(i)));
        }

        System.out.println(stringBuilder);
    }

    static int[] generateRandomPolynomial() {
        var random = new Random();
        int degree = MAX_DEGREE;
        int[] polynomial = new int[degree + 1];
        for (int i = 0; i <= degree; i++) {
            polynomial[i] = random.nextInt(MAX_COEFFICIENT);
        }
        return polynomial;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, MPIException {
        MPI.Init(args);
        MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
        int rank = MPI.COMM_WORLD.getRank();
        int totalNumberOfProcesses = MPI.COMM_WORLD.getSize();
        System.out.println("Hi! I am the process of rank " + rank);
//        int[] polynomial1 = {1, 1, 2};
//        int[] polynomial2 = {1, 1, 1};


//        printPolynomial(polynomial1);
//        printPolynomial(polynomial2);
//        System.out.println("--------------------------");

        long start, end;

        int[] product;

        if(args.length > 0){
            ALGORITHM = args[0];
        }

        if (rank == 0) {
            int[] polynomial1 = generateRandomPolynomial();
            int[] polynomial2 = generateRandomPolynomial();

            if (ALGORITHM.equals("regular")) {
                System.out.println("Sending polynomials to processors...");

                for (int processorRank = 1; processorRank < totalNumberOfProcesses; processorRank++) {
                    MPI.COMM_WORLD.send(polynomial1, polynomial1.length, MPI.INT, processorRank, 0);
                    MPI.COMM_WORLD.send(polynomial2, polynomial2.length, MPI.INT, processorRank, 0);
                }

                start = System.nanoTime();
                product = RegularMultiplication.parallel(polynomial1, polynomial2);
                end = System.nanoTime();

                System.out.printf("Regular multiplication parallel finished in: %dms\n", (end - start) / 1000000);
            } else {
                start = System.nanoTime();
                product = KaratsubaMultiplication.multiplyParallel(polynomial1, polynomial2, rank, totalNumberOfProcesses, 0);
                end = System.nanoTime();

                System.out.printf("Karatsuba multiplication parallel finished in: %dms\n", (end - start) / 1000000);

            }
            if (PRINT_OUTPUT_FLAG) printPolynomial(product);

        } else {
            if (ALGORITHM.equals("regular")) {
                int[] polynomial1 = new int[MAX_DEGREE + 1];
                int[] polynomial2 = new int[MAX_DEGREE + 1];
                MPI.COMM_WORLD.recv(polynomial1, MAX_DEGREE + 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.recv(polynomial2, MAX_DEGREE + 1, MPI.INT, 0, 0);
                RegularMultiplication.parallel(polynomial1, polynomial2);
            } else {
                //Karatsuba
                int[] parentAndLevel = KaratsubaMultiplication.getParentAndLevel(totalNumberOfProcesses, rank);
                System.out.printf(Arrays.toString(parentAndLevel));
                int p1length = 0;
                int[] p1 = null;
                MPI.COMM_WORLD.recv(p1length, 1, MPI.INT, parentAndLevel[0], MPI.ANY_TAG);
                p1 = new int[p1length];
                MPI.COMM_WORLD.recv(p1, p1length, MPI.INT, parentAndLevel[0], MPI.ANY_TAG);


                int p2length = 0;
                int[] p2 = null;
                MPI.COMM_WORLD.recv(p2length, 1, MPI.INT, parentAndLevel[0], MPI.ANY_TAG);
                p2 = new int[p2length];
                MPI.COMM_WORLD.recv(p2, p2length, MPI.INT, parentAndLevel[0], MPI.ANY_TAG);

                KaratsubaMultiplication.multiplyParallel(p1, p2, rank, totalNumberOfProcesses, parentAndLevel[1]);
            }

        }


        MPI.Finalize();
    }
}