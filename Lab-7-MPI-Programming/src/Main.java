import mpi.*;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    static int MAX_COEFFICIENT = 1000;
    static int MAX_DEGREE = 1000;

    static boolean PRINT_OUTPUT_FLAG = false;

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

        int product[];

        if(rank == 0){
            System.out.println("Sending polynomials to processors...");
            int[] polynomial1 = generateRandomPolynomial();
            int[] polynomial2 = generateRandomPolynomial();
            for(int processorRank = 1; processorRank < totalNumberOfProcesses; processorRank++){
                MPI.COMM_WORLD.send(polynomial1, polynomial1.length, MPI.INT, processorRank, 0);
                MPI.COMM_WORLD.send(polynomial2, polynomial2.length, MPI.INT, processorRank, 0);
            }

            start = System.nanoTime();
            product = RegularMultiplication.parallel(polynomial1, polynomial2);
            end = System.nanoTime();

//            printPolynomial(RegularMultiplication.sequential(polynomial1, polynomial2));
            System.out.printf("Regular multiplication parallel finished in: %dms\n", (end - start) / 1000000);
            if (PRINT_OUTPUT_FLAG) printPolynomial(product);
            System.out.println("---------------------------------------------------------------------------");

            start = System.nanoTime();
            product = KaratsubaMultiplication.multiplyParallel(polynomial1, polynomial2, rank, totalNumberOfProcesses);
            end = System.nanoTime();

            System.out.printf("Karatsuba multiplication parallel finished in: %dms\n", (end - start) / 1000000);
            if (PRINT_OUTPUT_FLAG) printPolynomial(product);
            System.out.println("---------------------------------------------------------------------------");
        }
        else{
            int[] polynomial1 = new int[MAX_DEGREE + 1];
            int[] polynomial2 = new int[MAX_DEGREE + 1];
            MPI.COMM_WORLD.recv(polynomial1, MAX_DEGREE + 1, MPI.INT, 0, 0);
            MPI.COMM_WORLD.recv(polynomial2, MAX_DEGREE + 1, MPI.INT, 0, 0);
            RegularMultiplication.parallel(polynomial1, polynomial2);
            KaratsubaMultiplication.workerForPararllelMultiply(rank);
        }




        MPI.Finalize();
    }
}