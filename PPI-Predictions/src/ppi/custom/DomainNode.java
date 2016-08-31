package ppi.custom;

public class DomainNode {
	private String domainName;
	private String proteinName;
	private String type;
	private String bitScore;
	private String eValue;
	private String start;
	private String end;
	
	public DomainNode(String domainName, String proteinName, String type, String bitScore, String eValue, String start, String end) {
		super();
		this.domainName = domainName;
		this.proteinName = proteinName;
		this.type = type;
		this.bitScore = bitScore;
		this.eValue = eValue;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return "DomainNode [domainName=" + domainName + ", proteinName=" + proteinName + ", type=" + type
				+ ", bitScore=" + bitScore + ", eValue=" + eValue + ", start=" + start + ", end=" + end + "]";
	}
}
