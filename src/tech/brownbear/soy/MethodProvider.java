package tech.brownbear.soy;

import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jbcsrc.api.RenderResult;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodProvider extends SoyAbstractCachingValueProvider {
    private final Method method;
    private final Object obj;

    public MethodProvider(Method method, Object obj) {
        this.method = method;
        this.obj = obj;
        method.setAccessible(true);
    }

    @Override
    protected SoyValue compute() {
        try {
            Object result = method.invoke(obj);
            return SoySerializer.serialize(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public RenderResult status() {
        return RenderResult.done();
    }
}
