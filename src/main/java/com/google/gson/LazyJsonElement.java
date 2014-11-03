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
    public BigDecimal getAsBigDecimal() {

	return getBackingElement().getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {

	return getBackingElement().getAsBigInteger();
    }

    @Override
    public boolean getAsBoolean() {

	return getBackingElement().getAsBoolean();
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
    public double getAsDouble() {

	return getBackingElement().getAsDouble();
    }

    @Override
    public float getAsFloat() {

	return getBackingElement().getAsFloat();
    }

    @Override
    public int getAsInt() {

	return getBackingElement().getAsInt();
    }

    @Override
    public JsonArray getAsJsonArray() {

	JsonArray original = getBackingElement().getAsJsonArray();
	JsonArray replace = new JsonArray();
	for (JsonElement element : original) {
	    replace.add(wrapper.unwrap(element));
	}
	return replace;
    }

    @Override
    public JsonNull getAsJsonNull() {

	return getBackingElement().getAsJsonNull();
    }

    @Override
    public JsonObject getAsJsonObject() {
	JsonObject result = getBackingElement().getAsJsonObject();
	for (Entry<String, JsonElement> entry : result.entrySet()) {
	    entry.setValue(wrapper.unwrap(entry.getValue()));
	}
	return result;
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
	return getBackingElement().getAsJsonPrimitive();
    }

    @Override
    public long getAsLong() {

	return getBackingElement().getAsLong();
    }

    @Override
    public Number getAsNumber() {

	return getBackingElement().getAsNumber();
    }

    @Override
    public short getAsShort() {

	return getBackingElement().getAsShort();
    }

    @Override
    public String getAsString() {

	return getBackingElement().getAsString();
    }

    @Override
    public boolean isJsonArray() {

	return getBackingElement().isJsonArray();
    }

    @Override
    public boolean isJsonNull() {

	return getBackingElement().isJsonNull();
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
    Boolean getAsBooleanWrapper() {

	return getBackingElement().getAsBooleanWrapper();
    }

    private JsonElement getBackingElement() {
	if (backingElement == null) {
	    backingElement = wrapper.wrap(key);
	}
	return backingElement;
    }

    // FIXME - what if elements modified?
    @Override
    public int hashCode() {
	return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LazyJsonElement other = (LazyJsonElement) obj;
	if (key == null) {
	    if (other.key != null)
		return false;
	}
	else if (!key.equals(other.key))
	    return false;
	return true;
    }
    
    

}
