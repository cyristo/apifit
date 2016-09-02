package apifit.common;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

public class DataPattern {
/*
	public static final String COUNT_PATTERN = "COUNT_PATTERN";
	public static final String DATE_PATTERN = "DATE_PATTERN";
	public static final String CONTAINS_ANY_PATTERN = "CONTAINS_ANY_PATTERN";
	*/
	/*
	 * 
IsEmpty/IsBlank - checks if a String contains text
Trim/Strip - removes leading and trailing whitespace
Equals - compares two strings null-safe
startsWith - check if a String starts with a prefix null-safe
endsWith - check if a String ends with a suffix null-safe
IndexOf/LastIndexOf/Contains - null-safe index-of checks
IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut - index-of any of a set of Strings
ContainsOnly/ContainsNone/ContainsAny - does String contains only/none/any of these characters
Substring/Left/Right/Mid - null-safe substring extractions
SubstringBefore/SubstringAfter/SubstringBetween - substring extraction relative to other strings
Split/Join - splits a String into an array of substrings and vice versa
Remove/Delete - removes part of a String
Replace/Overlay - Searches a String and replaces one String with another
Chomp/Chop - removes the last part of a String
LeftPad/RightPad/Center/Repeat - pads a String
UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize - changes the case of a String
CountMatches - counts the number of occurrences of one String in another
IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable - checks the characters in a String
DefaultString - protects against a null input String
Reverse/ReverseDelimited - reverses a String
Abbreviate - abbreviates a string using ellipsis
Difference - compares Strings and reports on their differences
LevensteinDistance - the number of changes needed to change one String into another
	 */
	
	public static boolean isApiFitPattern(String chain) {
		if (chain == null) return false;
		return StringUtils.startsWith(chain, "APIFIT:");
	}
	
	public static Object doPattern(String chain) {
		String pattern = extractPattern(chain.trim());
		if (isDatePattern(pattern)) return getDateFromPattern(pattern);
		return null;
	}
	
	public static Object doPattern(String chain, String content) {
		String pattern = extractPattern(chain.trim());
		if (isCounterPattern(pattern)) return getOccurenceNumberFromContent(pattern, content);
		if (isStringPattern(pattern)) return getStringFromContent(pattern, content);
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
	
	public static boolean isStringPattern(String pattern) {
		if (pattern.contains("CONTAINS")) return true;
		return false;
	}
	
	// private part //
	
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
	private static Integer getOccurenceNumberFromContent(String pattern, String content) {
		
		Integer returnedNumber = 0;
		if (pattern.startsWith("COUNT(")) {
			String toMatch = StringUtils.substringBetween(pattern, "(", ")");
			returnedNumber = StringUtils.countMatches(StringUtils.remove(content, " "), StringUtils.remove(toMatch, " "));
		}
		return returnedNumber;
	}
	private static String getStringFromContent(String pattern, String content) {
		String returnedString = "FALSE"; 
		if (pattern.startsWith("CONTAINS(")) {
			String toMatch = StringUtils.substringBetween(pattern, "(", ")");
			if (StringUtils.contains(content, toMatch)) returnedString = "TRUE";
		}
		return returnedString;
	}
	private static String extractPattern(String chain) {
		return StringUtils.removeStart(chain, "APIFIT:").trim();
	}
}
