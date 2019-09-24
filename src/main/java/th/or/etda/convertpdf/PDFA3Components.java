package th.or.etda.convertpdf;

public class PDFA3Components {
	private String inputFilePath;
	private String embedFilePath;
	private String colorProfilePath;
	private String outputFilePath;
	
	/**
	 * 	 * Use for set file path (input and output)
	 * 
	 * @param inputFilePath : path of input PDF file, e.g. Test.pdf, invoice.pdf
	 * @param embedFilePath : path of attached file, e.g. example.xml
	 * @param colorProfilePath : path of color profile file, e.g. sRGB Color Space Profile.icm
	 * @param outputFilePath : name of output PDF/A-3 file, e.g. invoiceA3.pdf
	 */
	public PDFA3Components(String inputFilePath, String embedFilePath, String colorProfilePath, String outputFilePath) {
		super();
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
		this.embedFilePath = embedFilePath;
		this.colorProfilePath = colorProfilePath;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getEmbedFilePath() {
		return embedFilePath;
	}

	public void setEmbedFilePath(String embedFilePath) {
		this.embedFilePath = embedFilePath;
	}

	public String getColorProfilePath() {
		return colorProfilePath;
	}

	public void setColorProfilePath(String colorProfilePath) {
		this.colorProfilePath = colorProfilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
}
