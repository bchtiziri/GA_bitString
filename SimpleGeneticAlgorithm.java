package com.baeldung.algorithms.ga.binary;


public class SimpleGeneticAlgorithm {

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.025;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    private static byte[] solution = new byte[64];

    public boolean runAlgorithm(int populationSize, String solution, int maxGeneration, String selectionMethod,double crossoverRate) {
        if (solution.length() != SimpleGeneticAlgorithm.solution.length) {
            throw new RuntimeException("The solution needs to have " + SimpleGeneticAlgorithm.solution.length + " bytes");
        }
        setSolution(solution);
        Population myPop = new Population(populationSize, true);

        int generationCount = 1;
        while (myPop.getFittest().getFitness() < getMaxFitness() && generationCount < maxGeneration) {
            System.out.println("Generation: " + generationCount + " Correct genes found: " + myPop.getFittest().getFitness());
            myPop = evolvePopulation(myPop, selectionMethod, crossoverRate);
            generationCount++;
        }
	if(myPop.getFittest().getFitness() == getMaxFitness()) {

        	System.out.println("Solution found!");
        	System.out.println("Generation: " + generationCount);
        	System.out.println("Genes: ");
        	System.out.println(myPop.getFittest());
        	return true;
	}else{
		System.out.println("Maximum number of generation reached.");
		System.out.println("Best fitness: " + myPop.getFittest().getFitness());
		return false;
	}
    }

    public Population evolvePopulation(Population pop, String selectionMethod, double crossoverRate) {
        int elitismOffset;
        Population newPopulation = new Population(pop.getIndividuals().size(), false);

        if (elitism) {
            newPopulation.getIndividuals().add(0, pop.getFittest());
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
	// depends on the method chosen 
        for (int i = elitismOffset; i < pop.getIndividuals().size(); i++) {
            Individual indiv1;
            Individual indiv2;

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
            if(Math.random()<crossoverRate){
                Individual newIndiv = crossover(indiv1, indiv2);
                mutate(newIndiv);
                newPopulation.getIndividuals().add(i, newIndiv);
            }
            else{
                newPopulation.getIndividuals().add(i, new Individual(indiv1));
            }
        }

        return newPopulation;
    }

    private Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        for (int i = 0; i < newSol.getDefaultGeneLength(); i++) {
            if (Math.random() <= uniformRate) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i));
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i));
            }
        }
        return newSol;
    }

    private void mutate(Individual indiv) {
        for (int i = 0; i < indiv.getDefaultGeneLength(); i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                indiv.setSingleGene(i, gene);
            }
        }
    }
	// Tournament selection 
    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            tournament.getIndividuals().add(i, pop.getIndividual(randomId));
        }
        Individual fittest = tournament.getFittest();
        return fittest;
    }
    	

    protected static int getFitness(Individual individual) {
        int fitness = 0;
        for (int i = 0; i < individual.getDefaultGeneLength() && i < solution.length; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                fitness++;
            }
        }
        return fitness;
    }

    protected int getMaxFitness() {
        int maxFitness = solution.length;
        return maxFitness;
    }

    protected void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            String character = newSolution.substring(i, i + 1);
            if (character.contains("0") || character.contains("1")) {
                solution[i] = Byte.parseByte(character);
            } else {
                solution[i] = 0;
            }
        }
    }
    
    //roulette selection 
    private Individual rouletteSelection(Population pop){
    	double totalFitness = 0;
    	
    	for (Individual indv : pop.getIndividuals()){
    		totalFitness += indv.getFitness();
    	}
    	// If all the fitnesses are nul
    	if (totalFitness == 0){
    		int randomId = (int) (Math.random() * pop.getIndividuals().size());
    		return pop.getIndividual(randomId);
    	}
    	
    	// random draw
    	double randomValue = Math.random() * totalFitness;
    	
    	// select the indv 
    	double cumulFitness = 0;
    	for (Individual indv : pop.getIndividuals()){
    		 cumulFitness += indv.getFitness();
    		 
    		 if( cumulFitness >= randomValue) {
    		 	return indv;
    		 }
    	}
    	
    	// sécurité en cas d'arrondi 
    	return pop.getIndividual(pop.getIndividuals().size() -1);
    }

}
