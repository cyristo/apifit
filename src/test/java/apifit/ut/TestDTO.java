package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Hashtable;

import org.junit.Test;

import apifit.common.ApiFitException;
import apifit.common.JsonDTOToolBox;

public class TestDTO {

	@Test
	public void extractFieldNameFromGetterMethodName() {
		JsonDTOToolBox dtoToolBox = new JsonDTOToolBox();
		String methodName = "getMyField()";
		assertEquals("","myField", dtoToolBox.extractFieldNameFromGetterMethodName(methodName));
		methodName = "toto.getMyField()";
		assertEquals("","myField", dtoToolBox.extractFieldNameFromGetterMethodName(methodName));
		methodName = "public java.lang.String com.toto.getMyField()";
		assertEquals("","myField", dtoToolBox.extractFieldNameFromGetterMethodName(methodName));
	}
	
	@Test
	public void getValueFromUnknowDTO() {
		
		//arrange
		boolean exceptionThrown = false;
		SimpleDTO dto = new SimpleDTO();
		dto.setHotelCode("15, rue les Lilas");
		EmbededDTO emb = new EmbededDTO();
		emb.setHotelCode("6, passage du marché");
		dto.setEmbededDTO(emb);
		JsonDTOToolBox dtoToolBox = new JsonDTOToolBox();
		
		//Act
		Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
		try {
			hashTable = dtoToolBox.getValueFieldsFromDTO(dto);
		} catch (ApiFitException e) {
			exceptionThrown = true;
		}

		//Assert
		assertFalse(exceptionThrown);
		assertEquals("", "15, rue les Lilas", hashTable.get("hotelCode_0"));
		assertNull("", hashTable.get("room_0"));
		assertEquals("", "6, passage du marché", hashTable.get("hotelCode_1"));
		assertNull("", hashTable.get("room_1"));
		
	}

}
