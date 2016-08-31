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
	
}
