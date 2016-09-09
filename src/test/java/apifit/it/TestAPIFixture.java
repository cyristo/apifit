package apifit.it;

import static apifit.common.ApiFitConstants.DELETE;
import static apifit.common.ApiFitConstants.GET;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.PUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import apifit.common.ApiFitException;
import apifit.fixture.APIFixture;

public class TestAPIFixture {

	//http://jsonplaceholder.typicode.com/users
	private static Process process = null;
	private String host = "localhost";
	private String port = "3000";
	private String path = "/myjsondb";		

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		SocketAddress sockaddr = new InetSocketAddress("localhost", 3000);
		// Create your socket
		Socket socket = new Socket();
		boolean online = true;
		// Connect with 10 s timeout
		try {
		    socket.connect(sockaddr, 10000);
		} catch (SocketTimeoutException stex) {
		    // treating timeout errors separately from other io exceptions
		    // may make sense
		    online=false;
		} catch (IOException iOException) {
		    online = false;    
		} finally {
		    // As the close() operation can also throw an IOException
		    // it must caught here
		    try {
		        socket.close();
		    } catch (IOException ex) {
		        // feel free to do something moderately useful here, eg log the event
		    }

		}
		// Now, in your initial version all kinds of exceptions were swallowed by
		// that "catch (Exception e)".  You also need to handle the IOException
		// exec() could throw:
		if(!online){
			ClassLoader classLoader = TestAPIFixture.class.getClassLoader();
			File dbFile = new File(classLoader.getResource("start-json-server.bat").getFile());
			String dbFileName = dbFile.getParent()+"\\start-json-server.bat";
			String cmd = "cmd /c start ";
			process = Runtime.getRuntime().exec(cmd + dbFileName);
			Thread.sleep(3000);
		}  

	}

	@Before
	public void setUpTest() throws IOException, InterruptedException, ApiFitException {
		Thread.sleep(3000);
		ClassLoader classLoader = TestAPIFixture.class.getClassLoader();
		File fileSave = new File(classLoader.getResource("db.json_save").getFile());
		File fileDB = new File(fileSave.getParent(), "db.json");
		FileUtils.copyFile(fileSave, fileDB);
	}

	@AfterClass
	public static void tearDown() throws IOException, InterruptedException {
		if (process != null) process.destroy();
		ClassLoader classLoader = TestAPIFixture.class.getClassLoader();
		File fileSave = new File(classLoader.getResource("db.json_save").getFile());
		File fileDB = new File(fileSave.getParent(), "db.json");
		FileUtils.copyFile(fileSave, fileDB);
	}

	@Test
	public void get() throws InterruptedException {

		//act
		APIFixture fixture = new APIFixture(GET, "http", host, port, path);
		fixture.set("APIFIT:CHECK_STATUS", "200");
		fixture.execute();

		//assert
		assertEquals("OK", fixture.executionStatus());
		assertEquals("200", fixture.get("APIFIT:STATUS_CODE"));
		assertEquals("", fixture.executionErrorMessage());
		assertTrue(fixture.executionTime() < 10000);
		assertEquals(new Integer(10), new Integer(fixture.get("APIFIT:COUNT(\"id\")")));
		assertEquals("Leanne Graham", fixture.get("$.[0].name"));
		assertEquals("Victor Plains", fixture.get("$.[1].address.street"));
		assertEquals("10", fixture.get("$.length()"));

	}

	@Test 	
	public void delete() throws InterruptedException {

		//act
		APIFixture fixture = new APIFixture(DELETE, "http", host, port, path+"/1");
		fixture.execute();

		//assert
		assertEquals("OK", fixture.executionStatus());
		assertEquals(new Integer(200), fixture.statusCode());
		assertEquals("", fixture.executionErrorMessage());
		assertTrue(fixture.executionTime() < 10000);

		//act again
		Thread.sleep(1000);
		fixture = new APIFixture(GET, "http", host, port, path);
		fixture.set("APIFIT:CHECK_STATUS", "200");
		fixture.execute();

		//assert
		assertEquals(new Integer(9), new Integer(fixture.get("APIFIT:COUNT(\"id\")")));
		assertEquals("200", fixture.get("APIFIT:STATUS_CODE"));
		assertEquals("Ervin Howell", fixture.get("$.[0].name"));
		assertEquals("Douglas Extension", fixture.get("$.[1].address.street"));
		assertEquals("9", fixture.get("$.length()"));

	}

	@Test 	
	public void put() throws IOException, InterruptedException {

		//arrange
		String payload = getJsonUser();

		//act
		APIFixture fixture = new APIFixture(PUT, "http", host, port, path+"/1");
		fixture.set("APIFIT:PAYLOAD", payload);
		fixture.execute();

		//assert
		assertEquals("OK", fixture.executionStatus());
		assertEquals(new Integer(200), fixture.statusCode());
		assertEquals("200", fixture.get("APIFIT:STATUS_CODE"));
		assertEquals("", fixture.executionErrorMessage());
		assertTrue(fixture.executionTime() < 10000);

		//act again
		Thread.sleep(1000);
		fixture = new APIFixture(GET, "http", host, port, path);
		fixture.set("APIFIT:CHECK_STATUS", "200");
		fixture.execute();

		//assert
		assertEquals(new Integer(10), new Integer(fixture.get("APIFIT:COUNT(\"id\")")));
		assertEquals("200", fixture.get("APIFIT:STATUS_CODE"));
		assertEquals("Stock", fixture.get("$.[0].name"));
		assertEquals("Victor Plains", fixture.get("$.[1].address.street"));
		assertEquals("10", fixture.get("$.length()"));

	}

	@Test 	
	public void post() throws IOException, InterruptedException {

		//arrange
		String payload = getJsonUser();

		//act
		APIFixture fixture = new APIFixture(POST, "http", host, port, path);
		fixture.set("APIFIT:PAYLOAD", payload);
		fixture.set("APIFIT:CHECK_STATUS", "201");
		fixture.execute();

		//assert
		assertEquals("OK", fixture.executionStatus());
		assertEquals("201", fixture.get("APIFIT:STATUS_CODE"));
		assertEquals("", fixture.executionErrorMessage());
		assertTrue(fixture.executionTime() < 10000);

		//act again
		Thread.sleep(1000);
		path = "/myjsondb";
		fixture = new APIFixture(GET, "http", host, port, path);
		fixture.set("APIFIT:CHECK_STATUS", "200");
		fixture.execute();

		//assert
		assertEquals(new Integer(11), new Integer(fixture.get("APIFIT:COUNT(\"id\")")));
		assertEquals("Leanne Graham", fixture.get("$.[0].name"));
		assertEquals("Victor Plains", fixture.get("$.[1].address.street"));
		assertEquals("11", fixture.get("$.length()"));
		assertEquals("Stock", fixture.get("$.[10].name"));
		assertEquals("42, Java Street", fixture.get("$.[10].address.street"));

	}

	private String getJsonUser() throws IOException {
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("user.json");
		String payload = IOUtils.toString(stream);
		stream.close();
		return payload;
	}
}
