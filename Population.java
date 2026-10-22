package com.baeldung.algorithms.ga.binary;

import java.util.ArrayList;
import java.util.List;


public class Population {

    private List<Individual> individuals;

    public Population(int size, boolean createNew) {
        individuals = new ArrayList<>();
        if (createNew) {
            createNewPopulation(size);
        }
    }

    protected Individual getIndividual(int index) {
        return individuals.get(index);
    }

    protected Individual getFittest() {
        Individual fittest = individuals.get(0);
        for (int i = 0; i < individuals.size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }
    
    //Added
    protected List<Individual> getIndividuals() {
	    return individuals;
	}
	
    // Create apopulation 
    private void createNewPopulation(int size) {
    int minLength = 8;
    int maxLength = 128;

    for (int i = 0; i < size; i++) {

        int randomLength = minLength
                + (int) (Math.random() * (maxLength - minLength + 1));

        Individual newIndividual = new Individual(randomLength);

        individuals.add(newIndividual);
    }
}
}
