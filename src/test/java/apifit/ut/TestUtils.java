package apifit.ut;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import apifit.common.ApiFitUtils;

public class TestUtils {

	@Test
	public void testNumbers() {
		
		String intS = "123";
		String doubleS = "123.253";
		
		assertTrue(ApiFitUtils.isInteger(intS));
		assertFalse(ApiFitUtils.isInteger(doubleS));		
		
	}
	
	
}
