package com.crypticbit.javelin.diff.jsonelement;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.map.MapDelta;
import com.crypticbit.javelin.diff.map.MapSequenceDiff;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonElementMap implements DifferFactoryElement {

    @Override
    public SequenceDiff<JsonElement, MapDelta<JsonElement>> createApplicator() {
	return new SequenceDiff<JsonElement, MapDelta<JsonElement>>() {

	    private MapSequenceDiff msd = new MapSequenceDiff();

	    @Override
	    public void addDelta(JsonElement parent, JsonElement child, Object branch) {
		msd.addDelta(convert(parent), convert(child), branch);
	    }

	    @Override
	    public JsonObject apply(JsonElement value) {
		return convert(msd.apply(convert(value)));
	    }

	    @Override
	    protected MapDelta createDelta(JsonElement parent, JsonElement child, Object branch) {
		throw new Error();
	    }

	    private Map<String, JsonElement> convert(JsonElement value) {
		Map<String, JsonElement> map = new HashMap<>();
		for (Entry<String, JsonElement> o : value.getAsJsonObject().entrySet()) {
		    map.put(o.getKey(), o.getValue());
		}
		return map;
	    }

	    private JsonObject convert(Map<String, JsonElement> map) {
		JsonObject jo = new JsonObject();
		for (Entry<String, JsonElement> o : map.entrySet()) {
		    jo.add(o.getKey(), o.getValue());
		}
		return jo;
	    }

	};
    }

    @Override
    public boolean supports(Object object) {
	return (object instanceof JsonElement && ((JsonElement) object).isJsonObject());
    }

}
