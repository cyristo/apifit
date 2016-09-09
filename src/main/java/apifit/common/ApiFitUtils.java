package apifit.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ApiFitUtils {

	public static String getFileContent(String baseDir, String fileName) throws ApiFitException {
		String content = null;
		
		try {
			 content = FileUtils.readFileToString(new File(baseDir + fileName), "UTF-8");
		} catch (IOException e) {
			throw new ApiFitException(e);
		}
		System.out.println(content);
		return content;
	}
	/*
	public static String getDayPlusXDay(String date) {
		System.out.println(LocalTime.now());
		return null;
	}
*/
}
