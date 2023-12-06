import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static void printPolynomial(int[] polynomial) {
        var stringBuilder = new StringBuilder("%d".formatted(polynomial[0]));

        for (int i = 1; i < polynomial.length; i++) {
            stringBuilder.append(" + %d%s".formatted(polynomial[i], "x^%d".formatted(i)));
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

    public static void main(String[] args) {
        int[] polynomial1 = {5, 0, 10, 6};
        int[] polynomial2 = {1, 2, 4};
        regularMultiplicationSequential(polynomial1, polynomial2);
    }
}