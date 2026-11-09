import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Classe qui va faire évoluer la population et résoudre XOR 
	Séléction -> Croisement -> Elitisme -> Nouvelle génération 
	jusqu'à obtention d'une bonne solution XOR
	
	Pour les mutations :
		modification du poids 80%
		Ajout d'une connexion 40%
		Ajout d'une neurone 30%
		Activer/Désactiver une connexion 10%
**/
public class NEATAlgorithm {

	    private List<Genome> population;
	    private InnovationTracker tracker;
	    private Random random = new Random();

	    private int populationSize;
	    private int maxGenerations;
	    
	    // Création de la population 	
	    public NEATAlgorithm(int populationSize, int maxGenerations) {

		this.populationSize = populationSize;
		this.maxGenerations = maxGenerations;

		tracker = new InnovationTracker();

		Population initialPopulation =
		        new Population(populationSize, tracker);

		population = initialPopulation.getGenomes();
	    }

	    // Copier un génome sans modifier l'original
	    private Genome copyGenome(Genome original) {

		Genome copy = new Genome();

		for (NodeGene node : original.getNodes()) {
		    copy.addNode(new NodeGene(
		        node.getId(),
		        node.getType()
		    ));
		}

		for (ConnectionGene connection : original.getConnections()) {
		    copy.addConnection(new ConnectionGene(
		        connection.getInNode(),
		        connection.getOutNode(),
		        connection.getWeight(),
		        connection.isEnabled(),
		        connection.getInnovation()
		    ));
		}

		copy.setFitness(original.getFitness());

		return copy;
	    }

	    // Sélection par tournoi
	    private Genome selectParent() {

		Genome best = null;

		for (int i = 0; i < 3; i++) {

		    Genome candidate = population.get(
		        random.nextInt(population.size())
		    );

		    if (best == null
		            || candidate.getFitness() > best.getFitness()) {
		        best = candidate;
		    }
		}

		return best;
	    }
		    
		private Genome crossover(Genome parent1, Genome parent2) {

		    // Le parent1 doit être le plus performant
		    if (parent2.getFitness() > parent1.getFitness()) {
			Genome temp = parent1;
			parent1 = parent2;
			parent2 = temp;
		    }

		    Genome child = new Genome();

		    // Copier les neurones du parent le plus performant
		    for (NodeGene node : parent1.getNodes()) {
			child.addNode(new NodeGene(
			    node.getId(),
			    node.getType()
			));
		    }

		    // Connexions du deuxième parent par innovation
		    Map<Integer, ConnectionGene> genes2 = new HashMap<>();

		    for (ConnectionGene connection : parent2.getConnections()) {
			genes2.put(connection.getInnovation(), connection);
		    }

		    // Construire l'enfant
		    for (ConnectionGene gene1 : parent1.getConnections()) {

			ConnectionGene chosen = gene1;

			if (genes2.containsKey(gene1.getInnovation())
				&& random.nextBoolean()) {

			    chosen = genes2.get(gene1.getInnovation());
			}

			boolean enabled = chosen.isEnabled();

			// Vérifier si cette connexion crée un cycle
			if (enabled && createsCycle(child,
				chosen.getInNode(),
				chosen.getOutNode())) {

			    enabled = false;
			}

			child.addConnection(new ConnectionGene(
			    chosen.getInNode(),
			    chosen.getOutNode(),
			    chosen.getWeight(),
			    enabled,
			    chosen.getInnovation()
			));
		    }

		    return child;
		}
	
	    // Appliquer les mutations
	    private void mutate(Genome genome) {

		if (random.nextDouble() < 0.8) {
		    genome.mutateWeights();
		}

		if (random.nextDouble() < 0.4) {
		    genome.mutateAddConnection(tracker);
		}

		if (random.nextDouble() < 0.35) {
		    genome.mutateAddNode(tracker);
		}

		if (random.nextDouble() < 0.2) {
		    genome.mutateToggleConnection();
		}
	    }

	    // Calculer la fitness de toute la population
	    private void evaluatePopulation() {

		for (Genome genome : population) {
		    genome.evaluateFitness();
		}
	    }

	    // Créer la génération suivante
	    private void nextGeneration() {

		population.sort(
		    Comparator.comparingDouble(Genome::getFitness).reversed()
		);

		List<Genome> newPopulation = new ArrayList<>();

		// Élitisme : conserver les 5 meilleurs individu
		for (int i=0; i< 5; i++){
			newPopulation.add(copyGenome(population.get(i)));
		}
		while (newPopulation.size() < populationSize) {

		    Genome parent1 = selectParent();
		    Genome parent2 = selectParent();
		    

		while (parent1 == parent2) {
		    parent2 = selectParent();
		}

		    Genome child = crossover(parent1, parent2);

		    mutate(child);

		    newPopulation.add(child);
		}

		population = newPopulation;
	    }

	    // Lancer l'évolution
	    public Genome run() {

		Genome best = null;

		for (int generation = 1;
		        generation <= maxGenerations;
		        generation++) {

		    evaluatePopulation();

		    population.sort(
		        Comparator.comparingDouble(Genome::getFitness).reversed()
		    );

		    best = population.get(0);

		    System.out.println(
		        "Generation : " + generation
		        + " | Fitness : " + best.getFitness()
		    );
		    
		    if (generation % 100 == 0) {

			    System.out.println(
				"Neurones : " + best.getNodes().size()
				+ " | Connexions : " +
				 best.getConnections().size()
				+ " | Accuracy : " + getAccuracy(best) + "/4"
			    );
		     }

		    // Vérifier si le réseau a résolu XOR
		    if (getAccuracy(best) == 4
		            && best.getFitness() >= 0.99) {

		        System.out.println("Solution trouvee !");
		        return best;
		    }

		    if (generation < maxGenerations) {
		        nextGeneration();
		    }
		}

		return best;
	    }

	    // Calculer le nombre de réponses XOR correctes
	    public int getAccuracy(Genome genome) {

		int correct = 0;

		int[][] inputs = {
		    {0, 0},
		    {0, 1},
		    {1, 0},
		    {1, 1}
		};

		int[] expected = {0, 1, 1, 0};

		for (int i = 0; i < 4; i++) {

		    double output = genome.predict(
		        inputs[i][0],
		        inputs[i][1]
		    );

		    int prediction = output >= 0.5 ? 1 : 0;

		    if (prediction == expected[i]) {
		        correct++;
		    }
		}

		return correct;
	    }
	    
	private boolean createsCycle(Genome genome, int from, int to) {

	    return hasPath(genome, to, from);
	}

	private boolean hasPath(Genome genome, int start, int target) {

	    if (start == target) {
		return true;
	    }

	    for (ConnectionGene connection : genome.getConnections()) {

		if (connection.isEnabled()
		        && connection.getInNode() == start) {

		    if (hasPath(
		            genome,
		            connection.getOutNode(),
		            target)) {

		        return true;
		    }
		}
	    }

	    return false;
	}

}

