package tech.brownbear.soy;

import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jbcsrc.api.RenderResult;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectValueProvider extends SoyAbstractCachingValueProvider {
    private final Object obj;

    public ObjectValueProvider(Object obj) {
        this.obj = obj;
    }

    private SoyDict parse(Object obj) {
        Map<String, SoyAbstractCachingValueProvider> providers = new HashMap<>();
        // Recurse through superclasses
        List<Class> superClasses = new ArrayList<>();
        Class superClass = obj.getClass().getSuperclass();
        while (superClass != null) {
            superClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }
        superClasses.remove(Object.class);
        // Reverse so that overrides make sense
        Collections.reverse(superClasses);
        for (Class c : superClasses) {
            // Add all declared methods
            providers.putAll(getMethodProviders(c));
            // Add all fields
            providers.putAll(getFieldProviders(obj.getClass()));
        }

        // Add all declared methods
        providers.putAll(getMethodProviders(obj.getClass()));
        // Add all fields
        providers.putAll(getFieldProviders(obj.getClass()));

        return new BasicSoyDict(providers);
    }

    private Map<String, FieldProvider> getFieldProviders(Class c) {
        Map<String, FieldProvider> providers = new HashMap<>();
        for (Field f : c.getDeclaredFields()) {
            if (!f.isSynthetic()) {
                providers.put(f.getName(), new FieldProvider(f, obj));
            }
        }
        return providers;
    }

    private Map<String, MethodProvider> getMethodProviders(Class c) {
        Map<String, MethodProvider> providers = new HashMap<>();
        for (Method m : c.getDeclaredMethods()) {
            if (!m.isBridge() && !m.isSynthetic() && m.getParameterCount() == 0) {
                String name = getAccessorName(m);
                if (name != null) {
                    providers.put(name, new MethodProvider(m, obj));
                }
            }
        }
        return providers;
    }

    private static String getAccessorName(Method m) {
        String name = null;
        if (m.getName().startsWith("get")) {
            name = m.getName().substring(3);
        } else if (m.getName().startsWith("is")) {
            name = m.getName().substring(2);
        }
        return firstCharToLower(name);
    }

    private static String firstCharToLower(String str) {
        if (str != null) {
            if (str.length() > 1) {
                str = str.substring(0, 1).toLowerCase() + str.substring(1);
            } else {
                str = str.toLowerCase();
            }
        }
        return str;
    }

    @Override
    protected SoyValue compute() {
        return parse(obj).resolve();
    }

    @Nonnull
    @Override
    public RenderResult status() {
        return RenderResult.done();
    }
}