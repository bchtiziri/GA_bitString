package com.baeldung.algorithms.ga.binary;

public class Main {

    public static void main(String[] args) {
	
	int populationSize = 20;
	int maxGeneration = 1000;
	String selectionMethod = "roulette";
        double crossoverProbability = 0;


        SimpleGeneticAlgorithm ga =
                new SimpleGeneticAlgorithm();

        String solution =
                "10110011";

        ga.runAlgorithm( populationSize, solution, maxGeneration, selectionMethod, crossoverProbability);
    }
}
