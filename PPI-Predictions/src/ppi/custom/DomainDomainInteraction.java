package ppi.custom;

public class DomainDomainInteraction {
	private String sourceDomain;
	private String targetDomain;
	private String occurences;
	
	public DomainDomainInteraction(String sourceDomain, String targetDomain, String occurences) {
		super();
		this.sourceDomain = sourceDomain;
		this.targetDomain = targetDomain;
		this.occurences = occurences;
	}
	
	@Override
	public String toString() {
		return "DomainDomainInteraction [sourceDomain=" + sourceDomain + ", targetDomain=" + targetDomain
				+ ", occurences=" + occurences + "]";
	}

	public String getSourceDomain() {
		return sourceDomain;
	}

	public String getTargetDomain() {
		return targetDomain;
	}

	public String getOccurences() {
		return occurences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (sourceDomain.compareTo(targetDomain) < 0) {
			result = prime * result + ((sourceDomain == null) ? 0 : sourceDomain.hashCode());
			result = prime * result + ((targetDomain == null) ? 0 : targetDomain.hashCode());
		} else {
			result = prime * result + ((targetDomain == null) ? 0 : targetDomain.hashCode());
			result = prime * result + ((sourceDomain == null) ? 0 : sourceDomain.hashCode());
		}
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
		
		DomainDomainInteraction other = (DomainDomainInteraction) obj;
		if (sourceDomain == null) {
			if (other.sourceDomain != null)
				return false;
		} else if (targetDomain == null) {
			if (other.targetDomain != null)
				return false;
		}
		
		if (sourceDomain.equals(other.sourceDomain) && targetDomain.equals(other.targetDomain))
			return true;
		else if (sourceDomain.equals(other.targetDomain) && targetDomain.equals(other.sourceDomain))
			return true;
		return false;
	}
}
