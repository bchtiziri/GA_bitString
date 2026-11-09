
public class SimpleGeneticAlgorithm {

    private static final double mutationRate = 0.025;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    public boolean runAlgorithm(
            int populationSize,
            int maxGeneration,
            String selectionMethod,
            double crossoverRate) {

        if (populationSize <= 0 || maxGeneration <= 0
                || Double.isNaN(crossoverRate)
                || crossoverRate < 0.0
                || crossoverRate > 1.0) {
            throw new IllegalArgumentException(
                "Invalid algorithm parameters"
            );
        }

        if (selectionMethod == null
                || (!selectionMethod.equalsIgnoreCase("roulette")
                && !selectionMethod.equalsIgnoreCase("tournament"))) {
            throw new IllegalArgumentException(
                "Unknown selection method: " + selectionMethod
            );
        }

        Population myPop = new Population(populationSize, true);

        int generationCount = 1;

        // Faire évoluer les générations
        while ((myPop.getFittest().getAccuracy() < 4
                || myPop.getFittest().getFitness() < 0.99)
                && generationCount < maxGeneration) {

            System.out.println(
                "Generation: " + generationCount
                + " Fitness: "
                + myPop.getFittest().getFitness()
            );

            myPop = evolvePopulation(
                myPop, selectionMethod, crossoverRate
            );

            generationCount++;
        }

        Individual best = myPop.getFittest();

        // Afficher les résultats XOR du meilleur individu
        XORNetwork network =
                new XORNetwork(best.decodeParameters());

        int[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };

        int[] expected = {0, 1, 1, 0};

        System.out.println();
        System.out.println("Résultats XOR :");

        for (int i = 0; i < inputs.length; i++) {

            double output = network.predict(
                inputs[i][0],
                inputs[i][1]
            );

            int prediction = output >= 0.5 ? 1 : 0;

            System.out.println(
                inputs[i][0] + " XOR " + inputs[i][1]
                + " = " + prediction
                + " (attendu : " + expected[i] + ")"
                + " | sortie : " + output
            );
        }

        System.out.println();
        System.out.println(
            "Accuracy : " + best.getAccuracy() + "/4"
        );

        // Vérifier si le GA a réussi
        if (best.getAccuracy() == 4
                && best.getFitness() >= 0.99) {

            System.out.println("Solution trouvée !");
            System.out.println(
                "Generation: " + generationCount
            );
            System.out.println(
                "Fitness: " + best.getFitness()
            );
            System.out.println("Genes: ");
            System.out.println(best);
            
		double[] parameters = best.decodeParameters();

		System.out.println("Paramètres du réseau :");

		System.out.println("Poids A -> H1 : " + parameters[0]);
		System.out.println("Poids A -> H2 : " + parameters[1]);
		System.out.println("Poids B -> H1 : " + parameters[2]);
		System.out.println("Poids B -> H2 : " + parameters[3]);

		System.out.println("Poids H1 -> Y : " + parameters[4]);
		System.out.println("Poids H2 -> Y : " + parameters[5]);

		System.out.println("Biais H1 : " + parameters[6]);
		System.out.println("Biais H2 : " + parameters[7]);
		System.out.println("Biais Y  : " + parameters[8]);


            return true;

        } else {

            System.out.println(
                "Maximum number of generations reached."
            );
            System.out.println(
                "Best fitness: " + best.getFitness()
            );
            System.out.println(
                "Best chromosome: " + best
            );
            System.out.println(
                "Chromosome length: "
                + best.getDefaultGeneLength()
            );

            return false;
        }
    }

    public Population evolvePopulation(
            Population pop,
            String selectionMethod,
            double crossoverRate) {

        Population newPopulation =
            new Population(pop.getIndividuals().size(), false);

        int elitismOffset = 0;

        // Conserver une copie du meilleur individu
        if (elitism) {
            newPopulation.getIndividuals().add(
                new Individual(pop.getFittest())
            );
            elitismOffset = 1;
        }

        for (int i = elitismOffset;
                i < pop.getIndividuals().size();
                i++) {

            Individual indiv1;
            Individual indiv2;

            // Choisir les parents
            if (selectionMethod.equalsIgnoreCase("roulette")) {

                indiv1 = rouletteSelection(pop);
                indiv2 = rouletteSelection(pop);

            } else if (selectionMethod.equalsIgnoreCase("tournament")) {

                indiv1 = tournamentSelection(pop);
                indiv2 = tournamentSelection(pop);

            } else {

                throw new IllegalArgumentException(
                    "Unknown selection method: " + selectionMethod
                );
            }

            Individual newIndiv;

            // Appliquer ou non le croisement
            if (Math.random() < crossoverRate) {
                newIndiv = crossover(indiv1, indiv2);
            } else {
                newIndiv = new Individual(indiv1);
            }

            // La mutation est indépendante du croisement
            mutate(newIndiv);

            newPopulation.getIndividuals().add(newIndiv);
        }

        return newPopulation;
    }

    // Croisement à un point
    private Individual crossover(
            Individual indiv1,
            Individual indiv2) {

        int length = indiv1.getDefaultGeneLength();

        int cut = (int) (Math.random() * (length + 1));

        byte[] newGenes = new byte[length];

        for (int i = 0; i < cut; i++) {
            newGenes[i] = indiv1.getSingleGene(i);
        }

        for (int i = cut; i < length; i++) {
            newGenes[i] = indiv2.getSingleGene(i);
        }

        Individual child = new Individual(length);
        child.setGenes(newGenes);

        return child;
    }

    // Mutation classique : inverser les bits
    private void mutate(Individual indiv) {

        for (int i = 0;
                i < indiv.getDefaultGeneLength();
                i++) {

            if (Math.random() < mutationRate) {

                byte newGene =
                    (byte) (1 - indiv.getSingleGene(i));

                indiv.setSingleGene(i, newGene);
            }
        }
    }

    // Sélection par tournoi
    private Individual tournamentSelection(Population pop) {

        Population tournament =
            new Population(tournamentSize, false);

        for (int i = 0; i < tournamentSize; i++) {

            int randomId = (int) (
                Math.random() * pop.getIndividuals().size()
            );

            tournament.getIndividuals().add(
                pop.getIndividual(randomId)
            );
        }

        return tournament.getFittest();
    }

    // Sélection par roulette
    private Individual rouletteSelection(Population pop) {

        double totalFitness = 0;

        for (Individual indiv : pop.getIndividuals()) {
            totalFitness += indiv.getFitness();
        }

        // Si toutes les fitness valent zéro
        if (totalFitness == 0) {

            int randomId = (int) (
                Math.random() * pop.getIndividuals().size()
            );

            return pop.getIndividual(randomId);
        }

        double randomValue =
            Math.random() * totalFitness;

        double cumulativeFitness = 0;

        for (Individual indiv : pop.getIndividuals()) {

            cumulativeFitness += indiv.getFitness();

            if (cumulativeFitness > randomValue) {
                return indiv;
            }
        }

        return pop.getIndividual(
            pop.getIndividuals().size() - 1
        );
    }

    // Fitness : erreur du réseau sur les 4 cas XOR
    protected static double getFitness(Individual individual) {

        XORNetwork network =
                new XORNetwork(individual.decodeParameters());

        int[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };

        int[] expected = {0, 1, 1, 0};

        double totalError = 0.0;

        for (int i = 0; i < inputs.length; i++) {

            double output = network.predict(
                inputs[i][0],
                inputs[i][1]
            );

            double error = expected[i] - output;

            totalError += error * error;
        }

        return 1.0 / (1.0 + totalError);
    }
}

