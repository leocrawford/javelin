package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map.Entry;

import com.google.gson.JsonElement;

public class LazyJsonElement extends JsonElement {

    private JsonElement backingElement;
    private AddressToJsonElementWrapper wrapper;
    
    public LazyJsonElement(JsonElement backingElement, AddressToJsonElementWrapper wrapper) {
	this.backingElement = backingElement;
	this.wrapper = wrapper;
    }

    @Override
    JsonElement deepCopy() {
//	return new LazyJsonElement(backingElement.deepCopy(),wrapper);
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isJsonArray() {
	
	return backingElement.isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
	
	return backingElement.isJsonObject();
    }

    @Override
    public boolean isJsonPrimitive() {
	
	return backingElement.isJsonPrimitive();
    }

    @Override
    public boolean isJsonNull() {
	
	return backingElement.isJsonNull();
    }

    @Override
    public JsonObject getAsJsonObject() {
	JsonObject result = backingElement.getAsJsonObject();
	for(Entry<String, JsonElement> entry : result.entrySet()) {
	    entry.setValue(wrap(entry.getValue()));
	}
	return result;
    }

    private JsonElement wrap(JsonElement value) {
	return wrapper.wrap(value);
    }

    @Override
    public JsonArray getAsJsonArray() {
	
	JsonArray original = backingElement.getAsJsonArray();
	JsonArray replace = new JsonArray();
	for(JsonElement element : original) {
	    replace.add(wrap(element));
	}
	return replace;
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
	return backingElement.getAsJsonPrimitive();
    }

    @Override
    public JsonNull getAsJsonNull() {
	
	return backingElement.getAsJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
	
	return backingElement.getAsBoolean();
    }

    @Override
    Boolean getAsBooleanWrapper() {
	
	return backingElement.getAsBooleanWrapper();
    }

    @Override
    public Number getAsNumber() {
	
	return backingElement.getAsNumber();
    }

    @Override
    public String getAsString() {
	
	return backingElement.getAsString();
    }

    @Override
    public double getAsDouble() {
	
	return backingElement.getAsDouble();
    }

    @Override
    public float getAsFloat() {
	
	return backingElement.getAsFloat();
    }

    @Override
    public long getAsLong() {
	
	return backingElement.getAsLong();
    }

    @Override
    public int getAsInt() {
	
	return backingElement.getAsInt();
    }

    @Override
    public byte getAsByte() {
	
	return backingElement.getAsByte();
    }

    @Override
    public char getAsCharacter() {
	
	return backingElement.getAsCharacter();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
	
	return backingElement.getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
	
	return backingElement.getAsBigInteger();
    }

    @Override
    public short getAsShort() {
	
	return backingElement.getAsShort();
    }

    
    
    

}
