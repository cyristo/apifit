package apifit.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.XmlPath.CompatibilityMode;

import apifit.common.ApiFitException;
import apifit.common.ApiFitLogger;

public class XmlToolBox {


	public Object getXmlParamValue(String xmlPayload, String name) {
		Object obj = null;
		try {
			XmlPath xmlPath = new XmlPath(xmlPayload);
			obj = xmlPath.get(name);			
		} catch (IllegalArgumentException e) {
			ApiFitLogger.log("XML PARSING ERROR : " + e.getMessage());
			throw e;
		} catch (PathNotFoundException e1) {
			ApiFitLogger.log("XML PARSING ERROR : " + e1.getMessage());
			throw e1;
		}
		return obj;
	}

	public Object getHtmlParamValue(String htmlPayload, String name) {
		Object obj = null;
		try {
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, htmlPayload);
			obj = xmlPath.get(name);			
		} catch (IllegalArgumentException e) {
			ApiFitLogger.log("HTML PARSING ERROR : " + e.getMessage());
			throw e;
		} catch (PathNotFoundException e1) {
			ApiFitLogger.log("HTML PARSING ERROR : " + e1.getMessage());
			throw e1;
		}
		return obj;
	}

	public boolean isXmlElement(String s) {
		boolean retBool = false;
		Pattern pattern;
		Matcher matcher;

		// REGULAR EXPRESSION TO SEE IF IT AT LEAST STARTS AND ENDS
		// WITH THE SAME ELEMENT
		final String XML_PATTERN_STR = "<(\\S+?)(.*?)>(.*?)</\\1>";

		// IF WE HAVE A STRING
		if (s != null && s.trim().length() > 0) {

			// IF WE EVEN RESEMBLE XML
			if (s.trim().startsWith("<")) {

				pattern = Pattern.compile(XML_PATTERN_STR,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

				// RETURN TRUE IF IT HAS PASSED BOTH TESTS
				matcher = pattern.matcher(s);
				retBool = matcher.matches();
			}
			// ELSE WE ARE FALSE
		}

		return retBool;
	}

	public String replaceXmlElement(String xmlElement, String newElement) {
		String newXmlElement = xmlElement;

		String oldElement = StringUtils.substringBetween(xmlElement, ">", "</");
		newXmlElement = StringUtils.replace(xmlElement, oldElement, newElement);

		return newXmlElement;
	}

	public String updateXmlNodeValue(String payload, String expression, String value) throws ApiFitException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(payload.getBytes(StandardCharsets.ISO_8859_1));
			doc = b.parse(stream);
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
			node.setTextContent(value);
		} catch (XPathExpressionException e) {
			throw new ApiFitException(e);
		} catch (ParserConfigurationException e) {
			throw new ApiFitException(e);
		} catch (SAXException e) {
			throw new ApiFitException(e);
		} catch (IOException e) {
			throw new ApiFitException(e);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
	        // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new ApiFitException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new ApiFitException(e);
		} catch (TransformerException e) {
			throw new ApiFitException(e);
		}

		return out.toString();

	}
}
