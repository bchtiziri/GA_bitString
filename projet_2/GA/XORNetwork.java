
public class XORNetwork {

	// 6 poids + 3 biais
	private final double[] parameters;

	public XORNetwork(double[] parameters) {
		if (parameters.length != 9) {
			throw new IllegalArgumentException(
			"Le réseau doit avoir 9 paramètres."
			);
		}
		this.parameters = parameters.clone();
		}

	private double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-4.9 * x));
	}

	public double predict(double a, double b) {

		// Première couche : 2 neurones cachés
		double h1 = sigmoid(
			a * parameters[0] +
			b * parameters[2] +
			parameters[6]
		);

		double h2 = sigmoid(
			a * parameters[1] +
			b * parameters[3] +
			parameters[7]
		);

		// Deuxième couche : neurone de sortie
		double y = sigmoid(
			h1 * parameters[4] +
			h2 * parameters[5] +
			parameters[8]
		);

		return y;
	}
}

