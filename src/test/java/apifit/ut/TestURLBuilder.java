package apifit.ut;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import apifit.api.APIToolBox;

public class TestURLBuilder {

	
	@Test
	public void testOptionsURL() {

		String targetURL = "http://rec2-secure.accorhotels.ws:80/rest/v2.0/rec2-m.accorhotels.com/hotels/9013/rooms/DBC/offers/COG7/"
				+ "options?dateIn=2016-06-04&nbNight=2&adults=1&childrenAges=1&cy=EUR&optionType=BREAKFAST";

		String scheme = "http";
		String host = "[env]-secure.[clientId].ws";
		String path = "/rest/[version]/[env]-m.[clientId].com";
		
		APIToolBox box = new APIToolBox();
		String baseURI = box.buildURI(scheme, host, path);
		
		String newURL = baseURI + "/hotels/[hotel]/rooms/[room]/offers/[offer]/options";
		newURL = StringUtils.replace(newURL, "[env]", "rec2");
		newURL = StringUtils.replace(newURL, "[clientId]", "accorhotels");
		newURL = StringUtils.replace(newURL, "[version]", "v2.0");
		newURL = StringUtils.replace(newURL,"[hotel]","9013");
		newURL = StringUtils.replace(newURL,"[room]","DBC");
		newURL = StringUtils.replace(newURL,"[offer]","COG7");
		//options?dateIn=2016-06-04&nbNight=2&adults=1&childrenAges=1¤cy=EUR&optionType=BREAKFAST
		
		newURL = box.addFirstParameter(newURL, "dateIn", "2016-06-04");
		newURL = box.addParameter(newURL, "nbNight", "2");
		newURL = box.addParameter(newURL, "adults", "1");
		newURL = box.addParameter(newURL, "childrenAges", "1");
		newURL = box.addParameter(newURL, "cy", "EUR");
		newURL = box.addParameter(newURL, "optionType", "BREAKFAST");
		
		assertEquals(targetURL, newURL);
	}
	
	

}
