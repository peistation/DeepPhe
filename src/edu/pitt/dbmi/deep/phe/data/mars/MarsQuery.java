package edu.pitt.dbmi.deep.phe.data.mars;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.deep.phe.data.model.Patient;
import edu.pitt.dbmi.deep.phe.data.model.Report;
import edu.pitt.dbmi.deep.phe.util.XMLUtils;

/**
 * This is a bridge for query MARS system for documents
 * @author tseytlin
 *
 */
public class MarsQuery {
	private String MARS_REMOTE_URL = "https://query-service.mars-systems.com/mars_query/MarsQuery";
	private String MARS_USER = "";
	private String MARS_PASS = "";
	private String MARS_CLIENT = "UPMC";
	private String MARS_KEYSTORE = System.getProperty("user.home")+File.separator+".keystore";
	private String MARS_KEYSTORE_PASSWORD = "caties5";

	
	private String client;
	private String password;
	private String trustStore;
	private String url;
	private String userId;
	private List<Patient> results = null;
	
	/**
	 * this is a query document that we use to query the system
	 */
	private static final String MARS_QUERY = 
			"<?xml version=\"1.0\"?>\n" + 
			"<Request Service=\"getRecords\">\n" + 
			"	<DataBaseName>emr</DataBaseName>\n" + 
			"	<RecentRecords>false</RecentRecords>\n" + 
			"	<DateLimit DateField=\"PQ\" ReverseLimit=\"false\"  ReferenceDate=\"now\">\n" + 
			"		<StartDate></StartDate>\n" + 
			"		<EndDate></EndDate>\n" + 
			"	</DateLimit>\n" + 
			"	<Search></Search>\n" + 
			"	<ReportList>\n" + 
			"		<EMRReport WithBody=\"true\">\n" + 
			"			<Column>ID</Column>\n" + 
			"			<Column>ID_NO</Column>\n" + 
			"			<Column>PQNO</Column>\n" + 
			"			<Column>NAME</Column>\n" + 
			"			<Column>RECORD_STATUS</Column>\n" + 
			"			<Column>DOB</Column>\n" + 
			"			<Column>DOP</Column>\n" + 
			"			<Column>SSN</Column>\n" + 
			"			<Column>SEX</Column>\n" + 
			"			<Column>RACE</Column>\n" + 
			"			<Column>RECORD_TYPE</Column>\n" + 
			"			<Column>REPORT_SUBTYPE</Column>\n" + 
			"			<Column>PATIENT_STATUS</Column>\n" + 
			"			<Column>RECORD_ID</Column>\n" + 
			"			<Column>DATE</Column>\n" + 
			"			<Column>TIME</Column>\n" + 
			"			<Column>ACCESSION_DATE</Column>\n" + 
			"			<Column>BODY</Column>\n" + 
			"		</EMRReport>\n" + 
			"	</ReportList>\n" + 
			"</Request>\n" ;
	
	
	/**
	 * connect to MARS webservice with username and password
	 * @param url
	 * @param userId
	 * @param password
	 * @param client
	 * @param trustStore
	 */
	public MarsQuery(String userId, String password) {
		this.userId = userId;
		this.password = password;
		this.url = MARS_REMOTE_URL;
		this.client = MARS_CLIENT;
		this.trustStore = MARS_KEYSTORE;
		
		System.setProperty("javax.net.ssl.trustStore", MARS_KEYSTORE); 
		System.setProperty("javax.net.ssl.trustStorePassword", MARS_KEYSTORE_PASSWORD); 
	}

	
	
	/**
	 * create query document
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document getQueryDocument() throws ParserConfigurationException, SAXException, IOException {
		Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
           
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream docStream =  new ByteArrayInputStream(MARS_QUERY.getBytes());
        document = builder.parse(docStream);
        Node requestNode = document.getFirstChild();
        
        Node searchNode = null;
        NodeList paramNodes = requestNode.getChildNodes();
        for ( int i = 0; i<paramNodes.getLength(); i++) {
        	Node paramNode = paramNodes.item(i);
        	if (paramNode.getNodeName().equalsIgnoreCase("Search")) {
        		searchNode = paramNode;
        		break;
        	}
        }

        CDATASection cdata = document.createCDATASection(getQuery());
        searchNode.appendChild(cdata); 
        return document;
	}

	/**
	 * search query
	 * @return
	 */
	private String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	/**
	 * add attribute to soap
	 * @param envelope
	 * @param attrName
	 * @param value
	 * @throws SOAPException
	 */
	private void addAttribute(SOAPEnvelope envelope, String attrName, String value) throws SOAPException {
		SOAPBody body = envelope.getBody();

		Name reqName = envelope.createName("Request");
		Iterator childElements = body.getChildElements(reqName);
		Iterator<SOAPElement> reqIt = childElements;
		SOAPElement reqNode = null;

		if (reqIt.hasNext()) {
			reqNode = reqIt.next();
		}
		if (reqNode == null) {
			System.out.println("Missing Node: Request");
			System.exit(1);
		}

		Name name = envelope.createName(attrName);
		reqNode.removeAttribute(name);
		reqNode.addAttribute(name, value);
	}



	/**
	 * Process MARS query
	 * @throws Exception
	 */
	public void doQuery() throws Exception{
		int attempt = 0;
		do{
			try {
				SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
				MessageFactory mf = MessageFactory.newInstance();
				SOAPMessage request = mf.createMessage();

				SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();

				// remove header
				envelope.getHeader().detachNode();
				SOAPBody body = envelope.getBody();

				body.addDocument(getQueryDocument());
				addAttribute(envelope, "client", client);
				addAttribute(envelope, "name", userId);
				addAttribute(envelope, "password", password);
				//XMLUtils.printFromNode(request.getSOAPBody().getParentNode());
	
				URL endpoint = new URL(url);
				SOAPMessage response = con.call(request, endpoint);
				con.close();
				//XMLUtils.printFromNode(response.getSOAPBody().getElementsByTagName("EMRReportList").item(0));
				//XMLUtils.printFromNode(request.getSOAPBody().getParentNode());
				SOAPBody responseBody = response.getSOAPBody();
				if (responseBody.hasFault()) {
					processFault(responseBody);
				} else {
					results = processReply(responseBody);
				}
				return;
			} catch (SOAPException e) {
				System.err.println("Exception thrown attempting to execute query (attempt "+(attempt+1)+") : " + e.toString());
				e.printStackTrace();
				Thread.sleep(60000);
			}
		}while(attempt ++ < 3);
	}


	private void processFault(SOAPBody responseBody) {
		SOAPFault newFault = responseBody.getFault();
		if (newFault != null) {

			// Get the qualified name of the fault code
			Name code = newFault.getFaultCodeAsName();

			String faultString = newFault.getFaultString();
			String actor = newFault.getFaultActor();

			System.err.println("SOAP fault contains: ");
			System.err.println("  FaultCode = " + code.getQualifiedName());
			System.err.println("  FaultString = " + faultString);
			System.err.println("  Actor = " + actor);
		} else {
			System.out.println("Unexpected error: fault is null");
		}

	}
	
	/**
	 * private process reply
	 * @param responseBody
	 * @return
	 * @throws TransformerException
	 * @throws AuthenticationException
	 */
	
	private List<Patient> processReply(SOAPBody responseBody) throws TransformerException, AuthenticationException {
		Map<String,Patient> results = new HashMap<String, Patient>();
		//		XMLUtils.printXML(responseBody, "", logger);
		Element soapResult = XMLUtils.getElementByTagName(responseBody,"Result");
		if (soapResult != null) {
			Element userInfo = XMLUtils.getElementByTagName(soapResult,"UserAccountInfo");
			if(userInfo != null){
				Element status = XMLUtils.getElementByTagName(userInfo,"Status");
				if(status != null){
					StringBuffer b = new StringBuffer("");
					for(Element e: XMLUtils.toElements(userInfo.getChildNodes())){
						b.append(e.getTagName()+" : "+e.getTextContent().trim()+", ");
					}
					// if not success, throw authentication exception
					if(!"SUCCESS".equals(status.getTextContent().trim())){
						throw new AuthenticationException("Failed to Connect with: "+b.substring(0,b.length()-2).toString());
					}
				}
			}
			NodeList reportNodes = soapResult.getElementsByTagName("EMRReport");
			for (int i = 0; i < reportNodes.getLength(); i++) {
				Report r = processReport(reportNodes.item(i));
				Patient p = results.get(r.getPatient().getMedicalRecordNumber());
				if(p == null){
					p = r.getPatient();
					results.put(p.getMedicalRecordNumber(),p);
				}
				p.addReport(r);
			}
		}
		return new ArrayList<Patient>(results.values());
	}
	
	/**
	 * create report object from the XML dome
	 * @param item
	 * @return
	 */
	private Report processReport(Node item) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
