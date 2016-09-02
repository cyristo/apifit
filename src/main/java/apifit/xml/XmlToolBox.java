package apifit.xml;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.XmlPath.CompatibilityMode;

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
	
}
