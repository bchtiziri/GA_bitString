
public class ConnectionGene {

	private int inNode;
	private int outNode;
	private double weight;
	private boolean enabled;
	private int innovation;

	public ConnectionGene(
			int inNode,
			int outNode,
			double weight,
			boolean enabled,
			int innovation) {

		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.enabled = enabled;
		this.innovation = innovation;
	}

	public int getInNode() {
		return inNode;
	}

	public int getOutNode() {
		return outNode;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInnovation() {
		return innovation;
	}
}

