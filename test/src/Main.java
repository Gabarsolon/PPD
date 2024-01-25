import mpi.*;

import java.util.Arrays;

public class Main {
    public static int[] product(int nrProcs, int[] p, int[] q) throws MPIException {
        int[] sizes = new int[2];
        sizes[0] = p.length;
        sizes[1] = q.length;
        MPI.COMM_WORLD.bcast(sizes, 2, MPI.INT, 0);
        MPI.COMM_WORLD.bcast(p, p.length, MPI.INT, 0);
        MPI.COMM_WORLD.bcast(q, q.length, MPI.INT, 0);

        int[] partRes = partProd(0, nrProcs, p, q);
        int[] res = new int[p.length + q.length - 1];
        MPI.COMM_WORLD.gather(partRes, partRes.length, MPI.INT,
                res, partRes.length, MPI.INT, 0);
        return res;
    }

    public static int[] partProd(int myId, int nrProc, int[] p, int[] q) {
        int chunkSize = (p.length + q.length - 1) / nrProc;
        int[] r = new int[chunkSize];
        int baseIdx = chunkSize * myId;
        for (int i = 0; i < chunkSize; ++i) {
            for (int j = 0; j <= i + baseIdx; ++j) {
                if (j < p.length && i + baseIdx - j < q.length)
                    r[i] += p[j] * q[i + baseIdx - j];
            }
        }

        return r;
    }

    public static void worker(int myID, int nrProc) throws MPIException {
        int[] sizes = new int[2];
        MPI.COMM_WORLD.bcast(sizes, 2, MPI.INT, 0);
        int[] p = new int[sizes[0]];
        int[] q = new int[sizes[1]];
        MPI.COMM_WORLD.bcast(p, p.length, MPI.INT, 0);
        MPI.COMM_WORLD.bcast(q, q.length, MPI.INT, 0);
        int[] r = partProd(myID, nrProc, p, q);
        MPI.COMM_WORLD.gather(r, r.length, MPI.INT,
                null, 0, MPI.INT, 0);
    }

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int me = MPI.COMM_WORLD.getRank();
        int nrProcs = MPI.COMM_WORLD.getSize();

        int[] p = {1, 1, 1, 1};
        int[] q = {1, 1, 1,};

        if (me == 0) {
            int[] r = product(nrProcs, p, q);
            System.out.println(Arrays.toString(r));
        } else {
            worker(me, nrProcs);
        }
        MPI.Finalize();
    }
}