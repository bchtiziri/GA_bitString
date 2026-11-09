import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Locale;
public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // 1. Paramètres de NEAT
        int populationSize = 150;
        int maxGenerations = 1000;

        // 2. Créer l'algorithme NEAT
        NEATAlgorithm neat = new NEATAlgorithm(
            populationSize,
            maxGenerations
        );

        // 3. Lancer l'évolution
        Genome best = neat.run();
        	//sauvegarde dans un fichier dot 
        sauvegarderReseauDOT(best);

        // 4. Afficher le meilleur génome
        System.out.println("\n===== MEILLEUR GENOME =====");

        System.out.println("Fitness : " + best.getFitness());

        System.out.println(
            "Accuracy : " + neat.getAccuracy(best) + "/4"
        );

        System.out.println(
            "Nombre de neurones : " + best.getNodes().size()
        );

        System.out.println(
            "Nombre de connexions : " + best.getConnections().size()
        );

        // 5. Tester les quatre entrées XOR
        System.out.println("\n===== TEST XOR =====");

        int[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };

        int[] expected = {0, 1, 1, 0};

        for (int i = 0; i < inputs.length; i++) {

            double output = best.predict(
                inputs[i][0],
                inputs[i][1]
            );

            int prediction = output >= 0.5 ? 1 : 0;

            System.out.println(
                inputs[i][0] + " XOR " + inputs[i][1]
                + " | Attendu : " + expected[i]
                + " | Sortie : " + output
                + " | Prediction : " + prediction
            );
        }

        // 6. Afficher les connexions du meilleur réseau
        System.out.println("\n===== CONNEXIONS =====");

        for (ConnectionGene connection : best.getConnections()) {

            System.out.println(
                connection.getInNode() + " -> "
                + connection.getOutNode()
                + " | Poids : " + connection.getWeight()
                + " | Active : " + connection.isEnabled()
                + " | Innovation : " + connection.getInnovation()
            );
        }
    }
    
    
	public static void sauvegarderReseauDOT(Genome genome)
		throws FileNotFoundException {

	    PrintWriter fichier = new PrintWriter("reseau.dot");

	    fichier.println("digraph NEAT {");
	    fichier.println("  rankdir=LR;");
	    fichier.println("  node [shape=circle];");

	    // Afficher les neurones
	    for (NodeGene node : genome.getNodes()) {

		String nom;

		if (node.getId() == 0) nom = "A";
		else if (node.getId() == 1) nom = "B";
		else if (node.getId() == 2) nom = "Y";
		else if (node.getId() == 3) nom = "BIAS";
		else nom = "H" + (node.getId() - 3);

		fichier.println(
		    "  " + node.getId()
		    + " [label=\"" + nom + "\"];"
		);
	    }

	    // Afficher uniquement les connexions actives
	    for (ConnectionGene connection : genome.getConnections()) {

		if (!connection.isEnabled()) {
		    continue;
		}

		fichier.printf(
		    Locale.US,
		    "  %d -> %d [label=\"%+.2f\"];%n",
		    connection.getInNode(),
		    connection.getOutNode(),
		    connection.getWeight()
		);
	    }

	    fichier.println("}");
	    fichier.close();

	    System.out.println("Reseau sauvegarde dans reseau.dot");
	}

}

