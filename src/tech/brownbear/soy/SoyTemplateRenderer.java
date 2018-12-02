package tech.brownbear.templates;

import java.util.Collections;
import java.util.Map;

/**
 * Render some templates
 */
@FunctionalInterface
public interface SoyTemplateRenderer {
    default String render(String templateName, Map<String, Object> args) {
        return render(Collections.emptyMap(), templateName, args);
    }

    default String render(String templateName) {
        return render(templateName, Collections.emptyMap());
    }

    String render(Map<String, Object> ij, String templateName, Map<String, Object> args);
}