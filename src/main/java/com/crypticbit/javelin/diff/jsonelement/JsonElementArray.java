package com.crypticbit.javelin.diff.jsonelement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crypticbit.javelin.diff.DifferFactoryElement;
import com.crypticbit.javelin.diff.SequenceDiff;
import com.crypticbit.javelin.diff.list.ListDelta;
import com.crypticbit.javelin.diff.list.ListSequenceDiff;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JsonElementArray implements DifferFactoryElement {

    @Override
    public SequenceDiff createApplicator() {
	return new SequenceDiff<JsonElement, ListDelta>() {

	    private ListSequenceDiff lsd = new ListSequenceDiff();

	    @Override
	    public void addDelta(JsonElement parent, JsonElement child, Object branch) {
		lsd.addDelta(convert(parent), convert(child), branch);
	    }

	    @Override
	    public JsonArray apply(JsonElement value) {
		return convert(lsd.apply(convert(value)));
	    }

	    @Override
	    protected ListDelta createDelta(JsonElement parent, JsonElement child, Object branch) {
		throw new Error();
	    }

	    private List<JsonElement> convert(JsonElement value) {
		List<JsonElement> list = new ArrayList<>();
		for (JsonElement o : value.getAsJsonArray()) {
		    list.add(o);
		}
		return list;
	    }

	    private JsonArray convert(List<JsonElement> list) {
		JsonArray ja = new JsonArray();
		for (JsonElement o : list) {
		    ja.add(o);
		}
		return ja;
	    }

	};
    }

    @Override
    public boolean supports(Object object) {
	return (object instanceof JsonElement && ((JsonElement) object).isJsonArray());
    }

}
