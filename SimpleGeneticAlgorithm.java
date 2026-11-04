package com.baeldung.algorithms.ga.binary;

import java.util.ArrayList;
import java.util.List;

public class SimpleGeneticAlgorithm {

    // ---- Méthodes au choix (enum plutôt que String) ----
    public enum SelectionMethod { ROULETTE, TOURNAMENT }

    public enum KeepMethod { BEST_FIT, ROULETTE, TOURNAMENT }

    public enum ExtraBitsRule { ALL_OR_NOTHING, BIT_BY_BIT, FITTEST_PARENT }

    // ---- Paramètres (plus codés en dur : modifiables par les setters) ----
    private double uniformRate = 0.5;        // proba de prendre le gène chez le parent 1 (crossover uniforme)
    private double crossoverRate = 0.9;      // proba qu'un nouvel individu soit issu d'un croisement
    private double flipRate = 0.0125;        // proba d'inverser chaque bit
    private double addRate = 0.05;           // proba d'ajouter un bit (par individu)
    private double removeRate = 0.05;        // proba de supprimer un bit (par individu)
    private int tournamentSize = 5;
    private boolean elitism = true;
    private SelectionMethod selectionMethod = SelectionMethod.TOURNAMENT;
    private KeepMethod keepMethod = KeepMethod.TOURNAMENT;
    private ExtraBitsRule extraBitsRule = ExtraBitsRule.BIT_BY_BIT;
    private boolean verbose = true;

    private static byte[] solution;

    // ---- Résultats de la dernière exécution (pour les tests) ----
    private int lastGenerationCount;
    private double lastBestFitness;
    private int lastBestLength;

    public boolean runAlgorithm(int populationSize, String solution, int maxGeneration) {
        setSolution(solution);
        Population myPop = new Population(populationSize, true);

        int generationCount = 1;
        while (myPop.getFittest().getFitness() < getMaxFitness() && generationCount < maxGeneration) {
            if (verbose) {
                Individual best = myPop.getFittest();
                System.out.println("Generation: " + generationCount
                        + " Best fitness: " + best.getFitness()
                        + " Length: " + best.length());
            }
            myPop = evolvePopulation(myPop);
            generationCount++;
        }

        Individual best = myPop.getFittest();
        lastGenerationCount = generationCount;
        lastBestFitness = best.getFitness();
        lastBestLength = best.length();

        boolean found = best.getFitness() == getMaxFitness();
        if (verbose) {
            if (found) {
                System.out.println("Solution found!");
                System.out.println("Generation: " + generationCount);
                System.out.println("Genes: ");
                System.out.println(best);
            } else {
                System.out.println("Maximum number of generation reached.");
                System.out.println("Best fitness: " + best.getFitness() + " (length " + best.length() + ")");
            }
        }
        return found;
    }

    public Population evolvePopulation(Population pop) {
        int size = pop.getIndividuals().size();
        Population newPopulation = new Population(size, false);

        int elitismOffset = 0;
        if (elitism) {
            newPopulation.getIndividuals().add(new Individual(pop.getFittest()));
            elitismOffset = 1;
        }

        // Population triée par fitness décroissante (pour BEST_FIT)
        List<Individual> sorted = new ArrayList<>(pop.getIndividuals());
        sorted.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        int keptIndex = elitismOffset; // sorted[0] = élite, déjà ajoutée

        for (int i = elitismOffset; i < size; i++) {
            if (Math.random() < crossoverRate) {
                // Nouvel enfant : 2 parents différents, croisement puis mutation
                Individual parent1 = select(pop);
                Individual parent2 = selectDifferent(pop, parent1);
                Individual child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.getIndividuals().add(child);
            } else {
                // Individu conservé de l'ancienne génération (copie, sans mutation)
                Individual kept;
                switch (keepMethod) {
                    case BEST_FIT:
                        kept = sorted.get(keptIndex % size);
                        keptIndex++;
                        break;
                    case ROULETTE:
                        kept = rouletteSelection(pop);
                        break;
                    default:
                        kept = tournamentSelection(pop);
                }
                newPopulation.getIndividuals().add(new Individual(kept));
            }
        }
        return newPopulation;
    }

    // ---- Sélection ----
    private Individual select(Population pop) {
        return selectionMethod == SelectionMethod.ROULETTE
                ? rouletteSelection(pop)
                : tournamentSelection(pop);
    }

    // Évite de choisir deux fois le même parent (sinon l'enfant est une simple copie)
    private Individual selectDifferent(Population pop, Individual other) {
        Individual candidate = select(pop);
        int tries = 0;
        while (candidate == other && tries < 10) {
            candidate = select(pop);
            tries++;
        }
        return candidate;
    }

    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            tournament.getIndividuals().add(pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    // Roulette avec décalage : poids = fitness - fitnessMin
    // -> gère les fitness négatives et augmente la pression de sélection
    private Individual rouletteSelection(Population pop) {
        double minFitness = Double.MAX_VALUE;
        for (Individual indiv : pop.getIndividuals()) {
            minFitness = Math.min(minFitness, indiv.getFitness());
        }

        double totalWeight = 0;
        for (Individual indiv : pop.getIndividuals()) {
            totalWeight += indiv.getFitness() - minFitness;
        }

        // Tous les individus ont la même fitness : tirage uniforme
        if (totalWeight == 0) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            return pop.getIndividual(randomId);
        }

        double randomValue = Math.random() * totalWeight;
        double cumul = 0;
        for (Individual indiv : pop.getIndividuals()) {
            cumul += indiv.getFitness() - minFitness;
            if (cumul >= randomValue) {
                return indiv;
            }
        }
        return pop.getIndividual(pop.getIndividuals().size() - 1); // sécurité arrondi
    }

    // ---- Crossover (longueurs différentes) ----
    private Individual crossover(Individual indiv1, Individual indiv2) {
        int common = Math.min(indiv1.length(), indiv2.length());
        Individual longer = indiv1.length() >= indiv2.length() ? indiv1 : indiv2;
        Individual child = Individual.empty(longer.length());

        // Partie commune : crossover uniforme
        for (int i = 0; i < common; i++) {
            child.addGene(Math.random() <= uniformRate ? indiv1.getSingleGene(i) : indiv2.getSingleGene(i));
        }

        // Bits en trop (présents seulement chez le parent le plus long)
        if (longer.length() > common) {
            switch (extraBitsRule) {
                case ALL_OR_NOTHING:
                    if (Math.random() < 0.5) {
                        copyExtraBits(longer, common, child);
                    }
                    break;
                case BIT_BY_BIT:
                    for (int i = common; i < longer.length(); i++) {
                        if (Math.random() < 0.5) {
                            child.addGene(longer.getSingleGene(i));
                        }
                    }
                    break;
                case FITTEST_PARENT:
                    Individual shorter = (longer == indiv1) ? indiv2 : indiv1;
                    if (longer.getFitness() > shorter.getFitness()) {
                        copyExtraBits(longer, common, child);
                    }
                    break;
            }
        }
        return child;
    }

    private void copyExtraBits(Individual from, int start, Individual to) {
        for (int i = start; i < from.length(); i++) {
            to.addGene(from.getSingleGene(i));
        }
    }

    // ---- Mutation : flip, ajout, suppression (chacune avec son taux) ----
    private void mutate(Individual indiv) {
        // 1. Flip : inverse le bit (corrige l'ancien tirage aléatoire)
        for (int i = 0; i < indiv.length(); i++) {
            if (Math.random() < flipRate) {
                indiv.setSingleGene(i, (byte) (1 - indiv.getSingleGene(i)));
            }
        }
        // 2. Ajout d'un bit aléatoire à une position aléatoire
        if (Math.random() < addRate) {
            int pos = (int) (Math.random() * (indiv.length() + 1));
            indiv.insertGene(pos, Individual.randomBit());
        }
        // 3. Suppression d'un bit à une position aléatoire (on garde au moins 1 bit)
        if (indiv.length() > 1 && Math.random() < removeRate) {
            int pos = (int) (Math.random() * indiv.length());
            indiv.removeGene(pos);
        }
    }

    // ---- Fitness : bits corrects - 1/2 * bits en trop ----
    protected static double getFitness(Individual individual) {
        int correct = 0;
        int common = Math.min(individual.length(), solution.length);
        for (int i = 0; i < common; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                correct++;
            }
        }
        int extra = Math.max(0, individual.length() - solution.length);
        return correct - 0.5 * extra;
    }

    protected double getMaxFitness() {
        return solution.length;
    }

    protected void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            solution[i] = (byte) (newSolution.charAt(i) == '1' ? 1 : 0);
        }
    }

    // ---- Setters (paramétrage) ----
    public void setUniformRate(double v) { uniformRate = v; }
    public void setCrossoverRate(double v) { crossoverRate = v; }
    public void setFlipRate(double v) { flipRate = v; }
    public void setAddRate(double v) { addRate = v; }
    public void setRemoveRate(double v) { removeRate = v; }
    public void setTournamentSize(int v) { tournamentSize = v; }
    public void setElitism(boolean v) { elitism = v; }
    public void setSelectionMethod(SelectionMethod v) { selectionMethod = v; }
    public void setKeepMethod(KeepMethod v) { keepMethod = v; }
    public void setExtraBitsRule(ExtraBitsRule v) { extraBitsRule = v; }
    public void setVerbose(boolean v) { verbose = v; }
    public void setInitialLengthRange(int min, int max) { Individual.setInitialLengthRange(min, max); }

    // ---- Résultats ----
    public int getLastGenerationCount() { return lastGenerationCount; }
    public double getLastBestFitness() { return lastBestFitness; }
    public int getLastBestLength() { return lastBestLength; }
}
