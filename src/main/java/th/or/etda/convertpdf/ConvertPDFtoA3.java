package th.or.etda.convertpdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.XmpConstants;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.DefinedStructuredType;
import org.apache.xmpbox.type.PDFAPropertyType;
import org.apache.xmpbox.type.PDFASchemaType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.VeraPDFFoundry;
import org.verapdf.pdfa.results.ValidationResult;

/**
 * The ConvertPDFtoPDFA3 method is used to convert any kind of PDF(.pdf) to
 * PDF/A-3
 * 
 * @author ETDA
 * 
 */
public class ConvertPDFtoA3 {

	private static float pdfVer = 1.7f;
	public final static String xmlns_pdfaSchema = "http://www.aiim.org/pdfa/ns/schema#";
	public final static String prefix_pdfaSchema = "pdfaSchema";
	public final static String xmlns_pdfaProperty = "http://www.aiim.org/pdfa/ns/property#";
	public final static String prefix_pdfaProperty = "pdfaProperty";
	public static String namespace = "urn:etda:pdfa:ElectronicDocument:1p0:standard#";
	public static String prefix = "ed";

	/**
	 * The convertPDFtoPDFA3(FilePaths, String, String, String) method is used to
	 * convert any kind of PDF(.pdf) to PDF/A-3
	 * 
	 * @param pdfa3Components
	 * @param embdata 
	 * @throws Exception
	 */
	public static void Convert(PDFA3Components pdfa3Components, EmbedXMLDATA embdata) throws Exception {
		File inputFile = new File(pdfa3Components.getInputFilePath());
		PDDocument doc = PDDocument.load(inputFile);
		File colorPFile = new File(pdfa3Components.getColorProfilePath());
		InputStream colorProfile = new FileInputStream(colorPFile);

		PDDocumentCatalog cat = makeA3compliant(doc, pdfa3Components, embdata);
		attachFile(doc, pdfa3Components.getEmbedFilePath());
		addOutputIntent(doc, cat, colorProfile);
		doc.setVersion(pdfVer);
		doc.save(pdfa3Components.getOutputFilePath());
		doc.close();
		File outputFile = new File(pdfa3Components.getOutputFilePath());

		if (outputFile.exists()) {

			VeraGreenfieldFoundryProvider.initialise();
			VeraPDFFoundry instance = Foundries.defaultInstance();

			PDFAParser parser = instance.createParser(new FileInputStream(pdfa3Components.getOutputFilePath()));
			PDFAValidator validator = instance.createValidator(parser.getFlavour(), false);
			ValidationResult result = validator.validate(parser);

			if (!result.isCompliant()) {
				System.out.println("Please check input file.");
			} else {
				System.out.println(pdfa3Components.getOutputFilePath());
			}
		} else {
			System.out.println("Failed to convert.");
		}
	}

	private static void addOutputIntent(PDDocument doc, PDDocumentCatalog cat, InputStream colorProfile)
			throws IOException {
		if (cat.getOutputIntents().isEmpty()) {
			PDOutputIntent oi = new PDOutputIntent(doc, colorProfile);
			oi.setInfo("sRGB IEC61966-2.1");
			oi.setOutputCondition("sRGB IEC61966-2.1");
			oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
			oi.setRegistryName("http://www.color.org");
			cat.addOutputIntent(oi);
		}

	}

	private static void attachFile(PDDocument doc, String embedFilePath) throws IOException {
		PDEmbeddedFilesNameTreeNode efTree = new PDEmbeddedFilesNameTreeNode();

		File embedFile = new File(embedFilePath);

		String subType = Files.probeContentType(FileSystems.getDefault().getPath(embedFilePath));
		String embedFileName = FilenameUtils.getName(embedFilePath);
		// first create the file specification, which holds the embedded file

		PDComplexFileSpecification fs = new PDComplexFileSpecification();
		fs.setFile(embedFileName);
		COSDictionary dict = fs.getCOSObject();
		// Relation "Source" for linking with eg. catalog
		dict.setName("AFRelationship", "Alternative");

		dict.setString("UF", embedFileName);

		InputStream is = new FileInputStream(embedFile);

		PDEmbeddedFile ef = new PDEmbeddedFile(doc, is);

		// set some of the attributes of the embedded file
		ef.setModDate(GregorianCalendar.getInstance());
		ef.setSize((int) embedFile.length());
		ef.setSubtype(subType);
		fs.setEmbeddedFile(ef);

		// now add the entry to the embedded file tree and set in the document.
		efTree.setNames(Collections.singletonMap(embedFileName, fs));

		// attachments are stored as part of the "names" dictionary in the
		PDDocumentCatalog catalog = doc.getDocumentCatalog();

		PDDocumentNameDictionary names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
		names.setEmbeddedFiles(efTree);
		catalog.setNames(names);

		COSDictionary dict2 = catalog.getCOSObject();
		COSArray array = new COSArray();
		array.add(fs.getCOSObject());
		dict2.setItem("AF", array);
	}

	private static PDDocumentCatalog makeA3compliant(PDDocument doc, PDFA3Components pdfa3Components, EmbedXMLDATA embdata) throws Exception {
		PDDocumentCatalog cat = doc.getDocumentCatalog();
		PDDocumentInformation pdd = doc.getDocumentInformation();
		PDMetadata meta = cat.getMetadata();

		PDDocumentInformation pdi = new PDDocumentInformation();
		pdi.setProducer(pdd.getProducer());
		pdi.setAuthor(pdd.getAuthor());
		pdi.setTitle(pdd.getTitle());
		pdi.setSubject(pdd.getSubject());
		pdi.setCreationDate(pdd.getCreationDate());
		doc.setDocumentInformation(pdi);

		String keywords = embdata.getKeywords();
		String title = embdata.getTitle();
		String description = embdata.getDescription();
		String creator = embdata.getCreator();
		String docOID = embdata.getDocOID();
		String docRefId = embdata.getDocRefId();
		String docVer = embdata.getDocVer();
		String docName = embdata.getDocName();

		DomXmpParser xmpParser = new DomXmpParser();
		try {
			XMPMetadata metadata = xmpParser.parse(meta.createInputStream());
			PDFAIdentificationSchema pdfaid = metadata.createAndAddPFAIdentificationSchema();
			pdfaid.setConformance("U");
			pdfaid.setPart(3);

			metadata.removeSchema(metadata.getDublinCoreSchema());

			DublinCoreSchema dc = metadata.createAndAddDublinCoreSchema();
			dc.addTitle("x-default", title);
			dc.addDescription("x-default", description);
			dc.addCreator(creator);

			AdobePDFSchema pdf = metadata.getAdobePDFSchema();
			pdf.setKeywords(keywords);

			XMPBasicSchema basic = metadata.getXMPBasicSchema();
			basic.setModifyDate(GregorianCalendar.getInstance());

			XMPSchema edSchema = metadata.createAndAddDefaultSchema(prefix, namespace);
			edSchema.setAboutAsSimple("");
			edSchema.setTextPropertyValue("DocumentOID", docOID);
			edSchema.setTextPropertyValue("DocumentReferenceID", docRefId);
			edSchema.setTextPropertyValue("DocumentVersion", docVer);
			edSchema.setTextPropertyValue("DocumentFileName", docName);

			attachExtensions(metadata, true);

			XmpSerializer serializer = new XmpSerializer();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			serializer.serialize(metadata, out, false);

			meta.importXMPMetadata(out.toByteArray());

		} catch (XmpParsingException e) {
			System.err.println("An error ouccred when parsing the meta data: " + e.getMessage());
		}

		cat.setMetadata(meta);

		return cat;
	}

	private static void attachExtensions(XMPMetadata metadata, boolean withZF) {

		PDFAExtensionSchema pdfaSchema = metadata.createAndAddPDFAExtensionSchemaWithDefaultNS();
		pdfaSchema.addNamespace(xmlns_pdfaSchema, prefix_pdfaSchema);
		pdfaSchema.addNamespace(xmlns_pdfaProperty, prefix_pdfaProperty);

		ArrayProperty newBag = pdfaSchema.createArrayProperty("schemas", Cardinality.Bag);
		DefinedStructuredType li = new DefinedStructuredType(metadata, pdfaSchema.getNamespace(),
				pdfaSchema.getPrefix(), XmpConstants.LIST_NAME);
		li.setAttribute(new Attribute(pdfaSchema.getNamespace(), XmpConstants.PARSE_TYPE, XmpConstants.RESOURCE_NAME));
		newBag.addProperty(li);
		pdfaSchema.addProperty(newBag);

		TextType pdfa1 = new TextType(metadata, xmlns_pdfaSchema, prefix_pdfaSchema, PDFASchemaType.SCHEMA,
				"Electronic Document PDFA Extension Schema");
		li.addProperty(pdfa1);

		pdfa1 = new TextType(metadata, xmlns_pdfaSchema, prefix_pdfaSchema, PDFASchemaType.NAMESPACE_URI, namespace);
		li.addProperty(pdfa1);

		pdfa1 = new TextType(metadata, xmlns_pdfaSchema, prefix_pdfaSchema, PDFASchemaType.PREFIX, prefix);
		li.addProperty(pdfa1);

		if (withZF) {
			ArrayProperty newSeq = new ArrayProperty(metadata, xmlns_pdfaSchema, prefix_pdfaSchema,
					PDFASchemaType.PROPERTY, Cardinality.Seq);
			li.addProperty(newSeq);
			addProperty(metadata, newSeq, "DocumentOID", "Text", "external",
					"OID of Exchange Document Associated File");
			addProperty(metadata, newSeq, "DocumentReferenceID", "Text", "external",
					"Document Identification from document's author");
			addProperty(metadata, newSeq, "DocumentVersion", "Text", "external", "Version of document");
			addProperty(metadata, newSeq, "DocumentFileName", "Text", "external",
					"File name of Exchange Document Associated File");
		}
	}

	private static DefinedStructuredType addProperty(XMPMetadata metadata, ArrayProperty parent, String name,
			String type, String category, String description) {

		DefinedStructuredType li = new DefinedStructuredType(metadata, parent.getNamespace(), parent.getPrefix(),
				XmpConstants.LIST_NAME);
		li.setAttribute(new Attribute(parent.getNamespace(), XmpConstants.PARSE_TYPE, XmpConstants.RESOURCE_NAME));

		ChoiceType pdfa2 = new ChoiceType(metadata, xmlns_pdfaProperty, prefix_pdfaProperty, PDFAPropertyType.NAME,
				name);
		li.addProperty(pdfa2);

		pdfa2 = new ChoiceType(metadata, xmlns_pdfaProperty, prefix_pdfaProperty, PDFAPropertyType.VALUETYPE, type);
		li.addProperty(pdfa2);

		pdfa2 = new ChoiceType(metadata, xmlns_pdfaProperty, prefix_pdfaProperty, PDFAPropertyType.CATEGORY, category);
		li.addProperty(pdfa2);

		pdfa2 = new ChoiceType(metadata, xmlns_pdfaProperty, prefix_pdfaProperty, PDFAPropertyType.DESCRIPTION,
				description);
		li.addProperty(pdfa2);

		parent.addProperty(li);

		return li;
	}
}
