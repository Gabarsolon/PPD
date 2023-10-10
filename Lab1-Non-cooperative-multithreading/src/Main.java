import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();

        ObservableVariable A = new ObservableVariable(10);
        ObservableVariable B = new ObservableVariable(15);
        ObservableVariable C = new ObservableVariable(20);

        ObservableVariable D = new ObservableVariable(Arrays.asList(A,B));
        ObservableVariable E = new ObservableVariable(Arrays.asList(B,C));
        ObservableVariable F = new ObservableVariable(Arrays.asList(D,A));

        A.variablesToNotify.add(D);
        B.variablesToNotify.add(D);

        B.variablesToNotify.add(E);
        C.variablesToNotify.add(E);

        D.variablesToNotify.add(F);
        A.variablesToNotify.add(F);

        while(true){
            Integer primaryVariableIndex = rand.nextInt(3);
            Integer newValue = rand.nextInt(100);
            switch(primaryVariableIndex){
                case 0:
                    A.setVariable(newValue);
                    break;
                case 1:
                    B.setVariable(newValue);
                    break;
                case 2:
                    C.setVariable(newValue);
                    break;
            }
            System.out.println("A=" + A.value);
            System.out.println("B=" + B.value);
            System.out.println("C=" + C.value);
            System.out.println("D=" + D.value);
            System.out.println("E=" + E.value);
            System.out.println("F=" + F.value);
            System.out.println(D.consistencyCheck());
            System.out.println(E.consistencyCheck());
            System.out.println(F.consistencyCheck());
            System.out.println();
            Thread.sleep(100);
        }
    }
}