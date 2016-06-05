package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import apifit.json.JsonToDTOConverter;

public class TestJsonToDTO {

	@Test
	public void convertLine() {
		String line0 = null;
		String line1 = "";
		String line2 = "{";
		String line3 = "\"hotelCode\": \"\",";
		String line4 = "\"room\": [";
		String line5 = "\"period\": {";
		String line6 = "\"childrenAges\": [";
		String line7 = "0";
		String line8 = "],";
		String line9 = "\"nights\": 0";
		String line10 = "\"adress1\": 0";
		
		assertNull(JsonToDTOConverter.createField(line0));
		assertNull(JsonToDTOConverter.createField(line1));
		assertNull(JsonToDTOConverter.createField(line2));
		assertEquals("", "private String hotelCode = null;", JsonToDTOConverter.createField(line3));
		assertEquals("", "private String room = null;", JsonToDTOConverter.createField(line4));
		assertEquals("", "private String period = null;", JsonToDTOConverter.createField(line5));
		assertEquals("", "private String childrenAges = null;", JsonToDTOConverter.createField(line6));
		assertNull(JsonToDTOConverter.createField(line7));
		assertNull(JsonToDTOConverter.createField(line8));
		assertEquals("", "private String nights = null;", JsonToDTOConverter.createField(line9));
		assertEquals("", "private String adress1 = null;", JsonToDTOConverter.createField(line10));
		
		
		
	}
}
