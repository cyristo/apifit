package apifit.json;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
//import com.jayway.restassured.path.json.JsonPath;
//import com.jayway.jsonpath.*;

import apifit.common.ApiFitException;
import apifit.common.ApiFitLogger;
import apifit.common.ApiFitUtils;

public class JsonToolBox {

	/*
	 * Keeping in mind that only numbers, strings, null, objects and arrays are valid JSON primitives
	 */
	private int payLoadlevel = 0;

	public Object getJsonParamValue(String jsonPayload, String name) {
		Object obj = null;
		try {
			if (name.startsWith("$")) {
				obj = JsonPath.read(jsonPayload, name);
			} else {
				com.jayway.restassured.path.json.JsonPath jsonPath = new com.jayway.restassured.path.json.JsonPath(jsonPayload);
				obj = jsonPath.get(name);			
			}

		} catch (IllegalArgumentException e) {
			obj = "JSON PARSING ERROR : " + e.getMessage();
			ApiFitLogger.log(obj+"");
			//throw e;
		} catch (PathNotFoundException e1) {
			obj = "JSON PARSING ERROR : " + e1.getMessage();
			ApiFitLogger.log(obj+"");
			//throw e1;
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
		JsonNode updatedJson = JsonPath.using(configuration).parse(jsonPayload).set(fieldPath, newValue).json();
		return updatedJson.toString();
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
	public String addNodeToPayload(String jsonPayload, String fieldName, String fieldValue) throws ApiFitException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;

		try {
			rootNode = mapper.readTree(jsonPayload);
		} catch (Exception e) {
			throw new ApiFitException(e);
		}

		ObjectNode node = (ObjectNode) rootNode;
		node.putObject(fieldName);
		node.put(fieldName, fieldValue);

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
				if (node.get(fieldName).getNodeType().equals(JsonNodeType.NUMBER)) {
					if (ApiFitUtils.isInteger(newValue.toString()))  {
						node.put(fieldName, new Integer(newValue.toString()));
					} else {
						node.put(fieldName, new Double(newValue.toString()));
					}
				} else {
					node.put(fieldName, new String((String) newValue));					
				}
			}
			return;
		}

		// Now, recursively invoke this method on all properties
		for (JsonNode child : parent) {
			change(child, fieldName, newValue, levelOfUpdate);
		}
	}
	private void change2(JsonNode parent, String fieldPath, Object newValue) {

		JsonNode node = ((ObjectNode) parent).findParent(fieldPath);
		System.out.println(node);
		/*
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
				if (node.get(fieldName).getNodeType().equals(JsonNodeType.NUMBER)) {
					if (ApiFitUtils.isInteger(newValue.toString()))  {
						node.put(fieldName, new Integer(newValue.toString()));
					} else {
						node.put(fieldName, new Double(newValue.toString()));
					}
				} else {
					node.put(fieldName, new String((String) newValue));					
				}
			}
			*/
			return;
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

	private static final Configuration configuration = Configuration.builder()
			.jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider())
			.build();
}
