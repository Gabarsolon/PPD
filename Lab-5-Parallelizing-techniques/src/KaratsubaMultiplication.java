import jdk.jshell.execution.Util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KaratsubaMultiplication {
    static int[] multiplySequential(int[] p1, int[] p2) {
        int p1Degree = p1.length - 1;
        int p2Degree = p2.length - 1;

        if (p1Degree < 2 || p2Degree < 2) {
            return RegularMultiplication.sequential(p1, p2);
        }

        int len = Math.max(p1Degree, p2Degree) / 2;

        int[] lowP1 = Arrays.copyOfRange(p1, 0, p1Degree / 2);
        int[] highP1 = Arrays.copyOfRange(p1, p1Degree / 2, p1.length);

        int[] lowP2 = Arrays.copyOfRange(p2, 0, p2Degree / 2);
        int[] highP2 = Arrays.copyOfRange(p2, p2Degree / 2, p2.length);


        int[] z1 = multiplySequential(lowP1, lowP2);
        int[] z2 = multiplySequential(Utils.addPolynomials(lowP1, highP1), Utils.addPolynomials(lowP2, highP2));
        int[] z3 = multiplySequential(highP1, highP2);

        // compute the final result

        int[] r1 = Utils.addZerosToPolynomial(z3, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(z2, z3), z1), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), z1);

        return result;
    }

    static int[] multiplyParallel(int[] p1, int[] p2) throws ExecutionException, InterruptedException {
        int p1Degree = p1.length - 1;
        int p2Degree = p2.length - 1;

        if (p1Degree < 2 || p2Degree < 2) {
            return RegularMultiplication.sequential(p1, p2);
        }

        int len = Math.max(p1Degree, p2Degree) / 2;

        int[] lowP1 = Arrays.copyOfRange(p1, 0, p1Degree / 2);
        int[] highP1 = Arrays.copyOfRange(p1, p1Degree / 2, p1.length);

        int[] lowP2 = Arrays.copyOfRange(p2, 0, p2Degree / 2);
        int[] highP2 = Arrays.copyOfRange(p2, p2Degree / 2, p2.length);

        CompletableFuture<int[]> f1Future = CompletableFuture.supplyAsync(() -> multiplySequential(lowP1, lowP2));
        CompletableFuture<int[]> f2Future = CompletableFuture.supplyAsync(() -> multiplySequential(Utils.addPolynomials(lowP1, highP1), Utils.addPolynomials(lowP2, highP2)));
        CompletableFuture<int[]> f3Future = CompletableFuture.supplyAsync(() -> multiplySequential(highP1, highP2));

        int[] z1 = f1Future.get();
        int[] z2 = f2Future.get();
        int[] z3 = f3Future.get();

        int[] r1 = Utils.addZerosToPolynomial(z3, 2 * len);
        int[] r2 = Utils.addZerosToPolynomial(Utils.substractPolynomials(Utils.substractPolynomials(z2, z3), z1), len);
        int[] result = Utils.addPolynomials(Utils.addPolynomials(r1, r2), z1);

        return result;
    }
}
