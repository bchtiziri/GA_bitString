
public class NodeGene {

	public enum Type {
		INPUT,
		HIDDEN,
		OUTPUT,
		BIAS
	}

	private int id;
	private Type type;

	public NodeGene(int id, Type type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}
}

