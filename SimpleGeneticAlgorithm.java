
package com.baeldung.algorithms.ga.binary;

public class SimpleGeneticAlgorithm {

    private static final double mutationRate = 0.025;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    private static byte[] solution = new byte[0];

    public boolean runAlgorithm(
            int populationSize,
            String solution,
            int maxGeneration,
            String selectionMethod,
            double crossoverRate) {

        // Vérification des paramètres
        if (solution == null || !solution.matches("[01]+")) {
            throw new IllegalArgumentException(
                "Solution must be a non-empty binary string"
            );
        }

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

        setSolution(solution);

        Population myPop = new Population(populationSize, true);
	System.out.println("Longueurs des chromosomes :");
	// voir les longueurs 
	for (Individual indiv : myPop.getIndividuals()) {
	    System.out.println(indiv.getDefaultGeneLength());
	}
        int generationCount = 1;

        while (myPop.getFittest().getFitness() < getMaxFitness()
                && generationCount < maxGeneration) {

            System.out.println(
                "Generation: " + generationCount
                + " Correct genes found: "
                + myPop.getFittest().getFitness()
            );

            myPop = evolvePopulation(
                myPop, selectionMethod, crossoverRate
            );

            generationCount++;
        }

        Individual best = myPop.getFittest();

        if (best.getFitness() == getMaxFitness()) {

            System.out.println("Solution found!");
            System.out.println("Generation: " + generationCount);
            System.out.println("Genes: ");
            System.out.println(best);

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

    // Croisement à un point avec longueurs variables
    private Individual crossover(
            Individual indiv1,
            Individual indiv2) {

        int length1 = indiv1.getDefaultGeneLength();
        int length2 = indiv2.getDefaultGeneLength();

        int cut1 = (int) (Math.random() * (length1 + 1));
        int cut2 = (int) (Math.random() * (length2 + 1));

        int newLength = cut1 + (length2 - cut2);

        // Éviter de créer un chromosome vide
        if (newLength == 0) {
            return new Individual(indiv1);
        }

        byte[] newGenes = new byte[newLength];

        // Première partie : parent 1
        for (int i = 0; i < cut1; i++) {
            newGenes[i] = indiv1.getSingleGene(i);
        }

        // Deuxième partie : parent 2
        for (int i = cut2; i < length2; i++) {
            newGenes[cut1 + i - cut2] =
                indiv2.getSingleGene(i);
        }

        Individual child = new Individual(newLength);
        child.setGenes(newGenes);

        return child;
    }

    // Mutation des bits et de la longueur
    private void mutate(Individual indiv) {

        // 1. Mutation classique : inverser des bits
        for (int i = 0;
                i < indiv.getDefaultGeneLength();
                i++) {

            if (Math.random() < mutationRate) {

                byte newGene =
                    (byte) (1 - indiv.getSingleGene(i));

                indiv.setSingleGene(i, newGene);
            }
        }

        // 2. Insertion d'un bit
        if (Math.random() < mutationRate) {

            int length = indiv.getDefaultGeneLength();

            int position =
                (int) (Math.random() * (length + 1));

            byte[] newGenes = new byte[length + 1];

            for (int i = 0; i < position; i++) {
                newGenes[i] = indiv.getSingleGene(i);
            }

            newGenes[position] =
                (byte) Math.round(Math.random());

            for (int i = position; i < length; i++) {
                newGenes[i + 1] = indiv.getSingleGene(i);
            }

            indiv.setGenes(newGenes);
        }

        // 3. Suppression d'un bit
        if (indiv.getDefaultGeneLength() > 1
                && Math.random() < mutationRate) {

            int length = indiv.getDefaultGeneLength();

            int position =
                (int) (Math.random() * length);

            byte[] newGenes = new byte[length - 1];

            for (int i = 0; i < position; i++) {
                newGenes[i] = indiv.getSingleGene(i);
            }

            for (int i = position + 1; i < length; i++) {
                newGenes[i - 1] = indiv.getSingleGene(i);
            }

            indiv.setGenes(newGenes);
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

    // Fitness : bits corrects et pénalité de longueur
    protected static int getFitness(Individual individual) {

        int correctBits = 0;

        int individualLength =
            individual.getDefaultGeneLength();

        int solutionLength = solution.length;

        int commonLength =
            Math.min(individualLength, solutionLength);

        for (int i = 0; i < commonLength; i++) {

            if (individual.getSingleGene(i) == solution[i]) {
                correctBits++;
            }
        }

        int lengthDifference =
            Math.abs(individualLength - solutionLength);

        return Math.max(
            0,
            correctBits - lengthDifference
        );
    }

    protected int getMaxFitness() {
        return solution.length;
    }

    // Définir la solution recherchée
    protected void setSolution(String newSolution) {

        solution = new byte[newSolution.length()];

        for (int i = 0; i < newSolution.length(); i++) {

            solution[i] =
                (byte) (newSolution.charAt(i) - '0');
        }
    }
}

