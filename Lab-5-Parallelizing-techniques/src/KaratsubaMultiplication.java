import jdk.jshell.execution.Util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KaratsubaMultiplication {
    static int DEGREE_THRESHOLD = 64;
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

    static int[] multiplyParallel(int[] p1, int[] p2) throws ExecutionException, InterruptedException {
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

        CompletableFuture<int[]> f1Future = CompletableFuture.supplyAsync(() -> multiplySequential(D0, E0));
        CompletableFuture<int[]> f2Future = CompletableFuture.supplyAsync(() -> multiplySequential(Utils.addPolynomials(D0, D1), Utils.addPolynomials(E0, E1)));
        CompletableFuture<int[]> f3Future = CompletableFuture.supplyAsync(() -> multiplySequential(D1, E1));

        int[] D0E0 = f1Future.get();
        int[] MID = f2Future.get();
        int[] D1E1 = f3Future.get();

        int[] r1 = Utils.addZerosToPolynomial(D1E1, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(MID, D1E1), D0E0), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), D0E0);

        return result;
    }
}
