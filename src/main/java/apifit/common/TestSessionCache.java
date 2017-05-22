package apifit.common;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestSessionCache {

	private TestSessionCache() {
	}

	private ConcurrentHashMap<String, Object> hashTable;

	/** Instance unique non préinitialisée */
	private static TestSessionCache INSTANCE = null;

	/** Point d'accès pour l'instance unique du singleton */
	public synchronized static TestSessionCache getInstance() {	
		if (INSTANCE == null) { 	
			INSTANCE = new TestSessionCache();
			INSTANCE.initCache();
		}
		return INSTANCE;
	}

	private void initCache() {
		hashTable = new ConcurrentHashMap<String, Object>();
	}
	public void addObjectInTestSession(String key, Object obj) {
		hashTable.put(key, obj);
	}
	public void addOrUpdateObjectInTestSession(String key, Object obj) {
		if (hashTable.get(key) == null) {
			hashTable.put(key, obj);
		} else {
			hashTable.replace(key, obj);
		}
	}
	public void removeObjectInTestSession(String key) {
		hashTable.remove(key);
	}
	public void removeAllObjectInTestSessionStartingWithKey(String startingKey) {
		Set<String> set = hashTable.keySet();
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if (key.startsWith(startingKey)) {
					hashTable.remove(key);
				}
		}

	}
	public Object getObjectInTestSession(String key) {
		return hashTable.get(key);
	}
	public void updateObjectInTestSession(String key, Object obj) {
		hashTable.replace(key, obj);
	}
	public int getCacheSize() {
		return hashTable.size();
	}
	public void clearCache() {
		hashTable.clear();
	}

}
