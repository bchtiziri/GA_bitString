package com.baeldung.algorithms.ga.binary;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private final List<Individual> individuals;

    public Population(int size, boolean createNew) {
        individuals = new ArrayList<>(size);
        if (createNew) {
            for (int i = 0; i < size; i++) {
                individuals.add(new Individual());
            }
        }
    }

    protected Individual getIndividual(int index) {
        return individuals.get(index);
    }

    protected List<Individual> getIndividuals() {
        return individuals;
    }

    protected Individual getFittest() {
        Individual fittest = individuals.get(0);
        for (Individual indiv : individuals) {
            if (indiv.getFitness() > fittest.getFitness()) {
                fittest = indiv;
            }
        }
        return fittest;
    }
}
