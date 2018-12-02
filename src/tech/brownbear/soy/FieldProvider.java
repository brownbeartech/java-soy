package tech.brownbear.templates;

import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.jbcsrc.api.RenderResult;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class FieldProvider extends SoyAbstractCachingValueProvider {
    private final Field field;
    private final Object obj;

    public FieldProvider(Field field, Object obj) {
        this.field = field;
        this.obj = obj;
        field.setAccessible(true);
    }

    @Override
    protected SoyValue compute() {
        try {
            return SoySerializer.serialize(field.get(obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public RenderResult status() {
        return RenderResult.done();
    }
}
