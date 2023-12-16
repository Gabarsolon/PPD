import java.util.*;

public class Main {
    static Integer numberOfVerticies = 5;
    static Map<Integer, List<Integer>> outboundEdgesMap = Map.of(
            0, List.of(1),
            1, List.of(2, 3),
            2, List.of(4),
            3, List.of(2),
            4, List.of(0)
    );

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

    public static void main(String[] args) {
        var hamiltonianCycle = getHamiltonianCycleStartingFromGivenVertex(0);

        if (hamiltonianCycle == null)
            System.out.println("There isn't any hamiltonian cycle");
        else
            System.out.println("A hamiltonian cycle corresponding to the graph is:\n %s".formatted(
                    hamiltonianCycle
                            .toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "->")
            ));
    }
}