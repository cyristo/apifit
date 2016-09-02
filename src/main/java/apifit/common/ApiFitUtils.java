package apifit.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
