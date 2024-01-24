import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main {
    static int MAX_COEFFICIENT = 1000;
    static int MAX_DEGREE = 100000;
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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        int[] polynomial1 = {1, 1, 2};
//        int[] polynomial2 = {1, 1, 1};

        int[] polynomial1 = generateRandomPolynomial();
        int[] polynomial2 = generateRandomPolynomial();

//        printPolynomial(polynomial1);
//        printPolynomial(polynomial2);
        System.out.println("--------------------------");

        long start, end;

        start = System.nanoTime();
        int[] product = RegularMultiplication.sequential(polynomial1, polynomial2);
        end = System.nanoTime();

        System.out.printf("Regular multiplication sequential finished in: %dms\n", (end - start) / 1000000);
        if (PRINT_OUTPUT_FLAG) printPolynomial(product);
        System.out.println("---------------------------------------------------------------------------");


        start = System.nanoTime();
        product = RegularMultiplication.parallel(polynomial1, polynomial2);
        end = System.nanoTime();

        System.out.printf("Regular multiplication parallel finished in: %dms\n", (end - start) / 1000000);
        if (PRINT_OUTPUT_FLAG) printPolynomial(product);
        System.out.println("---------------------------------------------------------------------------");


        start = System.nanoTime();
        product = KaratsubaMultiplication.multiplySequential(polynomial1, polynomial2);
        end = System.nanoTime();

        System.out.printf("Karatsuba multiplication sequential finished in: %dms\n", (end - start) / 1000000);
        if (PRINT_OUTPUT_FLAG) printPolynomial(product);
        System.out.println("---------------------------------------------------------------------------");


        start = System.nanoTime();
        product = KaratsubaMultiplication.multiplyParallel(polynomial1, polynomial2);
        end = System.nanoTime();

        System.out.printf("Karatsuba multiplication parallel finished in: %dms\n", (end - start) / 1000000);
        if (PRINT_OUTPUT_FLAG) printPolynomial(product);
        System.out.println("---------------------------------------------------------------------------");


    }
}