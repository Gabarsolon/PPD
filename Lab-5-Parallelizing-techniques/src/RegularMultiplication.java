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

        var product = new int[polynomial1Degree + polynomial2Degree];
        var productLength = product.length;
        var halfOfProductLength = productLength / 2;

        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();


        for (int i = 0; i < productLength; i++) {
            int finalI = i;
            int limit = 0;
            if (finalI > halfOfProductLength)
                limit = finalI - halfOfProductLength;
            int finalLimit = limit;

            completableFutures.add(CompletableFuture.runAsync(() -> {
                for (int j = 0 + finalLimit; j <= finalI - finalLimit; j++) {
                    product[finalI] += polynomial1[j] * polynomial2[finalI - j];
                }
            }));
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size() - 1])).join();

        return product;
    }
}
