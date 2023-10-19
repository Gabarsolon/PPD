import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
3. Summation with fixed structure of inputs
We have to keep the values of some integer variables.
Some of them are primary variables; they represent input data.
The others are secondary variables, and represent aggregations of some other variables.
In our case, each secondary variable is a sum of some input variables.
The inputs may be primary or secondary variables.
However, we assume that the relations do not form cycles.

At runtime, we get notifications of value changes for the primary variable.
Processing a notification must atomically update the primary variable, as well as any secondary variable depending, directly or indirectly, on it.
The updating shall not re-compute the sums; instead, you must use the difference between the old value and the new value of the primary variable.

From time to time, as well as at the end, a consistency check shall be performed.
 It shall verify that all the secondary variables are indeed the sums of their inputs, as specified.

Two updates involving distinct variables must be able to proceed independently (without having to wait for the same mutex).
*/

public class Main {
    static ObservableVariable A;
    static ObservableVariable B;
    static ObservableVariable C;
    static ObservableVariable D;
    static ObservableVariable E;
    static ObservableVariable F;
    static Random rand = new Random();

    static Lock lockForA = new ReentrantLock();
    static Lock lockForB = new ReentrantLock();
    static Lock lockForC = new ReentrantLock();

    private static void threadFunction(int threadIndex){
        while(true){
//            System.out.println("Thread " + threadIndex + " is doing stuff");
            Integer primaryVariableIndex = rand.nextInt(3);
            Integer newValue = rand.nextInt(100);
            String variableName = "";
            switch(primaryVariableIndex){
                case 0:
                    lockForA.lock();
                    A.setVariable(newValue);
                    lockForA.unlock();
                    variableName = "A";
                    break;
                case 1:
                    lockForB.lock();
                    B.setVariable(newValue);
                    lockForB.unlock();
                    variableName = "B";
                    break;
                case 2:
                    lockForC.lock();
                    C.setVariable(newValue);
                    lockForC.unlock();
                    variableName = "C";
                    break;
            }
//            System.out.printf("Thread %d modified variable %s\n", threadIndex, variableName);
//            System.out.println("A=" + A.value);
//            System.out.println("B=" + B.value);
//            System.out.println("C=" + C.value);
//            System.out.println("D=" + D.value);
//            System.out.println("E=" + E.value);
//            System.out.println("F=" + F.value);
//            System.out.println(D.consistencyCheck());
//            System.out.println(E.consistencyCheck());
//            System.out.println(F.consistencyCheck());
//            System.out.println();
        }
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        A = new ObservableVariable(10);
        B = new ObservableVariable(15);
        C = new ObservableVariable(20);

        D = new ObservableVariable(Arrays.asList(A,B));
        E = new ObservableVariable(Arrays.asList(B,C));
        F = new ObservableVariable(Arrays.asList(D,A));

        A.variablesToNotify.add(D);
        B.variablesToNotify.add(D);

        B.variablesToNotify.add(E);
        C.variablesToNotify.add(E);

        D.variablesToNotify.add(F);
        A.variablesToNotify.add(F);

        int nThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        for(int threadIndex = 0; threadIndex < nThreads; threadIndex++) {
            int finalThreadIndex = threadIndex;
            executorService.execute(() -> threadFunction(finalThreadIndex));
        }

        while(true){
            Thread.sleep(500);
            lockForA.lock();
            lockForB.lock();
            lockForC.lock();
            System.out.println(D.consistencyCheck());
            System.out.println(E.consistencyCheck());
            System.out.println(F.consistencyCheck());
            System.out.println();
            lockForA.unlock();
            lockForB.unlock();
            lockForC.unlock();
        }
    }
}