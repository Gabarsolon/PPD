import mpi.*;


public class RegularMultiplication {
    static int[] sequential(int[] polynomial1, int[] polynomial2) {
        int polynomial1Degree = polynomial1.length;
        int polynomial2Degree = polynomial2.length;

        int[] product = new int[polynomial1Degree + polynomial2Degree - 1];

        for (int i = 0; i < polynomial1Degree; i++)
            for (int j = 0; j < polynomial2Degree; j++)
                product[i + j] += polynomial1[i] * polynomial2[j];

        return product;
    }

    static int[] parallel(int[] polynomial1, int[] polynomial2) throws MPIException {
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        int polynomial1Degree = polynomial1.length - 1;
        int polynomial2Degree = polynomial2.length - 1;

        var productLength = polynomial1Degree + polynomial2Degree + 1;
        var halfOfProductLength = productLength / 2;

        if (rank == 0) {
            var product = new int[polynomial1Degree + polynomial2Degree + 1];

            int numberOfPositionsFromProductArrayProcessed = 0;
            for (int i = 0; i < halfOfProductLength; i += size){
                for (int j = 0; j <= i; j++) {
                    product[i] += polynomial1[j] * polynomial2[i - j];
                }

                int finalI2 = productLength - i - 1;
                int lowerLimit = finalI2 - halfOfProductLength;
                int upperLimit = finalI2 - lowerLimit;
                for (int j = lowerLimit; j <= upperLimit; j++) {
                    product[finalI2] += polynomial1[j] * polynomial2[finalI2 - j];
                }

                numberOfPositionsFromProductArrayProcessed += 2;
            }
            if (productLength % 2 != 0) {
                for (int j = 0; j <= halfOfProductLength; j++) {
                    product[halfOfProductLength] += polynomial1[j] * polynomial2[halfOfProductLength - j];
                }
                numberOfPositionsFromProductArrayProcessed++;
            }

            for(int currentNumberOfProcessedPositionsFromArray = numberOfPositionsFromProductArrayProcessed; currentNumberOfProcessedPositionsFromArray< productLength;currentNumberOfProcessedPositionsFromArray++){
                int[] computedProduct = new int[2];
                Status status = MPI.COMM_WORLD.recv(computedProduct,2, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                int positionInProductArray = computedProduct[0];
                int productCoefficient = computedProduct[1];
                product[positionInProductArray] = productCoefficient;
            }

            return product;
        }

        for (int i = rank; i < halfOfProductLength; i += size) {
            int i_product = 0;

            for (int j = 0; j <= i; j++) {
                i_product += polynomial1[j] * polynomial2[i - j];
            }

            MPI.COMM_WORLD.bSend(new int[]{i, i_product}, 2, MPI.INT, 0, 0);

            int symmetricIProduct = 0;
            int symmetricI = productLength - i - 1;
            int lowerLimit = symmetricI - halfOfProductLength;
            int upperLimit = symmetricI - lowerLimit;
            for (int j = lowerLimit; j <= upperLimit; j++) {
                symmetricIProduct += polynomial1[j] * polynomial2[symmetricI - j];
            }

            MPI.COMM_WORLD.bSend(new int[]{symmetricI, symmetricIProduct}, 2, MPI.INT, 0, 0);
        }

        return null;
    }
}
