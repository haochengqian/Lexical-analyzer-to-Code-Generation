package bit.minisys.minicc.xmlout;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.python.antlr.PythonParser.else_clause_return;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlIn {
	public int tokensNumber = 0;
	public ArrayList<String> number;
	public ArrayList<String> value;
	public ArrayList<String> type;
	public ArrayList<String> line;
	public ArrayList<String> valid;
	public XmlIn(){
		number = new ArrayList<String>();
		value = new ArrayList<String>();
		type = new ArrayList<String>();
		line = new ArrayList<String>();
		valid = new ArrayList<String>();
	}
	public void readXML(String fileIn) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(fileIn);
		NodeList nodeList = doc.getElementsByTagName("token");
		for(int i = 0;i < nodeList.getLength();i++){
			Element token = (Element) nodeList.item(i);
			for(org.w3c.dom.Node node = token.getFirstChild();node!=null;node = node.getNextSibling()){
				if(node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
					String tokenValue = node.getFirstChild().getNodeValue();
					String name = node.getNodeName();
					if(name.equals("value"))
						value.add(tokenValue);
					else if(name.equals("type"))
						type.add(tokenValue);
					else if(name.equals("line"))
						line.add(tokenValue);
					else if(name.equals("valid"))
						valid.add(tokenValue);
				}
			}
		}
	}
}
