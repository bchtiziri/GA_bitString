package com.baeldung.algorithms.ga.binary;

import com.baeldung.algorithms.ga.binary.SimpleGeneticAlgorithm.ExtraBitsRule;
import com.baeldung.algorithms.ga.binary.SimpleGeneticAlgorithm.KeepMethod;
import com.baeldung.algorithms.ga.binary.SimpleGeneticAlgorithm.SelectionMethod;

public class Main {

    static final String SOLUTION =
            "1011001110001111000011110000111100001111000011110000111100001111";

    public static void main(String[] args) {
        int populationSize = 20;
        int maxGeneration = 1000;

        SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm();
        ga.setSelectionMethod(SelectionMethod.TOURNAMENT);   // ROULETTE ou TOURNAMENT
        ga.setKeepMethod(KeepMethod.BEST_FIT);               // BEST_FIT, ROULETTE ou TOURNAMENT
        ga.setCrossoverRate(0.9);
        ga.setExtraBitsRule(ExtraBitsRule.BIT_BY_BIT);       // ALL_OR_NOTHING, BIT_BY_BIT ou FITTEST_PARENT
        ga.setFlipRate(0.0125);
        ga.setAddRate(0.05);
        ga.setRemoveRate(0.05);
        ga.setInitialLengthRange(32, 96);                    // longueur des individus au départ

        ga.runAlgorithm(populationSize, SOLUTION, maxGeneration);

        // Pour remplir les tableaux du rapport (50 exécutions par configuration) :
        // Experiments.main(args);
    }
}
