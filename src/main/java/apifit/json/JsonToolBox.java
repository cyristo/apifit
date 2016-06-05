package apifit.json;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.path.json.JsonPath;
//import com.jayway.jsonpath.*;

import apifit.common.ApiFitException;

public class JsonToolBox {

	/*
	 * Keeping in mind that only numbers, strings, null, objects and arrays are valid JSON primitives
	 */
	private int payLoadlevel = 0;

	public Object getJsonParamValue(String jsonPayload, String name) {
		Object obj = null;
		if (name.startsWith("$")) {
			obj = com.jayway.jsonpath.JsonPath.read(jsonPayload, name);
		} else {
			JsonPath jsonPath = new JsonPath(jsonPayload);
			obj = jsonPath.get(name);			
		}

		return obj;
	}

	public String getNodeFromPayload(String jsonPayload, String fieldName, int level) throws ApiFitException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;

		try {
			rootNode = mapper.readTree(jsonPayload);
		} catch (Exception e) {
			throw new ApiFitException(e);
		}

		return get(rootNode, fieldName);

	}

	public String updateJsonAttribute(String jsonPayload, String fieldPath, Object newValue) throws ApiFitException {
		String parent = StringUtils.substringBeforeLast(fieldPath, ".");
		String field = StringUtils.substringAfterLast(fieldPath, ".");
		return updateJsonPayload(jsonPayload, field, newValue, 0);
	}

	public String updateJsonPayload(String jsonPayload, String fieldName, Object newValue) throws ApiFitException {
		return updateJsonPayload(jsonPayload, fieldName, newValue, 0);
	}

	public String updateJsonPayload(String jsonPayload, String fieldName, Object newValue, int levelOfUpdate) throws ApiFitException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;

		try {
			rootNode = mapper.readTree(jsonPayload);
		} catch (Exception e) {
			throw new ApiFitException(e);
		}

		payLoadlevel = -1;
		change(rootNode, fieldName, newValue, levelOfUpdate);

		return rootNode.toString();
	}
	public String removeNodeFromPayload(String jsonPayload, String fieldName) throws ApiFitException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;

		try {
			rootNode = mapper.readTree(jsonPayload);
		} catch (Exception e) {
			throw new ApiFitException(e);
		}

		remove(rootNode, fieldName);

		return rootNode.toString();
	}

	private void change(JsonNode parent, String fieldName, Object newValue, int levelOfUpdate) {
		if (parent.isContainerNode()) {
			payLoadlevel++;
		}

		if (parent.has(fieldName) && (levelOfUpdate == 0 || levelOfUpdate == payLoadlevel)) {
			ObjectNode node = (ObjectNode) parent;
			if (newValue.getClass().equals(Integer.class)) {
				node.put(fieldName, new Integer((Integer) newValue));
			} else if (newValue.getClass().equals(Float.class)) {
				node.put(fieldName, new Float((Float) newValue));
			} else if (newValue.getClass().equals(Double.class)) {
				node.put(fieldName, new Double((Double) newValue));
			} else if (newValue.getClass().equals(ArrayList.class)) {
				ObjectMapper mapper = new ObjectMapper();
				ArrayNode array = mapper.valueToTree(newValue);
				node.putArray(fieldName).addAll(array);
			} else if (newValue.getClass().equals(Boolean.class)) {
				node.put(fieldName, new Boolean((Boolean) newValue));
			} else {
				node.put(fieldName, new String((String) newValue));
			}
			return;
		}

		// Now, recursively invoke this method on all properties
		for (JsonNode child : parent) {
			change(child, fieldName, newValue, levelOfUpdate);
		}
	}
	private void remove(JsonNode parent, String fieldName) {

		if (parent.has(fieldName)) {
			ObjectNode node = (ObjectNode) parent;
			node.remove(fieldName);
			return;
		}
		// Now, recursively invoke this method on all properties
		for (JsonNode child : parent) {
			remove(child, fieldName);
		}

	}

	private String get(JsonNode parent, String fieldName) {

		if (parent.has(fieldName)) {
			ObjectNode node = (ObjectNode) parent;
			return node.get(fieldName).asText();
		}
		// Now, recursively invoke this method on all properties
		for (JsonNode child : parent) {
			get(child, fieldName);
		}
		return "";
	}
	public String formatPretty(String txt) throws ApiFitException  {
		ObjectMapper mapper = new ObjectMapper();
		String ret = txt;
		try {
			Object json = mapper.readValue(txt, Object.class);
			ret = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new ApiFitException(e);
		} catch (IOException e) {
			throw new ApiFitException(e);
		}
		return ret;
		//mapper.defaultPrettyPrintingWriter().writeValueAsString(txt);
	}
}
