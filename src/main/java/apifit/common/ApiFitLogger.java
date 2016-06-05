package apifit.common;

public class ApiFitLogger {

	public static void log(String s) {
		System.out.println(s);
	}
/*
	public static void logHeaders(Response resp) {
		Headers heads = resp.getHeaders();
		Iterator<Header> iter = heads.iterator();
		while(iter.hasNext()) {
			ApiFitLogger.log(iter.next().toString());
		}
	}
	*/
}
