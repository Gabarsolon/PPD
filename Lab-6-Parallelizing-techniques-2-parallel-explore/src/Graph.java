import java.util.List;
import java.util.Map;

public class Graph {
    Map<Integer, List<Integer>> outboundEdgesMap;
    Integer numberOfVertices;
    public Graph(int numberOfVertices){
        this.numberOfVertices = numberOfVertices;
    }

}
