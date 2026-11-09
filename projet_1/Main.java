

public class Main {

    public static void main(String[] args) {
	
	int populationSize = 50;
	int maxGeneration = 100;
	String selectionMethod = "roulette"; //tournament ou roulette
    double crossoverProbability = 0.7;


        SimpleGeneticAlgorithm ga =
                new SimpleGeneticAlgorithm();

        String solution = "101100111011001110110011";
                

        ga.runAlgorithm( populationSize, solution, maxGeneration, selectionMethod, crossoverProbability);
    }
}
