import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static Integer START_VERTEX = 0;
    static Integer totalNumberOfEdges = 20;
    static Integer numberOfVerticies = 5;
    static Map<Integer, List<Integer>> outboundEdgesMap;

    static boolean hamCycleUtil(List<Integer> cycle, Map<Integer, Boolean> visitedVertices, Integer startVertex, Integer lastAddedVertex) {
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

                if (hamCycleUtil(cycle, visitedVertices, startVertex, nextVertex))
                    return true;

                cycle.remove(cycle.size() - 1);
                visitedVertices.remove(nextVertex);
            }
        }

        return false;
    }

    static List<Integer> getHamiltonianCycleStartingFromGivenVertex(int startVertex) {
        var cycle = new ArrayList<Integer>();
        var visitedVertices = new HashMap<Integer, Boolean>();

        cycle.add(startVertex);
        visitedVertices.put(startVertex, true);

        if (!hamCycleUtil(cycle, visitedVertices, startVertex, startVertex)) {
            System.out.println(cycle);
            return null;
        }

        return cycle;
    }

    public static void printHamiltonianCycle(List<Integer> hamiltonianCycle) {
        if (hamiltonianCycle == null)
            System.out.println("There isn't any hamiltonian cycle");
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

    public static void main(String[] args) {
//        outboundEdgesMap = Map.of(
//                0, List.of(1),
//                1, List.of(2, 3),
//                2, List.of(4),
//                3, List.of(2),
//                4, List.of(0)
//        );

        generateRandomGraph();
        System.out.println(outboundEdgesMap.entrySet());
        System.out.println("-----------------------------------------------------");

        var startTime = System.nanoTime();
        var hamiltonianCycle = getHamiltonianCycleStartingFromGivenVertex(START_VERTEX);
        var endTime = System.nanoTime();

        System.out.printf("Regular multiplication sequential finished in: %dms\n", (endTime - startTime) / 1000000);
        System.out.println("--------------------------------------------------------");
        printHamiltonianCycle(hamiltonianCycle);
    }
}