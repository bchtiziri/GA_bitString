package com.baeldung.algorithms.ga.binary;

public class Individual {

    private byte[] genes;
    private int fitness = -1;

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

    public int getFitness() {
        if (fitness == -1) {
            fitness = SimpleGeneticAlgorithm.getFitness(this);
        }
        return fitness;
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
