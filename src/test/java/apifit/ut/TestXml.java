package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import apifit.common.ApiFitException;
import apifit.common.ApiFitUtils;
import apifit.xml.XmlToolBox;

public class TestXml {

	@Test
	public void getXmlParamValue() {	
		XmlToolBox xmlToolBox = new XmlToolBox();
		assertEquals("", "Chocolate", xmlToolBox.getXmlParamValue(payload, "shopping.category.item[0].name"));
		assertEquals("", 5, xmlToolBox.getXmlParamValue(payload, "shopping.category.item.size()"));
		assertEquals("", 1, xmlToolBox.getXmlParamValue(payload, "shopping.category[2].item.size()"));

	}
	
	@Test
	public void getHtmlParamValue() {	
		
		String payload = null;
		boolean exceptionThrown = false;
		
		try {
			payload = ApiFitUtils.getFileContent(".\\src\\test\\resources\\", "pagehtml.txt");
		} catch (ApiFitException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}
		
		assertFalse(exceptionThrown);
		
		XmlToolBox xmlToolBox = new XmlToolBox();
		assertEquals("", "REC2 - 16.04 : ", xmlToolBox.getHtmlParamValue(payload, "html.head.title"));
		assertEquals("", "Air France", xmlToolBox.getHtmlParamValue(payload, "html.body.div[0].div[0].div[0].a"));

		//{ it.@id == 'header' }
		
	}
	
	@Test
	public void testXmlNodeReplacement() {
		
		XmlToolBox xmlToolBox = new XmlToolBox();
		String isXml = "<element>toto</element>";
		String isNotXml = "<element>toto<element>";
		
		assertTrue(xmlToolBox.isXmlElement(isXml));
		assertFalse(xmlToolBox.isXmlElement(isNotXml));
		
		assertEquals("<element>titi</element>", xmlToolBox.replaceXmlElement(isXml, "titi"));
				
	}
	
	@Test
	public void testXmlReplacement() {
		boolean exceptionThrown = false;
		XmlToolBox xmlToolBox = new XmlToolBox();
		String newPayload = payload2;
		try {
			newPayload = xmlToolBox.updateXmlNodeValue(payload2, "/data/startdate", "29/07/2015");
		} catch (ApiFitException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}
		assertFalse(exceptionThrown);
		assertEquals("", "29/07/2015", xmlToolBox.getXmlParamValue(newPayload, "data.startdate"));
		
				
	}
	
	private String payload = 
	 "<shopping>"+
		 "<category type=\"groceries\">"+
			 "<item>"+
				 "<name>Chocolate</name>"+
				 "<price>10</price>"+
			 "</item>"+
			 "<item>"+
				 "<name>Coffee</name>"+
				 "<price>20</price>"+
			 "</item>"+
		 "</category>"+
		 "<category type=\"supplies\">"+
			 "<item>"+
				 "<name>Paper</name>"+
				 "<price>5</price>"+
			 "</item>"+
				 "<item quantity=\"4\">"+
				 "<name>Pens</name>"+
			 "<price>15</price>"+
			 "</item>"+
		 "</category>"+
		 "<category type=\"present\">"+
			 "<item when=\"Aug 10\">"+
				 "<name>Kathryn's Birthday</name>"+
				 "<price>200</price>"+
			 "</item>"+
		 "</category>"+
	 "</shopping>";
	
	private String payload2 = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"+
	"<data>"+
	       "<username>admin</username>"+
	       "<password>12345</password>"+
	       "<interval>1</interval>"+
	       "<timeout>90</timeout>"+
	       "<startdate>01/01/2013</startdate>"+
	       "<enddate>06/01/2013</enddate>"+
	       "<ttime>1110</ttime>"+
	    "</data>";
}
