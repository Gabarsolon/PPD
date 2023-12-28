import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    enum EXECUTION_TYPE {
        SEQUENTIAL,
        PARALLEL
    }

    static Integer START_VERTEX = 0;
    static Integer totalNumberOfEdges = 400;
    static Integer numberOfVerticies = 20;
    static Map<Integer, List<Integer>> outboundEdgesMap;

    static boolean hamCycleUtilParallel(List<Integer> cycle, Map<Integer, Boolean> visitedVertices, Integer startVertex, Integer lastAddedVertex) throws ExecutionException, InterruptedException {
        if (cycle.size() == numberOfVerticies) {
            if (outboundEdgesMap.get(lastAddedVertex).contains(startVertex)) {
                cycle.add(startVertex);
                printHamiltonianCycle(cycle);
                return true;
            } else
                return false;
        }

        List<CompletableFuture<Boolean>> completableFutures = new ArrayList<>();
        for (var nextVertex : outboundEdgesMap.get(lastAddedVertex)) {
            if (visitedVertices.get(nextVertex) == null) {
                // Create a copy of the cycle and visited vertices
                List<Integer> updatedCycle = new ArrayList<>(cycle);
                Map<Integer, Boolean> updatedVisitedVertices = new HashMap<>(visitedVertices);

                updatedCycle.add(nextVertex);
                updatedVisitedVertices.put(nextVertex, true);

                completableFutures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        return hamCycleUtilParallel(updatedCycle, updatedVisitedVertices, startVertex, nextVertex);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        }

        for(var completableFuture : completableFutures)
            if(completableFuture.get())
                return true;

        return false;
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
        if (executionType == EXECUTION_TYPE.SEQUENTIAL)
            hasHamiltonianCycle = hamCycleUtilSequential(cycle, visitedVertices, startVertex, startVertex);
        else
            hasHamiltonianCycle = hamCycleUtilParallel(cycle, visitedVertices, startVertex, startVertex);

        if (!hasHamiltonianCycle) {
            return null;
        }

        return cycle;
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