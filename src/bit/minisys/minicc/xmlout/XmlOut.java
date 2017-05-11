package bit.minisys.minicc.xmlout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.crypto.dsig.Transform;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlOut {
	public int tokensNumber = 0;
	public ArrayList<String> value;
	public ArrayList<String> type;
	public ArrayList<Integer> line;
	public ArrayList<String> valid;
	public XmlOut(){
		value = new ArrayList<String>();
		type = new ArrayList<String>();
		line = new ArrayList<Integer>();
		valid = new ArrayList<String>();
	}
	public void printXml(String inFile,String outFile) throws IOException, ParserConfigurationException, SAXException, TransformerException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element project = document.createElement("project");
		project.setAttribute("name", inFile);
		document.appendChild(project);
		Element tokens = document.createElement("tokens");
		for(int i=0;i<tokensNumber;i++){
			Element token = document.createElement("token");
			Element numbere = document.createElement("number");
			numbere.setTextContent(String.valueOf(1));
			token.appendChild(numbere);
			Element valuee = document.createElement("value");
			valuee.setTextContent(value.get(i));
			token.appendChild(valuee);
			Element typee = document.createElement("type");
			typee.setTextContent(type.get(i));
			token.appendChild(typee);
			Element linee = document.createElement("line");
			linee.setTextContent(String.valueOf(line.get(i)));
			token.appendChild(linee);
			Element valide = document.createElement("valid");
			valide.setTextContent(valid.get(i));
			token.appendChild(valide);
			tokens.appendChild(token);
		}
		project.appendChild(tokens);
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer();
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		tf.transform(new DOMSource(document), new StreamResult(new File(outFile)));
	}
}
