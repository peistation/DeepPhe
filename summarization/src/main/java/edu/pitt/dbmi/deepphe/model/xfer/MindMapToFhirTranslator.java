package edu.pitt.dbmi.deepphe.model.xfer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class MindMapToFhirTranslator {

	private final Namespace xsNameSpace = Namespace.getNamespace("xs",
			"http://www.w3.org/2001/XMLSchema");

	private String fhirBaseFileName;
	private String mmFileName;

	private File fhirBaseFile;
	private File mmFile;

	private final HashMap<String, Element> fhirVisited = new HashMap<String, Element>();
	private final HashMap<String, Element> mmVisited = new HashMap<String, Element>();
	private final Queue<Element> q = new LinkedList<Element>();

	public static void main(String[] args) {
		MindMapToFhirTranslator mmTranslater = new MindMapToFhirTranslator();
		mmTranslater.buildCli(args);
		mmTranslater.execute();
	}

	public MindMapToFhirTranslator() {
		;
	}

	private void execute() {
		File resourcesDirectory = new File("summarization\\src\\main\\resources\\xfer");
		System.out.println(resourcesDirectory.getAbsolutePath());
		fhirBaseFile = new File(resourcesDirectory, getFhirBaseFileName());
		mmFile = new File(resourcesDirectory, getMmFileName());
		Element fhirBaseRoot = parseFileToElementTree(fhirBaseFile);
		Element mmRoot = parseFileToElementTree(mmFile);
		fhirVisited.putAll(traverseAndCache(fhirBaseRoot));
//		displayFhirVisited();

		Element fhirTargetElement = createFhirTargetElement();
		fhirTargetElement = traverseMindMapTree(mmRoot, fhirTargetElement);
		writeFhirTarget(fhirTargetElement);
		
		System.out.println("Translation Complete with out error.");
	}

	private Element traverseMindMapTree(Element mmRoot,
			Element fhirTargetElement) {
		q.add(mmRoot);
		while (!q.isEmpty()) {
			Element mmElement = q.poll();
			processNode(mmElement, fhirTargetElement);
		}
		return fhirTargetElement;
	}

	private void processNode(Element mmElement, Element fhirTargetElement) {
		MindMapNodeText mmNode = getNodeText(mmElement);
		if (mmNode.name.length() > 0
				&& !mmVisited.keySet().contains(mmNode.name)
				&& !fhirVisited.keySet().contains(mmNode.name)) {
			Element fhirTargetComplexTypeChildElement = new Element(
					"complexType", xsNameSpace);
			fhirTargetComplexTypeChildElement.setAttribute("name",
					mmNode.typeName);
			mmVisited.put(mmNode.name, fhirTargetComplexTypeChildElement);
			fhirTargetElement.addContent(fhirTargetComplexTypeChildElement);

			//
			Element fhirTargetElementChildElement = new Element("element",
					xsNameSpace);
			fhirTargetElementChildElement.setAttribute("name", mmNode.name);
			fhirTargetElementChildElement.setAttribute("type", mmNode.typeName);
			fhirTargetElement.addContent(fhirTargetElementChildElement);

			getNodeDescription(mmElement, fhirTargetComplexTypeChildElement);

			processTypeChildren(mmElement, fhirTargetComplexTypeChildElement);

			if (fhirTargetComplexTypeChildElement.getChildren().size() == 0) {
				addPlaceHolder(fhirTargetComplexTypeChildElement);
			}

		} else {
			processChildren(mmElement);
		}

	}

	private void processTypeChildren(Element mmElement,
			Element fhirTargetElement) {
		XPathFactory xFactory = XPathFactory.instance();
		try {
			XPathExpression<Element> expr = xFactory.compile("./node",
					Filters.element());
			List<Element> mmElementChildren = expr.evaluate(mmElement);
			Element fhirTargetSequenceChildElement = null;
			for (Element mmElementChild : mmElementChildren) {
				MindMapNodeText mmNodeText = getNodeText(mmElementChild);
				if (mmNodeText.name.length() > 0) {
					if (fhirTargetSequenceChildElement == null) {
						Element fhirTargetComplexChildElement = new Element(
								"complexContent", xsNameSpace);
						Element fhirTargetExtensionChildElement = new Element(
								"extension", xsNameSpace);
						fhirTargetExtensionChildElement.setAttribute("base",
								"Resource");
						fhirTargetSequenceChildElement = new Element(
								"sequence", xsNameSpace);
						fhirTargetElement
								.addContent(fhirTargetComplexChildElement);
						fhirTargetComplexChildElement
								.addContent(fhirTargetExtensionChildElement);
						fhirTargetExtensionChildElement
								.addContent(fhirTargetSequenceChildElement);
					}
					Element fhirTargetElementChildElement = new Element(
							"element", xsNameSpace);
					fhirTargetElementChildElement.setAttribute("name",
							mmNodeText.name);
					fhirTargetElementChildElement.setAttribute("type",
							mmNodeText.typeName);
					if (!mmNodeText.name.equals(mmNodeText.text)) {
						// at this point, we have a case where the name is
						// 'StartTime'
						// and the text is "Start Time [0.. 1]"
						// this indicates that we need to add attributes to
						// element to get out min and max
						getQuantityModifiers(fhirTargetElementChildElement,
								mmNodeText.text);
					}
					fhirTargetSequenceChildElement
							.addContent(fhirTargetElementChildElement);
					q.add(mmElementChild);
				}
			}

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void processChildren(Element mmElement) {
		for (Element mmChildElement : mmElement.getChildren()) {
			q.add(mmChildElement);
		}
	}

	private void addPlaceHolder(Element newNode) {
		Element tgtAnnotationElement = new Element("annotation", xsNameSpace);
		Element tgtDocumentationElement = new Element("documentation",
				xsNameSpace);
		tgtDocumentationElement.addContent("Type attributes TBD");
		tgtAnnotationElement.addContent(tgtDocumentationElement);
		newNode.addContent(tgtAnnotationElement);
	}

	private void getQuantityModifiers(Element tgtElementElement, String text) {
		// Text will generally be of the form "Name ..[0.. 1]"
		// in this case, add 'minOccurs="0"' and 'maxOccurs='1"' attributes to
		// the
		// target element.
		// We might also see "Name [0..M]", which correspond to maxOccurs =
		// "unbounded"
		Pattern pattern = Pattern.compile("\\[([0-9]+)\\.+\\s+(\\w+)");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			String min = matcher.group(1);
			String max = matcher.group(2);
			if (max.equals("M")) {
				max = "unbounded";
			}
			tgtElementElement.setAttribute("minOccurs", min);
			tgtElementElement.setAttribute("maxOccurs", max);
		}

	}

	private void getNodeDescription(Element mmElement, Element fhirTargetElement) {
		XPathFactory xFactory = XPathFactory.instance();
		try {
			XPathExpression<Element> expr = xFactory.compile(
					"./node[@TEXT='Information']", Filters.element());
			List<Element> informationElements = expr.evaluate(mmElement);
			Element infoToRemove = null;
			for (Element informationElement : informationElements) {
				Element descriptionElement = informationElement
						.getChild("node");
				if (descriptionElement != null) {
					String desc = descriptionElement.getAttributeValue("TEXT");
					Element tgtAnnotationElement = new Element("annotation",
							xsNameSpace);
					Element tgtDocumentationElement = new Element(
							"documentation", xsNameSpace);
					tgtDocumentationElement.addContent(desc);
					tgtAnnotationElement.addContent(tgtDocumentationElement);
					fhirTargetElement.addContent(tgtAnnotationElement);
				}
				infoToRemove = informationElement;
				break;
			}
			if (infoToRemove != null) {
				mmElement.removeContent(infoToRemove);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	private MindMapNodeText getNodeText(Element currentElement) {
		MindMapNodeText mindMapNodeText = new MindMapNodeText();
		final String[] exclusions = { "key", "CD", "TS", "Information", "CEMs" };
		if (currentElement.getName().equalsIgnoreCase("node")) {
			mindMapNodeText.text = currentElement.getAttributeValue("TEXT");
			if (mindMapNodeText.text != null
					&& mindMapNodeText.text.trim().length() > 0) {
				final String[] partitions = mindMapNodeText.text.split("\\s+");
				if (partitions.length > 0) {
					mindMapNodeText.name = partitions[0];
					if (isExclusion(exclusions, mindMapNodeText.name)) {
						mindMapNodeText.name = "";
					} else {
						mindMapNodeText.typeName = mindMapNodeText.name
								.replaceAll("\\s+", "") + "Type";
					}
				}
			}
		}
		return mindMapNodeText;
	}

	private boolean isExclusion(String[] exclusions, String candidate) {
		boolean retValue = false;
		for (String exclusion : exclusions) {
			if (exclusion.equals(candidate)) {
				retValue = true;
				break;
			}
		}
		return retValue;
	}

	private Element createFhirTargetElement() {
		Element fhirTargetElement = new Element("schema");
		fhirTargetElement.setNamespace(xsNameSpace);
		Element includeElement = new Element("include", xsNameSpace);
		includeElement.setAttribute(new Attribute("schemaLocation",
				"fhir-base.xsd"));
		fhirTargetElement.addContent(includeElement);
		return fhirTargetElement;
	}

	@SuppressWarnings("unused")
	private void displayFhirVisited() {
		for (String key : fhirVisited.keySet()) {
			Element complexType = fhirVisited.get(key);
			System.out.println(key + " ==> " + complexType.getTextNormalize());
		}
	}

	private Element parseFileToElementTree(File xmlFile) {
		Element rootNode = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(xmlFile);
			rootNode = document.getRootElement();
		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
		return rootNode;
	}

	public String getFhirBaseFileName() {
		return fhirBaseFileName;
	}

	public String getMmFileName() {
		return mmFileName;
	}

	private HashMap<String, Element> traverseAndCache(Element rootElement) {
		final HashMap<String, Element> complexTypeMap = new HashMap<String, Element>();
		Namespace xsdNamespace = rootElement.getNamespace("xs");
		XPathFactory xFactory = XPathFactory.instance();
		try {
			XPathExpression<Element> expr = xFactory.compile(
					"//xs:complexType", Filters.element(), null, xsdNamespace);
			List<Element> complexTypeElements = expr.evaluate(rootElement);
			for (Element complexTypeElement : complexTypeElements) {
				String name = complexTypeElement.getAttributeValue("name");
				complexTypeMap.put(name, complexTypeElement);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return complexTypeMap;
	}

	private void writeFhirTarget(Element fhirTargetElement) {
		try {
			Document doc = new Document();
			doc.setRootElement(fhirTargetElement);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			File workingDirectory = new File("summarization\\src\\main\\resources\\xfer");
			File outputXmlFile = new File(workingDirectory, "fhir-result.xml");
			xmlOutput.output(doc,
					new FileWriter(outputXmlFile.getAbsoluteFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void buildCli(String[] args) {
		Option help = new Option("help", "print this message");
		Option schemaFileOption = OptionBuilder.withArgName("fhir").hasArg()
				.withDescription("use given file for fhir basic xsd")
				.create("fhir");
		Option modelFileOption = OptionBuilder.withArgName("mm").hasArg()
				.withDescription("use given file for mind map model")
				.create("mm");
		Options options = new Options();
		options.addOption(schemaFileOption);
		options.addOption(modelFileOption);
		options.addOption(help);

		// create the command line parser
		CommandLineParser parser = new BasicParser();

		try {
			// parse the command line arguments
			if (args.length < 2) {
				usage(options);
			} else {
				CommandLine line = parser.parse(options, args);
				if (line.hasOption("help")) {
					usage(options);
				} else {
					if (line.hasOption("fhir")) {
						setFirBaseFileName(line.getOptionValue("fhir"));
					}
					if (line.hasOption("mm")) {
						setMmFileName(line.getOptionValue("mm"));
					}
					if (StringUtils.isEmpty(getFhirBaseFileName())
							|| StringUtils.isEmpty(getMmFileName())) {
						usage(options);
					}
				}
			}

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
			usage(options);
		}

	}

	private void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getClass().getSimpleName(), options);
		System.exit(1);
	}

	private void setFirBaseFileName(String fhirBaseFileName) {
		this.fhirBaseFileName = fhirBaseFileName;

	}

	private void setMmFileName(String mmFileName) {
		this.mmFileName = mmFileName;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName() + ":\n");
		sb.append("\tfhirBaseFileName => " + getFhirBaseFileName() + "\n");
		sb.append("\tmmFileName => " + getMmFileName() + "\n");
		return sb.toString();
	}

}
