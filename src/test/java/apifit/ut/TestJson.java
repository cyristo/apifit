package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import apifit.common.ApiFitException;
import apifit.common.ApiFitUtils;
import apifit.common.JsonDTOToolBox;
import apifit.json.JsonToolBox;
import apifit.json.ValidationUtils;

public class TestJson {

	@Test
	public void updateJsonNode() {
		JsonToolBox jsonToolBox = new JsonToolBox();
		String updatedPayload = null;
		try {
			updatedPayload = jsonToolBox.updateJsonAttribute(payload, "$.room[0].period.dateIn", "2022-11-11");
			updatedPayload = jsonToolBox.updateJsonAttribute(updatedPayload, "$.room[0].period.nights", 3);
		} catch (ApiFitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("", "2022-11-11", jsonToolBox.getJsonParamValue(updatedPayload, "$.room[0].period.dateIn"));
		assertEquals("", 3, jsonToolBox.getJsonParamValue(updatedPayload, "$.room[0].period.nights"));
	}

	@Test
	public void getJsonParamValue() {	
		JsonToolBox jsonToolBox = new JsonToolBox();
		assertEquals("", "7565", jsonToolBox.getJsonParamValue(payload, "hotelCode"));
		assertEquals("", "TWB", jsonToolBox.getJsonParamValue(payload, "room[0].productCode"));
		assertEquals("", "2016-05-30", jsonToolBox.getJsonParamValue(payload, "room[0].period.dateIn"));
		assertEquals("", new ArrayList(), jsonToolBox.getJsonParamValue(payload, "room[0].childrenAges"));
		assertEquals("", new Integer(1), jsonToolBox.getJsonParamValue(payload, "room[0].adults"));
		assertEquals("", new Boolean(false), new Boolean(jsonToolBox.getJsonParamValue(payload, "room[0].discount").toString()));
		assertEquals("", new Float(16.90), jsonToolBox.getJsonParamValue(payload, "room[0].price"));
	}

	@Test
	public void updateJsonPayload() {
		JsonToolBox jsonToolBox = new JsonToolBox();
		boolean exceptionThrown = false;
		try {
			String updatedPayload = jsonToolBox.updateJsonPayload(payload, "hotelCode", "7777");
			assertEquals("", "7777", jsonToolBox.getJsonParamValue(updatedPayload, "hotelCode"));

			updatedPayload = jsonToolBox.updateJsonPayload(payload, "productCode", "TTT");
			assertEquals("", "TTT", jsonToolBox.getJsonParamValue(updatedPayload, "room[0].productCode"));

			updatedPayload = jsonToolBox.updateJsonPayload(payload, "dateIn", "2022-11-11");
			assertEquals("", "2022-11-11", jsonToolBox.getJsonParamValue(updatedPayload, "room[0].period.dateIn"));

			ArrayList array = new ArrayList();
			array.add(1);
			updatedPayload = jsonToolBox.updateJsonPayload(payload, "childrenAges", array);
			assertEquals("", array, jsonToolBox.getJsonParamValue(updatedPayload, "room[0].childrenAges"));

			Boolean boul = new Boolean(true);
			updatedPayload = jsonToolBox.updateJsonPayload(payload, "discount", boul);
			assertEquals("", boul, new Boolean(jsonToolBox.getJsonParamValue(updatedPayload, "room[0].discount").toString()));

			updatedPayload = jsonToolBox.updateJsonPayload(payload, "price", new Double(20.99));
			//updatedPayload = jsonToolBox.updateJsonPayload(payload, "price", "20.99");
			assertEquals("", new Double(20.99), new Double(jsonToolBox.getJsonParamValue(updatedPayload, "room[0].price").toString()));
			//System.out.println(updatedPayload);

		} catch (ApiFitException e) {
			exceptionThrown = true;
		}
		assertFalse(exceptionThrown);

	}

	@Test
	public void addFieldToPayload() {
		JsonToolBox jsonToolBox = new JsonToolBox();
		String updatedPayload = null;
		try {
			updatedPayload = jsonToolBox.addNodeToPayload(payload, "myNewField", "myNewValue");
		} catch (ApiFitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("", "myNewValue", jsonToolBox.getJsonParamValue(updatedPayload, "myNewField"));
		System.out.println(updatedPayload);
	}

	@Test
	public void populateJsonPayloadFromDTO() {
		//arrange
		JsonToolBox jsonToolBox = new JsonToolBox();
		JsonDTOToolBox dtoToolBox = new JsonDTOToolBox();
		boolean exceptionThrown = false;
		SimpleDTO dto = new SimpleDTO();
		dto.setHotelCode("1234");
		dto.setDateIn("2014-01-02");
		dto.setNights(1);
		dto.setProductCode("myCode");

		String payload = null;		

		//Act
		try {
			payload = ApiFitUtils.getFileContent(".\\src\\test\\resources\\", "json.txt");
			payload = dtoToolBox.populateJsonPayloadFromDTO(dto, payload);
		} catch (ApiFitException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}

		//Assert
		assertFalse(exceptionThrown);
		assertEquals("", "1234", jsonToolBox.getJsonParamValue(payload, "hotelCode"));
		assertEquals("", 1, jsonToolBox.getJsonParamValue(payload, "room[0].period.nights"));
		assertEquals("", "myCode", jsonToolBox.getJsonParamValue(payload, "room[0].productCode"));
		assertEquals("", "2014-01-02", jsonToolBox.getJsonParamValue(payload, "room[0].period.dateIn"));
	}

	@Test
	public void sameFieldInPayload() {
		JsonToolBox jsonToolBox = new JsonToolBox();
		assertEquals("", "444", jsonToolBox.getJsonParamValue(payload2, "homeAddress.street"));
		assertEquals("", "888", jsonToolBox.getJsonParamValue(payload2, "billAddress.street"));

		String updatedPayload = null;
		//System.out.println(payload2);
		try {
			updatedPayload = jsonToolBox.updateJsonPayload(payload2, "street", "15 rue des Lilas", 1);
		} catch (ApiFitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("", "15 rue des Lilas", jsonToolBox.getJsonParamValue(updatedPayload, "homeAddress.street"));
		assertEquals("", "888", jsonToolBox.getJsonParamValue(updatedPayload, "billAddress.street"));
	}

	@Test
	public void removeFromJsonPayload() {

		//arrange
		JsonToolBox jsonToolBox = new JsonToolBox();
		boolean exceptionThrown = false;

		//assert
		assertTrue(payload.contains("option"));
		assertTrue(payload.contains("code"));
		assertTrue(payload.contains("quantity"));

		//act
		try {
			payload = jsonToolBox.removeNodeFromPayload(payload, "option");
		} catch (ApiFitException e) {
			exceptionThrown = false;
		}

		//assert
		assertFalse(payload.contains("option"));
		assertFalse(payload.contains("code"));
		assertFalse(payload.contains("quantity"));

	}

	@Test
	public void testSchemaValidation() {

		boolean exceptionThrown = false;
		String schema = null;
		String goodJson = null;
		String badJson = null;
		
		try {
			goodJson = ApiFitUtils.getFileContent(".\\src\\test\\resources\\", "Good_Example.json");
			badJson = ApiFitUtils.getFileContent(".\\src\\test\\resources\\", "Bad_Example.json");
			schema = ApiFitUtils.getFileContent(".\\src\\test\\resources\\", "Good_Schema.json");
		} catch (ApiFitException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}


		try {
			ValidationUtils.validateJson(schema, goodJson);
		} catch (IOException e) {
			exceptionThrown = true;
			e.printStackTrace();
		} catch (ProcessingException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}

		assertFalse(exceptionThrown);
		
		String reportMessage = null;
		try {
			ValidationUtils.validateJson(schema, badJson);
		} catch (IOException e) {
			exceptionThrown = true;
			e.printStackTrace();
		} catch (ProcessingException e) {
			exceptionThrown = true;
			reportMessage = e.getMessage();
		}

		assertTrue(exceptionThrown);
		assertNotNull(reportMessage);
		
	}

	private String payload = "{"+
			"\"hotelCode\" : \"7565\","+
			"\"room\" : ["+
			"{"+
			"\"adults\" : 1,"+
			"\"childrenAges\" : ["+
			"],"+
			"\"discount\" : \"false\","+
			"\"offerCode\" : \"TKHZU1\","+
			"\"price\" : 16.90,"+
			"\"option\" : ["+
			"{"+
			"\"code\": \"\","+
			"\"quantity\": 0"+
			"}"+
			"],"+
			"\"period\" : {"+
			"\"dateIn\" : \"2016-05-30\","+
			"\"nights\" : 1"+
			"},"+
			"\"productCode\" : \"TWB\""+
			"}"+
			"]"+
			"}";

	private String payload2 = "{"+
			"\"homeAddress\": {"+
			"\"street\": \"444\","+
			"\"zipCode\": \"\","+
			"\"town\": \"\","+
			"\"countryCode\": \"\","+
			"\"stateCode\": \"\""+
			"},"+
			"\"billAddress\": {"+
			"\"street\": \"888\","+
			"\"zipCode\": \"\","+
			"\"town\": \"\","+
			"\"countryCode\": \"\","+
			"\"stateCode\": \"\""+
			"}"+
			"}";

	private static final Configuration configuration = Configuration.builder()
			.jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider())
			.build();

}
