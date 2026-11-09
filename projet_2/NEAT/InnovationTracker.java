import java.util.HashMap;
import java.util.Map;

public class InnovationTracker {

	    private int nextInnovation = 1;
	    private int nextNodeId = 4;

	    private Map<String, Integer> innovations = new HashMap<>();
	    private Map<Integer, Integer> splitNodes = new HashMap<>();

	    public int getInnovation(int inNode, int outNode) {

		String key = inNode + "->" + outNode;

		if (!innovations.containsKey(key)) {
		    innovations.put(key, nextInnovation);
		    nextInnovation++;
		}

		return innovations.get(key);
	    }

	    public int getNodeForSplit(int connectionInnovation) {

		if (!splitNodes.containsKey(connectionInnovation)) {
		    splitNodes.put(connectionInnovation, nextNodeId);
		    nextNodeId++;
		}

		return splitNodes.get(connectionInnovation);
	    }
}

