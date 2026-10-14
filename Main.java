package com.baeldung.algorithms.ga.binary;

public class Main {

    public static void main(String[] args) {
	
	int populationSize = 20;
	int maxGeneration = 1000;
	String selectionMethod = "roulette";
    double crossoverProbability = 0.7;

        SimpleGeneticAlgorithm ga =
                new SimpleGeneticAlgorithm();

        String solution =
                "1011001110001111000011110000111100001111000011110000111100001111";

        ga.runAlgorithm( populationSize, solution, maxGeneration, selectionMethod, crossoverProbability);
    }
}
