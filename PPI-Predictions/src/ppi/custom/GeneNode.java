package ppi.custom;

public class GeneNode {
	private String geneName;
	
	public GeneNode(String geneName) {
		super();
		this.geneName = geneName;
	}

	@Override
	public String toString() {
		return geneName;
	}
}
