
public class Individual {

    private byte[] genes;
    private double fitness = -1;

    // Créer un individu avec une longueur choisie
    public Individual(int geneLength) {
        if (geneLength <= 0) {
            throw new IllegalArgumentException(
                "Gene length must be positive"
            );
        }

        genes = new byte[geneLength];

        for (int i = 0; i < genes.length; i++) {
            genes[i] = (byte) Math.round(Math.random());
        }
    }

    // Constructeur par défaut, pour conserver la compatibilité
    public Individual() {
        this(64);
    }

    // Copier un individu
    public Individual(Individual other) {
        this.genes = other.genes.clone();
        this.fitness = other.fitness;
    }

    protected byte getSingleGene(int index) {
        return genes[index];
    }

    protected int getDefaultGeneLength() {
        return genes.length;
    }

    protected void setSingleGene(int index, byte value) {
        genes[index] = value;
        fitness = -1;
    }

    // Remplacer le chromosome par un tableau de taille différente
    protected void setGenes(byte[] newGenes) {
        if (newGenes.length == 0) {
            throw new IllegalArgumentException(
                "Chromosome cannot be empty"
            );
        }

        genes = newGenes.clone();
        fitness = -1;
    }

    public double getFitness() {
        if (fitness == -1) {
            fitness = SimpleGeneticAlgorithm.getFitness(this);
        }
        return fitness;
    }
    	//TRansformer les 72 bits en 9 parametres 
	public double[] decodeParameters() {

	    double[] parameters = new double[9];

	    for (int p = 0; p < 9; p++) {

		int value = 0;

		for (int j = 0; j < 8; j++) {

		    value = value * 2
		            + genes[p * 8 + j];
		}

		parameters[p] =
		        -5.0 + value * 10.0 / 255.0;
	    }

	    return parameters;
	}
	
	public int getAccuracy() {

	    XORNetwork network =
		    new XORNetwork(decodeParameters());

	    int[][] inputs = {
		{0, 0},
		{0, 1},
		{1, 0},
		{1, 1}
	    };

	    int[] expected = {0, 1, 1, 0};

	    int correct = 0;

	    for (int i = 0; i < inputs.length; i++) {

		double output = network.predict(
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



    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (byte gene : genes) {
            result.append(gene);
        }

        return result.toString();
    }
}
