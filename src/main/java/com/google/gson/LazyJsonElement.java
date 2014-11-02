package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map.Entry;

import com.crypticbit.javelin.store.Key;

public class LazyJsonElement extends JsonElement {

    private JsonElement backingElement;
    private Key key;
    private AddressToJsonElementWrapper wrapper;
    
    public LazyJsonElement(Key key, AddressToJsonElementWrapper wrapper) {
	this.key = key;
	this.wrapper = wrapper;
    }

    @Override
    public JsonElement deepCopy() {
	return getBackingElement().deepCopy();
    }

    @Override
    public boolean isJsonArray() {
	
	return getBackingElement().isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
	
	return getBackingElement().isJsonObject();
    }

    @Override
    public boolean isJsonPrimitive() {
	
	return getBackingElement().isJsonPrimitive();
    }

    @Override
    public boolean isJsonNull() {
	
	return getBackingElement().isJsonNull();
    }

    @Override
    public JsonObject getAsJsonObject() {
	JsonObject result = getBackingElement().getAsJsonObject();
	for(Entry<String, JsonElement> entry : result.entrySet()) {
	    entry.setValue(wrapper.unwrap(entry.getValue()));
	}
	return result;
    }

    @Override
    public JsonArray getAsJsonArray() {
	
	JsonArray original = getBackingElement().getAsJsonArray();
	JsonArray replace = new JsonArray();
	for(JsonElement element : original) {
	    replace.add(wrapper.unwrap(element));
	}
	return replace;
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
	return getBackingElement().getAsJsonPrimitive();
    }

    @Override
    public JsonNull getAsJsonNull() {
	
	return getBackingElement().getAsJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
	
	return getBackingElement().getAsBoolean();
    }

    @Override
    Boolean getAsBooleanWrapper() {
	
	return getBackingElement().getAsBooleanWrapper();
    }

    @Override
    public Number getAsNumber() {
	
	return getBackingElement().getAsNumber();
    }

    @Override
    public String getAsString() {
	
	return getBackingElement().getAsString();
    }

    @Override
    public double getAsDouble() {
	
	return getBackingElement().getAsDouble();
    }

    @Override
    public float getAsFloat() {
	
	return getBackingElement().getAsFloat();
    }

    @Override
    public long getAsLong() {
	
	return getBackingElement().getAsLong();
    }

    @Override
    public int getAsInt() {
	
	return getBackingElement().getAsInt();
    }

    @Override
    public byte getAsByte() {
	
	return getBackingElement().getAsByte();
    }

    @Override
    public char getAsCharacter() {
	
	return getBackingElement().getAsCharacter();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
	
	return getBackingElement().getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
	
	return getBackingElement().getAsBigInteger();
    }

    @Override
    public short getAsShort() {
	
	return getBackingElement().getAsShort();
    }

    private JsonElement getBackingElement() {
	if(backingElement == null)
	{
		backingElement = wrapper.wrap(key);
	}
	return backingElement;
    }

    
    
    

}
