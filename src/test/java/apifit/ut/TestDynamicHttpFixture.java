package apifit.ut;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import apifit.fixture.APIFixture;

public class TestDynamicHttpFixture {

	@Test
	public void testURLBuilding() {

		//Arrange
		String targetUrl = "http://rec2-secure.accorhotels.ws:80/rest/v2.0/rec2-m.accorhotels.com/hotels/9013/rooms/DBC/offers/COG7/"
				+ "options?dateIn=[TODAY+1]&nbNight=2&adults=1&childrenAges=1&cy=EUR&optionType=BREAKFAST";

		LocalDateTime dayPlusOne = LocalDateTime.now().plusDays(new Integer(1));;
		targetUrl = StringUtils.replace(targetUrl, "[TODAY+1]", StringUtils.substringBefore(dayPlusOne.toString(), "T"));		
		
		String hotel = "9013";
		String room = "DBC";
		String offer = "COG7";
		//String dateIn = "2016-06-04";
		String nbNight = "2";
		String adults = "1";
		String childrenAges = "1";
		String currency = "EUR";
		String optionType = "BREAKFAST";

		String scheme = "http";
		String host = "[env]-secure.[clientId].ws";
		String path = "/rest/[version]/[env]-m.[clientId].com";
		String optionsPath = "/hotels/[hotel]/rooms/[room]/offers/[offer]/options";

		//public HttpRequestFixture(String httpVerb, String scheme, String host, Integer port, String path) {
		APIFixture httpFixture = new APIFixture(scheme, host, 80+"", path+optionsPath);
		httpFixture.reset();		
		httpFixture.set("[env]", "rec2");
		httpFixture.set("[version]", "v2.0");
		httpFixture.set("[clientId]", "accorhotels");
		httpFixture.set("[hotel]", hotel);
		httpFixture.set("[room]", room);
		httpFixture.set("[offer]", offer);
		
		httpFixture.set("dateIn", "APIFIT:TODAY+1");
		httpFixture.set("nbNight", nbNight);
		httpFixture.set("adults", adults);
		httpFixture.set("childrenAges", childrenAges);
		httpFixture.set("cy", currency);
		httpFixture.set("optionType", optionType);
				
		assertEquals(targetUrl, httpFixture.URL());
	}
}
