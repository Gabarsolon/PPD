import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    static List<ObservableVariable> primaryVariablesList = new ArrayList<>();
    static List<ObservableVariable> secondaryVariablesList = new ArrayList<>();
    static Random rand = new Random();

    static int RANDOM_UPPERBOUND = 100;

    private static void threadFunction(int threadIndex) throws InterruptedException {
        while (true) {
            ObservableVariable randomPrimaryVariableFromThePrimaryVariableList =
                    primaryVariablesList.get(rand.nextInt(primaryVariablesList.size()));

            randomPrimaryVariableFromThePrimaryVariableList.lock.lock();
            randomPrimaryVariableFromThePrimaryVariableList.setVariable(rand.nextInt(RANDOM_UPPERBOUND));
            randomPrimaryVariableFromThePrimaryVariableList.lock.unlock();

            System.out.printf("Variable %s has changed\n---------------------------------------\n",
                    randomPrimaryVariableFromThePrimaryVariableList.name);
            Thread.sleep(500);
        }
    }

    private static void consistencyCheckForSecondaryVariables() throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            primaryVariablesList.forEach((secondaryVariable) -> secondaryVariable.lock.lock());
            long numberOfConsistentVariables =
                    secondaryVariablesList.stream().map(ObservableVariable::consistencyCheck)
                            .filter(Boolean::booleanValue).count();
            System.out.printf("CONSISTENCY CHECK STATUS:\nThere are %d consistent variables out of %d\n",
                    numberOfConsistentVariables, secondaryVariablesList.size());
            System.out.println("--------------------------------------------------");
            primaryVariablesList.forEach((secondaryVariable) -> secondaryVariable.lock.unlock());
        }
    }

    public static void main(String[] args) throws Exception {
        int numberOfPrimaryVariables;
        int numberOfSecondaryVariables;
        int numberOfThreads;

        Scanner scanner = new Scanner(System.in);

        System.out.print("Input the number of primary variables: ");
        numberOfPrimaryVariables = scanner.nextInt();
        if (numberOfPrimaryVariables < 1)
            throw new Exception("You must input a number of primary variables larger than 1");


        System.out.print("Input the number of secondary variables: ");
        numberOfSecondaryVariables = scanner.nextInt();
        if (numberOfSecondaryVariables < 1)
            throw new Exception("You must input a number of secondary variables larger than 1");

        System.out.print("Input the number of threads: ");
        numberOfThreads = scanner.nextInt();
        if (numberOfThreads < 1) {
            throw new Exception("You must have at least one thread");
        }

        for (int variableIndex = 0; variableIndex < numberOfPrimaryVariables; variableIndex++) {
            primaryVariablesList.add(new ObservableVariable(rand.nextInt(RANDOM_UPPERBOUND), Integer.toString(variableIndex)));
        }

        for (int variableIndex = 0; variableIndex < numberOfSecondaryVariables; variableIndex++) {
            List<ObservableVariable> allVariablesList = Stream.concat(primaryVariablesList.stream(),
                    secondaryVariablesList.stream()).toList();
            int allVariablesListSize = allVariablesList.size();

            int numberOfVariablesToWatch = rand.nextInt(3);
            List<ObservableVariable> variablesToWatch = new ArrayList<>();

            for (int i = 0; i < numberOfVariablesToWatch; i++) {
                ObservableVariable variableToWatch = allVariablesList.get(rand.nextInt(allVariablesListSize));
                variablesToWatch.add(variableToWatch);
            }

            ObservableVariable secondaryVariable = new ObservableVariable(variablesToWatch, Integer.toString(variableIndex));
            secondaryVariablesList.add(secondaryVariable);

            variablesToWatch.forEach((variable) -> variable.variablesToNotify.add(secondaryVariable));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
            int finalThreadIndex = threadIndex;
            executorService.execute(() -> {
                try {
                    threadFunction(finalThreadIndex);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        consistencyCheckForSecondaryVariables();
    }
}