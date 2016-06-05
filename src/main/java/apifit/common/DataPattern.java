package apifit.common;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

public class DataPattern {

	public static boolean isApiFitPattern(String chain) {
		if (chain == null) return false;
		return StringUtils.startsWith(StringUtils.remove(chain, " "), "APIFIT:");
	}
	
	public static Object doPattern(String chain) {
		String pattern = extractPattern(chain.trim());
		if (isDatePattern(pattern)) return getDateFromPattern(pattern);
		return null;
	}
	
	public static Object doPattern(String chain, String content) {
		String pattern = extractPattern(chain.trim());
		if (isCounterPattern(pattern)) return getPatternOccurenceNumberFromContent(pattern, content);
		return null;
	}

	public static boolean isCounterPattern(String pattern) {
		if (pattern.contains("COUNT(")) return true;
		return false;
	}

	public static boolean isDatePattern(String pattern) {
		if (pattern.contains("TODAY") || pattern.equals("NOW")) return true;
		return false;
	}

	private static LocalDateTime getDateFromPattern(String pattern) {
		LocalDateTime returnedDate = null;
		if (pattern.equals("TODAY") || pattern.equals("NOW")) {
			returnedDate = LocalDateTime.now();
		} else if (pattern.contains("TODAY")) {
			String days = "0";
			if (pattern.contains("+")) {
				days = StringUtils.substringAfterLast(pattern, "+");
				LocalDateTime time = LocalDateTime.now();
				returnedDate = time.plusDays(new Integer(days));			
			} else if (pattern.contains("-")) {
				days = StringUtils.substringAfterLast(pattern, "-");
				LocalDateTime time = LocalDateTime.now();
				returnedDate = time.minusDays(new Integer(days));			
			}
		}
		return returnedDate;
	}

	public static Integer getPatternOccurenceNumberFromContent(String pattern, String content) {
		
		Integer returnedNumber = 0;
		if (pattern.startsWith("COUNT(")) {
			String toMatch = StringUtils.substringBetween(pattern, "(", ")");
			returnedNumber = StringUtils.countMatches(StringUtils.remove(content, " "), StringUtils.remove(toMatch, " "));
		}
		return returnedNumber;

	}
	
	private static String extractPattern(String chain) {
		return StringUtils.removeStart(StringUtils.remove(chain, " "), "APIFIT:").trim();
	}
}
