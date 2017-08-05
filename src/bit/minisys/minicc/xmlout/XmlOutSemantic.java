package bit.minisys.minicc.xmlout;

import bit.minisys.minicc.parser.Syntax;
import bit.minisys.minicc.parser.MiniCCParser.RecordTree;
import mars.util.FilenameFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
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


public class XmlOutSemantic {
	public RecordTree root;
	public Document document; 
	public void findChild(RecordTree rootn,Element parent){
		if(rootn.children.size() == 0&&!rootn.father.equals("@")){
			if(rootn.father == null) return;
			Element element = document.createElement(rootn.father);
//			element.setTextContent(rootn.value);
//			parent.appendChild(element);
			parent.setTextContent(rootn.value);
//			System.out.println(rootn.value);
			return;
		}
		else{
			Element element = document.createElement(rootn.value);
			for(int i= rootn.children.size() - 1;i>=0;i--){
				findChild(rootn.children.get(i), element);
				parent.appendChild(element);
			}
		}
	}
	public void out(String fileOut) throws ParserConfigurationException, TransformerException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		Element project = document.createElement("project");
		project.setAttribute("name", fileOut);
		Element rtElement = document.createElement(root.value);
		findChild(root, rtElement);
		project.appendChild(rtElement);
		document.appendChild(project);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transform = transformerFactory.newTransformer();
		transform.setOutputProperty(OutputKeys.INDENT, "yes");
		transform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transform.transform(new DOMSource(document), new StreamResult(new File(fileOut)));
	}
}
