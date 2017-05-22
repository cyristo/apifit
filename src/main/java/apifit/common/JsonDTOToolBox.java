package apifit.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import apifit.contract.IJsonDTO;
import apifit.json.JsonToolBox;

public class JsonDTOToolBox {

	public String populateJsonPayloadFromDTO(IJsonDTO dto, String payload) throws ApiFitException {

		//ArrayList<ValueField> table = getValueFieldsFromDTO(dto);
		HashMap<String, Object> hashTable = getValueFieldsFromDTO(dto);
		Set<String> keys = hashTable.keySet();
		
		//Enumeration<String> keys = hashTable.keys();
		String key;
		Object value;
		String level;
		JsonToolBox jsonToolBox = new JsonToolBox();

		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			key = (String) iterator.next();
			//key = (String) keys.nextElement();
			value = hashTable.get(key);
			level = StringUtils.substringAfter(key, "_");
			key = StringUtils.removeEnd(key, "_"+level);
			//System.out.println("key = " + key + " , value = " + value + ", level = " + level);
			payload = jsonToolBox.updateJsonPayload(payload, key, value, new Integer(level));
		}

		return payload;
	}

	public HashMap<String, Object> getValueFieldsFromDTO(IJsonDTO dto) throws ApiFitException {

		HashMap<String, Object> hashTable = new HashMap<String, Object>();		
		Method[] methods = dto.getClass().getMethods();
		int level = dto.getJsonNodeLevel();

		String key;
		Object obj;

		for (Method method : methods) {

			if (isGetter(method)) {
				if (isDTOGetter(method)) {
					try {
						IJsonDTO newDTO = (IJsonDTO) method.invoke(dto);
						if (newDTO != null) {
							hashTable.putAll(getValueFieldsFromDTO(newDTO));	
						}
					} catch (IllegalAccessException e) {
						throw new ApiFitException(e);
					} catch (IllegalArgumentException e) {
						throw new ApiFitException(e);
					} catch (InvocationTargetException e) {
						throw new ApiFitException(e);
					}
				}
				key = method.getName();
				try {
					obj = (Object) method.invoke(dto);
				} catch (IllegalAccessException e) {
					throw new ApiFitException(e);
				} catch (IllegalArgumentException e) {
					throw new ApiFitException(e);
				} catch (InvocationTargetException e) {
					throw new ApiFitException(e);
				}
				if (obj != null && key != "getClass" && key != "getJsonNodeLevel") {
					key = key + "_" + level;
					hashTable.put(extractFieldNameFromGetterMethodName(key), obj);
				}
			}
		}

		return hashTable;
	}


	public String extractFieldNameFromGetterMethodName(String fullMethodName) {
		String methodName;
		if (StringUtils.contains(fullMethodName, ".")) {
			methodName = StringUtils.substringAfterLast(fullMethodName, ".");
		} else {
			methodName = fullMethodName;
		}
		String fieldName = StringUtils.removeStart(methodName, "get");
		fieldName = StringUtils.removeEnd(fieldName, "()");
		String firstChar = StringUtils.left(fieldName, 1);
		return StringUtils.replaceOnce(fieldName, firstChar, StringUtils.lowerCase(firstChar));
	}


	public boolean isGetter(Method method) {
		boolean result = method.getName().startsWith("get")
				&& (method.getParameterTypes().length == 0)
				&& (!Void.class.equals(method.getReturnType()));
		return result;
	}

	public boolean isSetter(Method method) {
		boolean result = (method.getName().startsWith("set"))
				&& (method.getParameterTypes().length == 1);
		return result;
	}
	public boolean isDTOGetter(Method method) {
		boolean result = method.getName().startsWith("get")
				&& (method.getName().endsWith("DTO"))
				&& (method.getParameterTypes().length == 0);
		//&& (IDTO.class.equals(method.getReturnType()));
		return result;
	}

}
