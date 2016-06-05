package apifit.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import apifit.common.TestSessionCache;

public class TestTheCacheSystem {

	@Test
	public void executionCacheIsWorkingFIne() {
		
		//Arrange
		TestSessionCache testExecutionCache = TestSessionCache.getInstance();
		String obj1 = "obj1";
		String obj2 = "obj2";
		String obj3 = "obj3";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		
		//Act
		testExecutionCache.addObjectInTestSession(key1, obj1);
		testExecutionCache.addObjectInTestSession(key2, obj2);
		testExecutionCache.addObjectInTestSession(key3, obj3);
		
		//Assert
		assertEquals(3, testExecutionCache.getCacheSize());
		assertNull(testExecutionCache.getObjectInTestSession("unknown_key"));
		assertEquals(obj1, testExecutionCache.getObjectInTestSession(key1));
		
		testExecutionCache.removeObjectInTestSession(key1);
		assertEquals(2, testExecutionCache.getCacheSize());
		assertEquals(obj2, testExecutionCache.getObjectInTestSession(key2));
		
		testExecutionCache.updateObjectInTestSession(key2, "new_value");
		assertEquals("new_value", testExecutionCache.getObjectInTestSession(key2));
		assertEquals(2, testExecutionCache.getCacheSize());
		
		testExecutionCache.removeObjectInTestSession("unknown_key");
		assertEquals(2, testExecutionCache.getCacheSize());
		
		testExecutionCache.addOrUpdateObjectInTestSession("new_key", "new_value1");
		assertEquals(3, testExecutionCache.getCacheSize());
		testExecutionCache.addOrUpdateObjectInTestSession("new_key", "new_value2");
		assertEquals(3, testExecutionCache.getCacheSize());
		assertEquals("new_value2", testExecutionCache.getObjectInTestSession("new_key"));
		
		testExecutionCache.clearCache();
		assertEquals(0, testExecutionCache.getCacheSize());
		
	}
	
	@Test
	public void removeAllObjectWithKey() {
		
		TestSessionCache testExecutionCache = TestSessionCache.getInstance();
		String obj1 = "obj1";
		String obj2 = "obj2";
		String obj3 = "obj3";
		String obj4 = "obj3";
		String obj5 = "obj3";
		String key1 = "keyToRemove1";
		String key2 = "keyToRemove2";
		String key3 = "keyToRemove3";
		String key4 = "keyNotToRemove1";
		String key5 = "keyNotToRemove2";
		
		testExecutionCache.addObjectInTestSession(key1, obj1);
		testExecutionCache.addObjectInTestSession(key2, obj2);
		testExecutionCache.addObjectInTestSession(key3, obj3);
		testExecutionCache.addObjectInTestSession(key4, obj4);
		testExecutionCache.addObjectInTestSession(key5, obj5);
		
		assertEquals(5, testExecutionCache.getCacheSize());
		
		testExecutionCache.removeAllObjectInTestSessionStartingWithKey("keyToRemove");
		assertEquals(2, testExecutionCache.getCacheSize());
		
		testExecutionCache.clearCache();
		assertEquals(0, testExecutionCache.getCacheSize());
	}
	
	
}
