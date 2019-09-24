package th.or.etda.convertpdf;

public class EmbedXMLDATA {
	private String keywords = "";
	private String title = "";
	private String description = "";
	private String creator = "";
	private String docOID = "";
	private String docRefId = "";
	private String docVer = "";
	
	public EmbedXMLDATA(String keywords, String title, String description, String creator, String docOID,
			String docRefId, String docVer, String docName) {
		super();
		this.keywords = keywords;
		this.title = title;
		this.description = description;
		this.creator = creator;
		this.docOID = docOID;
		this.docRefId = docRefId;
		this.docVer = docVer;
		this.docName = docName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getDocOID() {
		return docOID;
	}
	public void setDocOID(String docOID) {
		this.docOID = docOID;
	}
	public String getDocRefId() {
		return docRefId;
	}
	public void setDocRefId(String docRefId) {
		this.docRefId = docRefId;
	}
	public String getDocVer() {
		return docVer;
	}
	public void setDocVer(String docVer) {
		this.docVer = docVer;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	private String docName = "";
}
