package tech.brownbear.templates;

import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.ListImpl;
import com.google.template.soy.data.restricted.IntegerData;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BasicSoyList extends SoyAbstractValue implements SoyList {
    private final List<SoyValueProvider> providers = new ArrayList<>();

    public BasicSoyList(Iterable<?> data) {
        put(data);
    }

    private void put(Iterable<?> data) {
        Stream<?> stream;
        if (data instanceof List) {
            stream = ((List<?>) data).stream();
        } else {
            stream = StreamSupport.stream(data.spliterator(), false);
        }
        stream.map(SoySerializer::serializeLazy).forEach(providers::add);
    }

    @Override
    public int length() {
        return providers.size();
    }

    @Nonnull
    @Override
    public List<? extends SoyValueProvider> asJavaList() {
        return new ArrayList<>(providers);
    }

    @Nonnull
    @Override
    public List<? extends SoyValue> asResolvedJavaList() {
        return providers.stream().map(SoyValueProvider::resolve).collect(Collectors.toList());
    }

    @Override
    public SoyValue get(int index) {
        return getProvider(index).resolve();
    }

    @Override
    public SoyValueProvider getProvider(int index) {
        return providers.get(index);
    }

    @Override
    public int getItemCnt() {
        return length();
    }

    @Nonnull
    @Override
    public Iterable<? extends SoyValue> getItemKeys() {
        return IntStream.rangeClosed(0, length())
            .boxed()
            .map(IntegerData::forValue)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasItem(SoyValue key) {
        return providers.size() > key.integerValue();
    }

    @Override
    public SoyValue getItem(SoyValue key) {
        return getItemProvider(key).resolve();
    }

    @Override
    public SoyValueProvider getItemProvider(SoyValue key) {
        return ListImpl.forProviderList(providers).getItemProvider(key);
    }

    @Override
    public boolean coerceToBoolean() {
        return ListImpl.forProviderList(providers).coerceToBoolean();
    }

    @Override
    public String coerceToString() {
        return ListImpl.forProviderList(providers).coerceToString();
    }

    @Override
    public void render(Appendable appendable) throws IOException {
        ListImpl.forProviderList(providers).render(appendable);
    }

    @Override
    public boolean equals(Object o) {
        return providers.equals(o);
    }
}
