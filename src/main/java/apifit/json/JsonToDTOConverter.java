package apifit.json;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonToDTOConverter {

	public static void main(String[] args) {
		String fileName = "basket.txt";
		System.out.println(fileName+"++++++++++++++++++++++++++++++++++++++");
		JsonToDTOConverter.convert(fileName);
		System.out.println(fileName+"--------------------------------------");
		
		fileName = "booking.txt";
		System.out.println(fileName+"++++++++++++++++++++++++++++++++++++++");
		JsonToDTOConverter.convert(fileName);
		System.out.println(fileName+"--------------------------------------");
		
		fileName = "identification.txt";
		System.out.println(fileName+"++++++++++++++++++++++++++++++++++++++");
		JsonToDTOConverter.convert(fileName);
		System.out.println(fileName+"--------------------------------------");
	

		fileName = "user.txt";
		System.out.println(fileName+"++++++++++++++++++++++++++++++++++++++");
		JsonToDTOConverter.convert(fileName);
		System.out.println(fileName+"--------------------------------------");
	}
	
	private static void convert(String fileNAme) {
		
		File file = FileUtils.getFile("C:\\Users\\cstock\\workspace\\ahapidomain\\src\\main\\resources\\", fileNAme);
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(file, "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Iterator iter = lines.iterator();
		while (iter.hasNext()) {
			String field = createField((String) iter.next());
			if (field != null) {
				System.out.println(field);
			}
		}
		
	}
	
	public static String createField(String line) {
		String newLine = null;
		if (StringUtils.isNotBlank(line) 
					&& StringUtils.containsAny(line, ALPHEBET) 
					&& StringUtils.containsAny(line, StringUtils.lowerCase(ALPHEBET)) ) {
			line = StringUtils.removePattern(line, "String");
			line = StringUtils.removePattern(line, "[^a-zA-Z][0-9]");
			line = StringUtils.removePattern(line, "[^\\w]");
			if (line.endsWith("\"String\"")) line = StringUtils.removePattern(line, "\"String\"");
			if (line.endsWith("false")) line = StringUtils.removePattern(line, "false");
			if (line.endsWith("false,")) line = StringUtils.removePattern(line, "false,");
			if (line.endsWith("true")) line = StringUtils.removePattern(line, "true");
			if (line.endsWith("true,")) line = StringUtils.removePattern(line, "true,");
			if (line.endsWith("null")) line = StringUtils.removePattern(line, "null");
			if (line.endsWith("null,")) line = StringUtils.removePattern(line, "null,");
			if (line.length() > 0) newLine = "private String " + line + " = null;";
		}
		return newLine;
	}

	private static final String ALPHEBET = "abcdefghijklmnopqrstuvwxyz";
}
