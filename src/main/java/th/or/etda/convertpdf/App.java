package th.or.etda.convertpdf;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	/**** Sample Input ****/
//		String inputFilePath = "src/main/resources/eReceipt.pdf";
//		String embbedFilePath = "src/main/resources/ContentInformation.xml";
//		String colorProfilePath = "src/main/resources/sRGB Color Space Profile.icm";
//		String outputFilePath = "target/success_eReceipt.pdf";
//		String keywords = "";
//		String title = "ใบเสร็จรับเงิน";
//		String description = "ใบเสร็จรับเงิน ภาครัฐ";
//		String creator = "สำนักงานพัฒนาธุรกรรมทางอิเล็กทรอนิกส์";
//		String docOID = "x.x.x.x.x";
//		String docRefId = "RCT-001";
//		String docVer = "1.0";
//		String docName = "ContentInformation.xml";
		
		String inputFilePath = args[0];
		String embbedFilePath = args[1];
		String colorProfilePath = args[2];
		String outputFilePath = args[3];
		String keywords = args[4];
		String title = args[5];
		String description = args[6];
		String creator = args[7];
		String docOID = args[8];
		String docRefId = args[9];
		String docVer = args[10];
		String docName = args[11];
		
		EmbedXMLDATA embdata = new EmbedXMLDATA(keywords, title, description, creator, docOID, docRefId, docVer, docName);
		PDFA3Components pdfa3Components = new PDFA3Components(inputFilePath, embbedFilePath, colorProfilePath,
				outputFilePath);
		ConvertPDFtoA3.Convert(pdfa3Components, embdata);
    }
}
