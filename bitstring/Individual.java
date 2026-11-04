package com.baeldung.algorithms.ga.binary.bitstring;

import java.util.ArrayList;
import java.util.List;

public class Individual {

    // Longueur des individus créés aléatoirement (configurable)
    private static int minInitialLength = 32;
    private static int maxInitialLength = 96;

    // Gènes stockés dans une liste : la longueur peut changer (ajout / suppression de bits)
    private final List<Byte> genes;
    private double fitness;
    private boolean fitnessComputed = false;

    // Nouvel individu aléatoire, de longueur aléatoire entre min et max
    public Individual() {
        int length = minInitialLength
                + (int) (Math.random() * (maxInitialLength - minInitialLength + 1));
        genes = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            genes.add(randomBit());
        }
    }

    // Individu vide (utilisé par le crossover, qui ajoute les gènes un par un)
    private Individual(int capacity) {
        genes = new ArrayList<>(capacity);
    }

    protected static Individual empty(int capacity) {
        return new Individual(capacity);
    }

    // Constructeur de copie (copie profonde)
    public Individual(Individual other) {
        this.genes = new ArrayList<>(other.genes);
        this.fitness = other.fitness;
        this.fitnessComputed = other.fitnessComputed;
    }

    protected static void setInitialLengthRange(int min, int max) {
        if (min < 1 || max < min) {
            throw new IllegalArgumentException("Invalid length range: " + min + ".." + max);
        }
        minInitialLength = min;
        maxInitialLength = max;
    }

    protected static byte randomBit() {
        return (byte) (Math.random() < 0.5 ? 0 : 1);
    }

    protected int length() {
        return genes.size();
    }

    protected byte getSingleGene(int index) {
        return genes.get(index);
    }

    protected void setSingleGene(int index, byte value) {
        genes.set(index, value);
        fitnessComputed = false;
    }

    protected void addGene(byte value) {
        genes.add(value);
        fitnessComputed = false;
    }

    protected void insertGene(int index, byte value) {
        genes.add(index, value);
        fitnessComputed = false;
    }

    protected void removeGene(int index) {
        genes.remove(index);
        fitnessComputed = false;
    }

    public double getFitness() {
        if (!fitnessComputed) {
            fitness = SimpleGeneticAlgorithm.getFitness(this);
            fitnessComputed = true;
        }
        return fitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(genes.size());
        for (byte g : genes) {
            sb.append(g);
        }
        return sb.toString();
    }
}
