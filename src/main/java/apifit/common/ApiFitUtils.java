package apifit.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiFitUtils {

	public static String getFileContent(String baseDir, String fileName) throws ApiFitException {
		String content = null;
		try {
			content = new String(Files.readAllBytes(Paths.get(baseDir + fileName)));
		} catch (IOException e) {
			throw new ApiFitException(e);
		}
		return content;
	}
	/*
	public static String getDayPlusXDay(String date) {
		System.out.println(LocalTime.now());
		return null;
	}
*/
}
