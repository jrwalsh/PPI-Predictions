package ppi.custom;

/**
 * Store all information output by the domain prediction program pfam_scan.
 * 
 * @author jesse
 *
 */
public class PredictedDomain {
	private String domainName;
	private String proteinName;
	private String geneName;
	private String type;
	private String bitScore;
	private String eValue;
	private String start;
	private String end;
	
	public PredictedDomain(String domainName, String proteinName, String geneName, String type, String bitScore, String eValue, String start, String end) {
		super();
		this.domainName = domainName;
		this.proteinName = proteinName;
		this.geneName = geneName;
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

	public String getDomainName() {
		return domainName;
	}

	public String getProteinName() {
		return proteinName;
	}

	public String getGeneName() {
		return geneName;
	}

	public String getType() {
		return type;
	}

	public String getBitScore() {
		return bitScore;
	}

	public String geteValue() {
		return eValue;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}
}
