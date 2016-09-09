package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
	
}
