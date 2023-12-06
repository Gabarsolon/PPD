import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;

public class Main {
    static int MAX_COEFFICIENT = 1000;
    static int MAX_DEGREE = 100;
    static void printPolynomial(int[] polynomial) {
        var stringBuilder = new StringBuilder("%d".formatted(polynomial[0]));

        for (int i = 1; i < polynomial.length; i++) {
            stringBuilder.append(" + %d%s".formatted(polynomial[i], "x^%d".formatted(i)));
        }

        System.out.println(stringBuilder);
    }

    static int[] generateRandomPolynomial(){
        var random = new Random();
        int degree = random.nextInt(MAX_DEGREE);
        int[] polynomial = new int[degree];
        for(int i = 0;i<degree;i++){
            polynomial[i] = random.nextInt(MAX_COEFFICIENT);
        }
        return polynomial;
    }

    static void printPolynomial(AtomicIntegerArray polynomial) {
        var stringBuilder = new StringBuilder("%d".formatted(polynomial.get(0)));

        for (int i = 1; i < polynomial.length(); i++) {
            stringBuilder.append(" + %d%s".formatted(polynomial.get(i), "x^%d".formatted(i)));
        }

        System.out.println(stringBuilder);
    }

    static void regularMultiplicationSequential(int[] polynomial1, int[] polynomial2) {
        int polynomial1Degree = polynomial1.length;
        int polynomial2Degree = polynomial2.length;

        int[] product = new int[polynomial1Degree + polynomial2Degree - 1];
        for (int i = 0; i < product.length; i++)
            product[i] = 0;

        for (int i = 0; i < polynomial1Degree; i++)
            for (int j = 0; j < polynomial2Degree; j++)
                product[i + j] += polynomial1[i] * polynomial2[j];

        printPolynomial(product);
    }

    static void regularMultiplicationParallel(int[] polynomial1, int[] polynomial2) {
        int polynomial1Degree = polynomial1.length;
        int polynomial2Degree = polynomial2.length;

        var product = new AtomicIntegerArray(polynomial1Degree + polynomial2Degree - 1);

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        for (int i = 0; i < polynomial1Degree; i++)
            for (int j = 0; j < polynomial2Degree; j++) {
                int finalI = i;
                int finalJ = j;
                completableFutures.add(CompletableFuture.supplyAsync(() -> {
                    product.addAndGet(finalI + finalJ, polynomial1[finalI] * polynomial2[finalJ]);
                    return null;
                }));
            }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();

        printPolynomial(product);
    }

    public static void main(String[] args) {
        int[] polynomial1 = {5, 0, 10, 6};
        int[] polynomial2 = {1, 2, 4};
        regularMultiplicationSequential(polynomial1, polynomial2);
        regularMultiplicationParallel(polynomial1, polynomial2);
    }
}