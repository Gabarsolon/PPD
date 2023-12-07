import java.util.Arrays;

public class KaratsubaMultiplication {
    static int[] multiplySequential(int[] polynomial1, int[] polynomial2) {
        if (polynomial1.length < 2 || polynomial2.length < 2)
            return RegularMultiplication.sequential(polynomial1, polynomial2);

        int length = Math.max(polynomial1.length, polynomial2.length) / 2;

        int[] D0 = Arrays.copyOfRange(polynomial1, 0, length);
        int[] D1 = Arrays.copyOfRange(polynomial1, length, polynomial1.length);

        int[] E0 = Arrays.copyOfRange(polynomial2, 0, length);
        int[] E1 = Arrays.copyOfRange(polynomial2, length, polynomial2.length);

        int[] f1 = multiplySequential(E1, D1);
        int[] f2 = multiplySequential(Utils.addPolynomials(E1, E0), Utils.addPolynomials(D0, D1));
        int[] f3 = multiplySequential(E0, D0);

        return Utils.addPolynomials(f1, Utils.addPolynomials(Utils.substractPolynomials(f2, Utils.substractPolynomials(f1, f3)), f3));
    }
}
