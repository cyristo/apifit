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


	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
		
}
