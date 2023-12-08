import jdk.jshell.execution.Util;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KaratsubaMultiplication {
    static int[] multiplySequential(int[] polynomial1, int[] polynomial2) {
        if (polynomial1.length < 3 || polynomial2.length < 3)
            return RegularMultiplication.sequential(polynomial1, polynomial2);

        int length = Math.max(polynomial1.length, polynomial2.length) / 2;

        int[] D0 = Arrays.copyOfRange(polynomial1, 0, length);
        int[] D1 = Arrays.copyOfRange(polynomial1, length, polynomial1.length);

        int[] E0 = Arrays.copyOfRange(polynomial2, 0, length);
        int[] E1 = Arrays.copyOfRange(polynomial2, length, polynomial2.length);

        int[] f1 = multiplySequential(E1, D1);
        int[] f2 = multiplySequential(Utils.addPolynomials(E1, E0), Utils.addPolynomials(D0, D1));
        int[] f3 = multiplySequential(E0, D0);

        int[] r = new int[polynomial1.length * polynomial2.length - 1];

        return Utils.addPolynomials(r, Utils.addPolynomials(f1, Utils.addPolynomials(Utils.substractPolynomials(f2, Utils.substractPolynomials(f1, f3)), f3)));
    }

    static int[] multiplyParallel(int[] polynomial1, int[] polynomial2) throws ExecutionException, InterruptedException {
        if (polynomial1.length < 3 || polynomial2.length < 3)
            return RegularMultiplication.parallel(polynomial1, polynomial2);

        int length = Math.max(polynomial1.length, polynomial2.length) / 2;

        int[] D0 = Arrays.copyOfRange(polynomial1, 0, length);
        int[] D1 = Arrays.copyOfRange(polynomial1, length, polynomial1.length);

        int[] E0 = Arrays.copyOfRange(polynomial2, 0, length);
        int[] E1 = Arrays.copyOfRange(polynomial2, length, polynomial2.length);

        CompletableFuture<int[]> f1Future = CompletableFuture.supplyAsync(() -> multiplySequential(E1, D1));
        CompletableFuture<int[]> f2Future = CompletableFuture.supplyAsync(() -> multiplySequential(Utils.addPolynomials(E1, E0), Utils.addPolynomials(D0, D1)));
        CompletableFuture<int[]> f3Future = CompletableFuture.supplyAsync(() -> multiplySequential(E0, D0));

        int[] f1 = f1Future.get();
        int[] f2 = f2Future.get();
        int[] f3 = f3Future.get();

        int[] r = new int[polynomial1.length * polynomial2.length - 1];

        return Utils.addPolynomials(r, Utils.addPolynomials(f1, Utils.addPolynomials(Utils.substractPolynomials(f2, Utils.substractPolynomials(f1, f3)), f3)));
    }
}
