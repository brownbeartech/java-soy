package tech.brownbear.templates;

import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.DictImpl;
import com.google.template.soy.data.restricted.StringData;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicSoyDict extends SoyAbstractValue implements SoyDict {
    private final Map<String, SoyValueProvider> providers = new HashMap<>();

    public BasicSoyDict(Map<?, ?> data) {
        putAll(data);
    }

    private void put(String k, SoyValueProvider p) {
        providers.put(k, p);
    }

    private void putAll(Map<?, ?> providers) {
        providers.forEach((k, v) -> put(String.valueOf(k), SoySerializer.serializeLazy(v)));
    }

    @Nonnull
    @Override
    public Map<String, ? extends SoyValueProvider> asJavaStringMap() {
        return new HashMap<>(providers);
    }

    @Nonnull
    @Override
    public Map<String, ? extends SoyValue> asResolvedJavaStringMap() {
        return providers.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().resolve()));
    }

    @Override
    public int getItemCnt() {
        return providers.keySet().size();
    }

    @Nonnull
    @Override
    public Iterable<? extends SoyValue> getItemKeys() {
        return providers.keySet().stream()
            .map(StringData::forValue)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasItem(SoyValue soyValue) {
        return hasField(soyValue.stringValue());
    }

    @Override
    public SoyValue getItem(SoyValue soyValue) {
        return getItemProvider(soyValue).resolve();
    }

    @Override
    public SoyValueProvider getItemProvider(SoyValue soyValue) {
        return DictImpl.forProviderMap(providers).getItemProvider(soyValue);
    }

    @Override
    public boolean hasField(String s) {
        return providers.containsKey(s);
    }

    @Override
    public SoyValue getField(String s) {
        return getFieldProvider(s).resolve();
    }

    @Override
    public SoyValueProvider getFieldProvider(String s) {
        return DictImpl.forProviderMap(providers).getFieldProvider(s);
    }

    @Override
    public boolean coerceToBoolean() {
        return DictImpl.forProviderMap(providers).coerceToBoolean();
    }

    @Override
    public String coerceToString() {
        return DictImpl.forProviderMap(providers).coerceToString();
    }

    @Override
    public void render(Appendable appendable) throws IOException {
        DictImpl.forProviderMap(providers).render(appendable);
    }

    @Override
    public boolean equals(Object o) {
        return providers.equals(o);
    }
}
