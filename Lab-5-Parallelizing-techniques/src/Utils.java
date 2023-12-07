public class Utils {
    static int[] addPolynomials(int[] p1, int[] p2) {
        int minimumLength = Math.min(p1.length, p2.length);
        int maximumLength = Math.max(p1.length, p2.length);

        int[] sum = new int[maximumLength];

        for (int i = 0; i < minimumLength; i++)
            sum[i] = p1[i] + p2[i];

        int[] biggerPolynomial = p1;
        if (p2.length == maximumLength)
            biggerPolynomial = p2;

        for (int i = minimumLength; i < maximumLength; i++)
            sum[i] = biggerPolynomial[i];

        return sum;
    }

    static int[] substractPolynomials(int[] p1, int[] p2) {
        int minimumLength = Math.min(p1.length, p2.length);
        int maximumLength = Math.max(p1.length, p2.length);

        int[] diff = new int[maximumLength];

        for (int i = 0; i < minimumLength; i++)
            diff[i] = p1[i] - p2[i];

        if (p1.length == maximumLength)
            for (int i = minimumLength; i < maximumLength; i++)
                diff[i] = p1[i];
        else
            for (int i = minimumLength; i < maximumLength; i++)
                diff[i] -= p2[i];

        return diff;
    }
}
