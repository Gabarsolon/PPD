import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    enum EXECUTION_TYPE {
        SEQUENTIAL,
        PARALLEL
    }

    static Integer START_VERTEX = 0;
    static Integer totalNumberOfEdges = 10000;
    static Integer numberOfVerticies = 20;
    static Map<Integer, List<Integer>> outboundEdgesMap;
    static private ReentrantLock foundHamiltonianCycleLock = new ReentrantLock();
    static private Condition foundHamiltonianCycleCondition = foundHamiltonianCycleLock.newCondition();
    static private List<Integer> foundHamiltonianCycle = null;
    static private Integer MAX_DEPTH = 1;
    static private AtomicInteger functionCount = new AtomicInteger(0);

    static void hamCycleUtilParallel(List<Integer> cycle, Map<Integer, Boolean> visitedVertices, Integer startVertex, Integer lastAddedVertex, Integer currentDepth) throws ExecutionException, InterruptedException {
        functionCount.getAndIncrement();
//        System.out.println(cycle);
        if (cycle.size() == numberOfVerticies) {
            if (outboundEdgesMap.get(lastAddedVertex).contains(startVertex)) {
                foundHamiltonianCycleLock.lock();
                cycle.add(startVertex);
                foundHamiltonianCycle = cycle;
                foundHamiltonianCycleCondition.signalAll();
                foundHamiltonianCycleLock.unlock();
            }
            functionCount.getAndDecrement();
            if (functionCount.get() < 1) {
                foundHamiltonianCycleLock.lock();
                foundHamiltonianCycleCondition.signalAll();
                foundHamiltonianCycleLock.unlock();
            }
            return;
        }

        for (var nextVertex : outboundEdgesMap.get(lastAddedVertex)) {
            if (visitedVertices.get(nextVertex) != null) {
                continue;
            }

            if (currentDepth > MAX_DEPTH) {
                cycle.add(nextVertex);
                visitedVertices.put(nextVertex, true);

                hamCycleUtilParallel(cycle, visitedVertices, startVertex, nextVertex, currentDepth);

                cycle.remove(cycle.size() - 1);
                visitedVertices.remove(nextVertex);
            } else {

                // Create a copy of the cycle and visited vertices
                List<Integer> updatedCycle = new ArrayList<>();
                Map<Integer, Boolean> updatedVisitedVertices = new HashMap<>();

                updatedCycle.addAll(cycle);
                updatedVisitedVertices.putAll(visitedVertices);

                updatedCycle.add(nextVertex);
                updatedVisitedVertices.put(nextVertex, true);

                CompletableFuture.runAsync(() -> {
                    try {
                        hamCycleUtilParallel(updatedCycle, updatedVisitedVertices, startVertex, nextVertex, currentDepth + 1);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        functionCount.getAndDecrement();
    }

    static boolean hamCycleUtilSequential(List<Integer> cycle, Map<Integer, Boolean> visitedVertices, Integer startVertex, Integer lastAddedVertex) {
        if (cycle.size() == numberOfVerticies) {
            if (outboundEdgesMap.get(lastAddedVertex).contains(startVertex)) {
                cycle.add(startVertex);
                return true;
            } else
                return false;
        }

        for (var nextVertex : outboundEdgesMap.get(lastAddedVertex)) {
            if (visitedVertices.get(nextVertex) == null) {

                cycle.add(nextVertex);
                visitedVertices.put(nextVertex, true);

                if (hamCycleUtilSequential(cycle, visitedVertices, startVertex, nextVertex))
                    return true;

                cycle.remove(cycle.size() - 1);
                visitedVertices.remove(nextVertex);
            }
        }

        return false;
    }

    static List<Integer> getHamiltonianCycleStartingFromGivenVertex(int startVertex, EXECUTION_TYPE executionType) throws ExecutionException, InterruptedException {
        var cycle = new ArrayList<Integer>();
        var visitedVertices = new HashMap<Integer, Boolean>();

        cycle.add(startVertex);
        visitedVertices.put(startVertex, true);

        boolean hasHamiltonianCycle;
        if (executionType == EXECUTION_TYPE.SEQUENTIAL) {
            hasHamiltonianCycle = hamCycleUtilSequential(cycle, visitedVertices, startVertex, startVertex);
            if (hasHamiltonianCycle)
                return cycle;
        } else {
            foundHamiltonianCycleLock.lock();
            hamCycleUtilParallel(cycle, visitedVertices, startVertex, startVertex, 0);
            foundHamiltonianCycleCondition.await();
            foundHamiltonianCycleLock.unlock();
            return foundHamiltonianCycle;
        }


        return null;
    }


    public static void printHamiltonianCycle(List<Integer> hamiltonianCycle) {
        if (hamiltonianCycle == null)
            System.out.printf("There isn't any hamiltonian cycle starting from node %d%n", START_VERTEX);
        else
            System.out.printf(
                    "A hamiltonian cycle corresponding to the graph is:\n %s%n",
                    hamiltonianCycle
                            .toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "->")
            );
    }

    static void generateRandomGraph() {
        var random = new Random();
        var currentNumberOfAddedEdges = 0;
        outboundEdgesMap = new HashMap<>();

        for (Integer vertex = 0; vertex < numberOfVerticies; vertex++) {
            List<Integer> listOfVertices = new ArrayList<>();

            for (int currentVertex = random.nextInt(0, numberOfVerticies / 2);
                 currentVertex < numberOfVerticies && currentNumberOfAddedEdges < totalNumberOfEdges;
                 currentVertex += random.nextInt(1, numberOfVerticies / 2 + 1)
            ) {
                listOfVertices.add(currentVertex);
                currentNumberOfAddedEdges++;
            }

            outboundEdgesMap.put(vertex, listOfVertices);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        outboundEdgesMap = Map.of(
//                0, List.of(1),
//                1, List.of(2, 3),
//                2, List.of(4),
//                3, List.of(2),
//                4, List.of(0)
//        );
//        numberOfVerticies = 5;

        generateRandomGraph();
        System.out.println(outboundEdgesMap.entrySet());
        System.out.println("-----------------------------------------------------");

        var startTime = System.nanoTime();
        var hamiltonianCycle = getHamiltonianCycleStartingFromGivenVertex(START_VERTEX, EXECUTION_TYPE.SEQUENTIAL);
        var endTime = System.nanoTime();

        System.out.printf("Sequential execution finished in: %dms\n", (endTime - startTime) / 1000000);
        printHamiltonianCycle(hamiltonianCycle);

        startTime = System.nanoTime();
        hamiltonianCycle = getHamiltonianCycleStartingFromGivenVertex(START_VERTEX, EXECUTION_TYPE.PARALLEL);
        endTime = System.nanoTime();

        System.out.printf("Parallel execution finished in: %dms\n", (endTime - startTime) / 1000000);
        printHamiltonianCycle(hamiltonianCycle);


        System.out.println("--------------------------------------------------------");
        printHamiltonianCycle(hamiltonianCycle);
    }
}