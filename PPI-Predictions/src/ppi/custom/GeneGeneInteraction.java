package ppi.custom;

public class GeneGeneInteraction {
	private String sourceGene;
	private String targetGene;
	
	public GeneGeneInteraction(String sourceGene, String targetGene) {
		super();
		this.sourceGene = sourceGene;
		this.targetGene = targetGene;
	}
	
	@Override
	public String toString() {
		return "GeneGeneInteraction [sourceGene=" + sourceGene + ", targetGene=" + targetGene + "]";
	}
	
	public String getSourceGene() {
		return sourceGene;
	}
	
	public String getTargetGene() {
		return targetGene;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceGene == null) ? 0 : sourceGene.hashCode());
		result = prime * result + ((targetGene == null) ? 0 : targetGene.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		GeneGeneInteraction other = (GeneGeneInteraction) obj;
		if (sourceGene == null) {
			if (other.sourceGene != null)
				return false;
		} else if (targetGene == null) {
			if (other.targetGene != null)
				return false;
		}
		
		if (sourceGene.equals(other.sourceGene) && targetGene.equals(other.targetGene))
			return true;
		else if (sourceGene.equals(other.targetGene) && targetGene.equals(other.sourceGene))
			return true;
		return false;
	}
}
