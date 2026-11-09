import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {

	private List<Genome> genomes;
	private Random random = new Random();
	
	
	public Population(int size, InnovationTracker tracker) {

		genomes = new ArrayList<>();

		for (int i = 0; i < size; i++) {

			Genome genome = new Genome();

			// Créer les neurones
			genome.addNode(new NodeGene(0, NodeGene.Type.INPUT));
			genome.addNode(new NodeGene(1, NodeGene.Type.INPUT));
			genome.addNode(new NodeGene(2, NodeGene.Type.OUTPUT));
			genome.addNode(new NodeGene(3, NodeGene.Type.BIAS));

			// Créer les connexions avec des poids aléatoires
			genome.addConnection(new ConnectionGene(
				0, 2, randomWeight(), true,
				tracker.getInnovation(0, 2)
			));

			genome.addConnection(new ConnectionGene(
				1, 2, randomWeight(), true,
				tracker.getInnovation(1, 2)
			));

			genome.addConnection(new ConnectionGene(
				3, 2, randomWeight(), true,
				tracker.getInnovation(3, 2)
			));

			genomes.add(genome);
		}
	}

	private double randomWeight() {
		return random.nextDouble() * 2 - 1;
	}

	public List<Genome> getGenomes() {
		return genomes;
	}
}

