import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class RegularMultiplication {
    static int[] sequential(int[] polynomial1, int[] polynomial2) {
        int polynomial1Degree = polynomial1.length;
        int polynomial2Degree = polynomial2.length;

        int[] product = new int[polynomial1Degree + polynomial2Degree - 1];
        for (int i = 0; i < product.length; i++)
            product[i] = 0;

        for (int i = 0; i < polynomial1Degree; i++)
            for (int j = 0; j < polynomial2Degree; j++)
                product[i + j] += polynomial1[i] * polynomial2[j];

        return product;
    }

    static int[] parallel(int[] polynomial1, int[] polynomial2) {
        int polynomial1Degree = polynomial1.length - 1;
        int polynomial2Degree = polynomial2.length - 1;

        var product = new int[polynomial1Degree + polynomial2Degree + 1];
        var productLength = product.length;
        var halfOfProductLength = productLength / 2;

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();


        for (int i = 0; i < halfOfProductLength; i++) {
            int finalI = i;
            completableFutures.add(CompletableFuture.runAsync(() -> {
                for (int j = 0; j <= finalI; j++) {
                    product[finalI] += polynomial1[j] * polynomial2[finalI - j];
                }

                int finalI2 = productLength - finalI - 1;
                int lowerLimit = finalI2 - halfOfProductLength;
                int upperLimit = finalI2 - lowerLimit;
                for (int j = lowerLimit; j <= upperLimit; j++) {
                    product[finalI2] += polynomial1[j] * polynomial2[finalI2 - j];
                }
            }));
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size() - 1])).join();

        return product;
    }
}
