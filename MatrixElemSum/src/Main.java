import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {
    static int recursiveSum(List<Integer> a, int beginIndex, int endIndex, int nrThreads) throws Exception {
        if (endIndex == beginIndex + 1)
            return a.get(beginIndex);

        int midPoint = (beginIndex + endIndex) / 2;
        if (nrThreads <= 1) {
            return recursiveSum(a, beginIndex, midPoint, 1)
                    + recursiveSum(a, midPoint, endIndex, 1);
        }

        var f1 = CompletableFuture.supplyAsync(() -> {
            try {
                return recursiveSum(a, beginIndex, midPoint, nrThreads / 2);
            } catch (Exception ignored) {
            }
            return null;
        });
        int s2 = recursiveSum(a, midPoint, endIndex, nrThreads - (nrThreads / 2));
        return f1.get() + s2;
    }

    public static void main(String[] args) throws Exception {
        var matrix = Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(3, 5, 7, 9),
                Arrays.asList(9, 4, 6, 3),
                Arrays.asList(3, 5, 5, 4)
        );

        List<Integer> flattenedMatrix = new ArrayList<>();
        matrix.forEach(flattenedMatrix::addAll);

        int nrThreads = 8;
        System.out.println(recursiveSum(flattenedMatrix, 0, flattenedMatrix.size(), nrThreads));
    }
}