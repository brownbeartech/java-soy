package tech.brownbear.soy;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.template.soy.data.*;
import com.google.template.soy.data.restricted.*;

import java.util.Map;
import java.util.function.Function;

public class SoySerializer {
    private static final String ERROR_MSG = "Class '%s' cannot be serialized";

    public static final Map<Class<?>, Function<Object, SoyValueProvider>> PROVIDERS =
        ImmutableMap.<Class<?>, Function<Object, SoyValueProvider>>builder()
            .put(Map.class, o -> serialize((Map<?,?>) o))
            .put(Iterable.class, o -> serialize((Iterable<?>) o))
            .put(JsonElement.class, o -> new JsonElementValueProvider((JsonElement) o))
            .put(String.class, o -> StringData.forValue((String) o))
            .put(Integer.class, o -> IntegerData.forValue((Integer) o))
            .put(Long.class, o -> IntegerData.forValue((Long) o))
            .put(Double.class, o -> FloatData.forValue((Double) o))
            .put(Float.class, o -> FloatData.forValue((Float) o))
            .put(Boolean.class, o -> BooleanData.forValue((Boolean) o))
            .put(Object.class, o -> new ObjectValueProvider(o))
            .build();

    public static SoyDict serialize(Map<?,?> o) {
        return new BasicSoyDict(o);
    }

    public static SoyList serialize(Iterable<?> o) {
        return new BasicSoyList(o);
    }

    public static SoyValue serialize(Object o) {
        return serializeLazy(o).resolve();
    }

    static SoyValueProvider serializeLazy(Object o) {
        if (o == null) {
            return NullData.INSTANCE;
        }
        if (o instanceof SoyValueProvider) {
            return (SoyValueProvider) o;
        }
        for (Class<?> c : PROVIDERS.keySet()) {
            if (c.isInstance(o)) {
                return PROVIDERS.get(c).apply(o);
            }
        }
        throw new AssertionError(String.format(ERROR_MSG, o.getClass().getName()));
    }
}
