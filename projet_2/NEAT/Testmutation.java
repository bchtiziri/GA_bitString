
public class Testmutation {

    public static void main(String[] args) {

        // 1. Créer le gestionnaire des innovations
        InnovationTracker tracker = new InnovationTracker();

        // 2. Créer une population de 5 génomes
        Population population = new Population(5, tracker);

        // 3. Tester les prédictions et les fitness
        System.out.println("===== POPULATION INITIALE =====");

        int i = 1;

        for (Genome genome : population.getGenomes()) {

            genome.evaluateFitness();

            System.out.println("\nGenome " + i);

            System.out.println(
                "Nombre de neurones : " + genome.getNodes().size()
            );

            System.out.println(
                "Nombre de connexions : " + genome.getConnections().size()
            );

            System.out.println(
                "Prediction XOR (1,0) : " + genome.predict(1, 0)
            );

            System.out.println(
                "Fitness : " + genome.getFitness()
            );

            i++;
        }

        // 4. Choisir le premier génome pour tester les mutations
        Genome genome = population.getGenomes().get(0);

        System.out.println("\n===== AVANT MUTATIONS =====");
        afficherGenome(genome);

        // 5. Mutation des poids
        genome.mutateWeights();

        System.out.println("\n===== APRES MUTATION DES POIDS =====");
        afficherGenome(genome);

        // 6. Ajouter un neurone caché
        genome.mutateAddNode(tracker);

        System.out.println("\n===== APRES AJOUT D'UN NEURONE =====");
        afficherGenome(genome);

        // 7. Ajouter une connexion
        genome.mutateAddConnection(tracker);

        System.out.println("\n===== APRES AJOUT D'UNE CONNEXION =====");
        afficherGenome(genome);

        // 8. Activer ou désactiver une connexion
        genome.mutateToggleConnection();

        System.out.println("\n===== APRES TOGGLE D'UNE CONNEXION =====");
        afficherGenome(genome);

        // 9. Recalculer la fitness après les mutations
        genome.evaluateFitness();

        System.out.println("\n===== RESULTAT FINAL =====");

        System.out.println("Fitness : " + genome.getFitness());

        System.out.println("0 XOR 0 : " + genome.predict(0, 0));
        System.out.println("0 XOR 1 : " + genome.predict(0, 1));
        System.out.println("1 XOR 0 : " + genome.predict(1, 0));
        System.out.println("1 XOR 1 : " + genome.predict(1, 1));
    }

    // Afficher les informations d'un génome
    public static void afficherGenome(Genome genome) {

        System.out.println(
            "Nombre de neurones : " + genome.getNodes().size()
        );

        System.out.println(
            "Nombre de connexions : " + genome.getConnections().size()
        );

        for (ConnectionGene connection : genome.getConnections()) {

            System.out.println(
                connection.getInNode() + " -> "
                + connection.getOutNode()
                + " | poids : " + connection.getWeight()
                + " | active : " + connection.isEnabled()
                + " | innovation : " + connection.getInnovation()
            );
        }
    }
}

