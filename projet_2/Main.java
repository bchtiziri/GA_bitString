
public class Main {

    public static void main(String[] args) {

        SimpleGeneticAlgorithm ga =
                new SimpleGeneticAlgorithm();

        int populationSize = 50;
        int maxGeneration = 10000;

        String selectionMethod = "roulette";
        double crossoverRate = 0.7;

        ga.runAlgorithm(
                populationSize,
                maxGeneration,
                selectionMethod,
                crossoverRate
        );
    }
}

