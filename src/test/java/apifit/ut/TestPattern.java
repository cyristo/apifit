package apifit.ut;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import apifit.common.DataPattern;

public class TestPattern {

	@Test
	public void patternTest() {
		
		LocalDateTime today = LocalDateTime.now();
		
		String pattern = "APIFIT:TODAY +7";
		LocalDateTime todayPlus7 = today.plusDays(7);
		String targetDateIn = StringUtils.substringBefore(todayPlus7.toString(), "T");
		String patternExecResult = StringUtils.substringBefore(DataPattern.doPattern(pattern).toString(), "T");
		assertEquals(targetDateIn, patternExecResult);
		
		pattern = "APIFIT:TODAY";
		targetDateIn = StringUtils.substringBefore(today.toString(), "T");
		patternExecResult = StringUtils.substringBefore(DataPattern.doPattern(pattern).toString(), "T");
		assertEquals(targetDateIn, patternExecResult);
		
		pattern = "APIFIT:TODAY -2";
		LocalDateTime todayMinus2 = today.minusDays(2);
		targetDateIn = StringUtils.substringBefore(todayMinus2.toString(), "T");
		patternExecResult = StringUtils.substringBefore(DataPattern.doPattern(pattern).toString(), "T");
		assertEquals(targetDateIn, patternExecResult);
		
	}
	
	@Test
	public void numberPatternTest() {
		
		String pattern = "APIFIT:COUNT(toto)";
		assertEquals(new Integer(0), DataPattern.doPattern(pattern, payload));
		pattern = "APIFIT:COUNT (hotelCode )";
		assertEquals(new Integer(1), DataPattern.doPattern(pattern, payload));
		pattern = "APIFIT: COUNT(Code)";
		assertEquals(new Integer(3), DataPattern.doPattern(pattern, payload));
		pattern = "APIFIT : COUNT(\"hotelCode\" :)";
		assertEquals(new Integer(1), DataPattern.doPattern(pattern, payload));
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
}
