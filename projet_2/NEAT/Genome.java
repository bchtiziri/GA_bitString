
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Random;

public class Genome {

	    private List<NodeGene> nodes;
	    private List<ConnectionGene> connections;
	    private double fitness;

	    private Random random = new Random();

	    // Constructeur
	    public Genome() {
		nodes = new ArrayList<>();
		connections = new ArrayList<>();
		fitness = 0.0;
	    }

	    // Ajouter un neurone
	    public void addNode(NodeGene node) {
		nodes.add(node);
	    }

	    // Ajouter une connexion
	    public void addConnection(ConnectionGene connection) {
		connections.add(connection);
	    }

	    // Getters et setters
	    public List<NodeGene> getNodes() {
		return nodes;
	    }

	    public List<ConnectionGene> getConnections() {
		return connections;
	    }

	    public double getFitness() {
		return fitness;
	    }

	    public void setFitness(double fitness) {
		this.fitness = fitness;
	    }

	    // Fonction d'activation
	    private double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-4.9 * x));
	    }

	    // Prédiction du réseau
	    public double predict(double a, double b) {

		Map<Integer, Double> values = new HashMap<>();

		values.put(0, a);
		values.put(1, b);
		values.put(3, 1.0);

		return calculateNode(2, values, new HashSet<>());
	    }

	    // Calcul récursif des neurones
	    private double calculateNode(
		    int nodeId,
		    Map<Integer, Double> values,
		    Set<Integer> visiting) {

		if (values.containsKey(nodeId)) {
		    return values.get(nodeId);
		}

		if (!visiting.add(nodeId)) {
		    throw new IllegalStateException(
		        "Cycle détecté dans le réseau"
		    );
		}

		double sum = 0.0;

		for (ConnectionGene connection : connections) {

		    if (connection.isEnabled()
		            && connection.getOutNode() == nodeId) {

		        double inputValue = calculateNode(
		            connection.getInNode(),
		            values,
		            visiting
		        );

		        sum += inputValue * connection.getWeight();
		    }
		}

		visiting.remove(nodeId);

		double output = sigmoid(sum);
		values.put(nodeId, output);

		return output;
	    }

	    // Calcul de la fitness XOR
	    public void evaluateFitness() {

		double[][] inputs = {
		    {0, 0},
		    {0, 1},
		    {1, 0},
		    {1, 1}
		};

		double[] expected = {0, 1, 1, 0};

		double error = 0.0;

		for (int i = 0; i < 4; i++) {

		    double output = predict(
		        inputs[i][0],
		        inputs[i][1]
		    );

		    error += Math.pow(expected[i] - output, 2);
		}

		fitness = 1.0 / (1.0 + error);
	    }

	    // MUTATION 1 : modifier les poids
	    public void mutateWeights() {

		for (ConnectionGene connection : connections) {

		    if (random.nextDouble() < 0.8) {

		        double newWeight =
		            connection.getWeight()
		            + random.nextGaussian() * 0.5;

		        connection.setWeight(newWeight);
		    }
		}
	    }

	    // Vérifier s'il existe un chemin entre deux neurones
	    private boolean hasPath(int start, int target) {

		return hasPath(start, target, new HashSet<>());
	    }

	    private boolean hasPath(
		    int start,
		    int target,
		    Set<Integer> visited) {

		if (start == target) {
		    return true;
		}

		if (!visited.add(start)) {
		    return false;
		}

		for (ConnectionGene connection : connections) {

		    if (connection.isEnabled()
		            && connection.getInNode() == start) {

		        if (hasPath(
		                connection.getOutNode(),
		                target,
		                visited)) {

		            return true;
		        }
		    }
		}

		return false;
	    }

	    // MUTATION 2 : ajouter une connexion
	    public void mutateAddConnection(InnovationTracker tracker) {

		for (int attempt = 0; attempt < 30; attempt++) {

		    NodeGene from = nodes.get(
		        random.nextInt(nodes.size())
		    );

		    NodeGene to = nodes.get(
		        random.nextInt(nodes.size())
		    );

		    if (from.getType() == NodeGene.Type.OUTPUT) {
		        continue;
		    }

		    if (to.getType() == NodeGene.Type.INPUT
		            || to.getType() == NodeGene.Type.BIAS) {
		        continue;
		    }

		    if (from.getId() == to.getId()) {
		        continue;
		    }

		    boolean exists = false;

		    for (ConnectionGene connection : connections) {

		        if (connection.getInNode() == from.getId()
		                && connection.getOutNode() == to.getId()) {

		            exists = true;
		            break;
		        }
		    }

		    if (exists) {
		        continue;
		    }

		    if (hasPath(to.getId(), from.getId())) {
		        continue;
		    }

		    double weight = random.nextDouble() * 2 - 1;

		    int innovation = tracker.getInnovation(
		        from.getId(),
		        to.getId()
		    );

		    connections.add(new ConnectionGene(
		        from.getId(),
		        to.getId(),
		        weight,
		        true,
		        innovation
		    ));

		    return;
		}
	    }

	    // MUTATION 3 : ajouter un neurone caché
	    public void mutateAddNode(InnovationTracker tracker) {

		List<ConnectionGene> candidates = new ArrayList<>();

		for (ConnectionGene connection : connections) {

		    if (!connection.isEnabled()) {
		        continue;
		    }

		    int newNodeId = tracker.getNodeForSplit(
		        connection.getInnovation()
		    );

		    boolean nodeExists = false;

		    for (NodeGene node : nodes) {

		        if (node.getId() == newNodeId) {
		            nodeExists = true;
		            break;
		        }
		    }

		    if (!nodeExists) {
		        candidates.add(connection);
		    }
		}

		if (candidates.isEmpty()) {
		    return;
		}

		ConnectionGene oldConnection = candidates.get(
		    random.nextInt(candidates.size())
		);

		int inNode = oldConnection.getInNode();
		int outNode = oldConnection.getOutNode();
		double oldWeight = oldConnection.getWeight();

		int newNodeId = tracker.getNodeForSplit(
		    oldConnection.getInnovation()
		);

		// Désactiver l'ancienne connexion
		oldConnection.setEnabled(false);

		// Ajouter le nouveau neurone caché
		nodes.add(new NodeGene(
		    newNodeId,
		    NodeGene.Type.HIDDEN
		));

		// Connexion : ancien départ -> nouveau neurone
		connections.add(new ConnectionGene(
		    inNode,
		    newNodeId,
		    1.0,
		    true,
		    tracker.getInnovation(inNode, newNodeId)
		));

		// Connexion : nouveau neurone -> ancienne arrivée
		connections.add(new ConnectionGene(
		    newNodeId,
		    outNode,
		    oldWeight,
		    true,
		    tracker.getInnovation(newNodeId, outNode)
		));
	    }

	    // MUTATION 4 : activer ou désactiver une connexion
	    public void mutateToggleConnection() {

		if (connections.isEmpty()) {
		    return;
		}

		ConnectionGene connection = connections.get(
		    random.nextInt(connections.size())
		);

		if (connection.isEnabled()) {

		    connection.setEnabled(false);

		} else {

		    // Réactiver uniquement si aucun cycle n'est créé
		    if (!hasPath(
		            connection.getOutNode(),
		            connection.getInNode())) {

		        connection.setEnabled(true);
		    }
		}
	    }
}

