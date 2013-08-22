package com.crypticbit.diff.demo.swing.contacts;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * <p>
 * Provides the model for translating JsonElement into JTree data nodes. This class is not thread safe.
 * </p>
 * 
 * @author Stephen Owens
 *         <p>
 *         Provides the model for translating JsonElement into JTree data nodes.
 *         </p>
 *         <p>
 *         Copyright 2011 Stephen P. Owens : steve@doitnext.com
 *         </p>
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 *         compliance with the License. You may obtain a copy of the License at
 *         </p>
 *         <p>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         </p>
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software distributed under the License is
 *         distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 *         the License for the specific language governing permissions and limitations under the License.
 *         </p>
 */
public class JsonJTreeNode extends DefaultMutableTreeNode {
    /**
     * Using default serial id.
     */
    private static final long serialVersionUID = 1L;

    final DataType dataType;;
    final int index;
    String fieldName;
    final String value;

    /**
     * @param fieldName
     *            - name of field if applicable or null
     * @param index
     *            - index of element in the array or -1 if not part of an array
     * @param jsonElement
     *            - element to represent
     */
    public JsonJTreeNode(String fieldName, int index, JsonElement jsonElement) {
	this.index = index;
	this.fieldName = fieldName;
	if (jsonElement.isJsonArray()) {
	    this.dataType = DataType.ARRAY;
	    this.value = jsonElement.toString();
	    populateChildren(jsonElement);
	}
	else if (jsonElement.isJsonObject()) {
	    this.dataType = DataType.OBJECT;
	    this.value = jsonElement.toString();
	    populateChildren(jsonElement);
	}
	else if (jsonElement.isJsonPrimitive()) {
	    this.dataType = DataType.VALUE;
	    this.value = jsonElement.toString();
	}
	else if (jsonElement.isJsonNull()) {
	    this.dataType = DataType.VALUE;
	    this.value = jsonElement.toString();
	}
	else {
	    throw new IllegalArgumentException("jsonElement is an unknown element type.");
	}

    }

    public JsonElement asJsonElement() {
	StringBuilder sb = new StringBuilder();
	buildJsonString(sb);
	String json = sb.toString().trim();
	if (json.startsWith("{") || json.startsWith("[")) {
	    return new JsonParser().parse(sb.toString());
	}
	else {
	    // Safety check the JSON, if it is of a named value object
	    // We cheat a little if it is an orphan name value pair then
	    // if we wrap it in {} chars it will parse if it isn't the parse
	    // fails.
	    String testValue = "{" + json + "}";
	    try {
		JsonElement wrapperElt = new JsonParser().parse(testValue);
		JsonObject obj = (JsonObject) wrapperElt;
		Iterator<Entry<String, JsonElement>> it = obj.entrySet().iterator();
		Entry<String, JsonElement> entry = it.next();
		return entry.getValue();
	    }
	    catch (JsonSyntaxException jse) {
		JsonElement rawElement = new JsonParser().parse(json);
		return rawElement;
	    }
	}
    }

    @Override
    public String toString() {
	switch (dataType) {
	case ARRAY:
	case OBJECT:
	    if (index >= 0) {
		return String.format("[%d] (%s)", index, dataType.name());
	    }
	    else if (fieldName != null) {
		return String.format("%s (%s)", fieldName, dataType.name());
	    }
	    else {
		return String.format("(%s)", dataType.name());
	    }
	default:
	    if (index >= 0) {
		return String.format("[%d] %s", index, value);
	    }
	    else if (fieldName != null) {
		return String.format("%s: %s", fieldName, value);
	    }
	    else {
		return String.format("%s", value);
	    }

	}
    }

    @SuppressWarnings("unchecked")
    private void buildJsonString(StringBuilder sb) {
	if (!(this.fieldName == null || this.fieldName.length() == 0)) {
	    sb.append("\"" + this.fieldName + "\":");
	}
	Enumeration children;
	switch (dataType) {
	case ARRAY:
	    sb.append("[");
	    children = this.children();
	    while (children.hasMoreElements()) {
		JsonJTreeNode child = (JsonJTreeNode) children.nextElement();
		child.buildJsonString(sb);
		if (children.hasMoreElements()) {
		    sb.append(",");
		}
	    }
	    sb.append("]");
	    break;
	case OBJECT:
	    sb.append("{");
	    children = this.children();
	    while (children.hasMoreElements()) {
		JsonJTreeNode child = (JsonJTreeNode) children.nextElement();
		child.buildJsonString(sb);
		if (children.hasMoreElements()) {
		    sb.append(",");
		}
	    }
	    sb.append("}");
	    break;
	default: {
	    // Use the JSON parser to parse the value for safety
	    JsonElement elt = new JsonParser().parse(this.value);
	    sb.append(elt.toString());
	}
	}
    }

    private void populateChildren(JsonElement myJsonElement) {
	switch (dataType) {
	case ARRAY:
	    int index = 0;
	    Iterator<JsonElement> it = myJsonElement.getAsJsonArray().iterator();
	    while (it.hasNext()) {
		JsonElement element = it.next();
		JsonJTreeNode childNode = new JsonJTreeNode(null, index, element);
		this.add(childNode);
		index++;
	    }
	    break;
	case OBJECT:
	    for (Entry<String, JsonElement> entry : myJsonElement.getAsJsonObject().entrySet()) {
		JsonJTreeNode childNode = new JsonJTreeNode(entry.getKey(), -1, entry.getValue());
		this.add(childNode);
	    }
	    break;
	default:
	    throw new IllegalStateException("Internal coding error this should never happen.");
	}
    }

    public enum DataType {
	ARRAY, OBJECT, VALUE
    }
}
