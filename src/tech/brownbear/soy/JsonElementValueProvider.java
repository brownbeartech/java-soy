package tech.brownbear.soy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jbcsrc.api.RenderResult;

import javax.annotation.Nonnull;
import java.util.Map;

public class JsonElementValueProvider extends SoyAbstractCachingValueProvider {
    private final JsonElement json;

    public JsonElementValueProvider(JsonElement json) {
        this.json = json;
    }

    @Override
    protected SoyValue compute() {
        return parse(json);
    }

    @Nonnull
    @Override
    public RenderResult status() {
        return RenderResult.done();
    }

    public static SoyMapData parse(JsonElement json) {
        if (json.isJsonObject()) {
            return parse(json.getAsJsonObject(), new SoyMapData());
        }
        return null;
    }

    private static SoyMapData parse(JsonObject json, SoyMapData data) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    data.put(key, primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    data.put(key, resolveLazyNumber(primitive.getAsNumber()));
                } else if (primitive.isString()) {
                    data.put(key, primitive.getAsString());
                }
            } else if (element.isJsonArray()) {
                data.put(key, parse(element.getAsJsonArray()));
            } else if (element.isJsonObject()) {
                data.put(key, parse(element.getAsJsonObject(), new SoyMapData()));
            }
        }
        return data;
    }

    private static SoyListData parse(JsonArray array) {
        SoyListData data = new SoyListData();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    data.add(primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    data.add(resolveLazyNumber(primitive.getAsNumber()));
                } else if (primitive.isString()) {
                    data.add(primitive.getAsString());
                }
            } else if (element.isJsonArray()) {
                data.add(parse(element.getAsJsonArray()));
            } else if (element.isJsonObject()) {
                data.add(parse(element.getAsJsonObject(), new SoyMapData()));
            }
        }
        return data;
    }

    // TODO support long and double
    private static Number resolveLazyNumber(Number number) {
        if (number instanceof LazilyParsedNumber) {
            LazilyParsedNumber lazy = (LazilyParsedNumber) number;
            if (lazy.toString().contains(".")) {
                return lazy.floatValue();
            }
            return lazy.intValue();
        }
        return number;
    }
}
