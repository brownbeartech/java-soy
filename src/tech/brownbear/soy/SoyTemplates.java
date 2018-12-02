package tech.brownbear.soy;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.shared.SoyGeneralOptions;
import com.google.template.soy.tofu.SoyTofu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SoyTemplates implements SoyTemplateRenderer {
    protected static Logger logger = LoggerFactory.getLogger(SoyTemplates.class);
    private static int RELOAD_INTERVAL_MILLIS = 1000;

    private final TemplateLoader loader;
    private SoyFileSet sfs;
    private SoyTofu tofu;

    public SoyTemplates(TemplateLoader loader) {
        this.loader = loader;
    }

    public String render(String template) {
        if (tofu == null) initialize();
        return tofu.newRenderer(template).render();
    }

    public String render(Map<String, Object> ij, String template, Map<String, Object> data) {
        if (tofu == null) initialize();
        return render(SoySerializer.serialize(ij), template, SoySerializer.serialize(data));
    }

    public String render(String template, Map<String, Object> args) {
        return render(template, SoySerializer.serialize(args));
    }

    public String render(String template, SoyRecord data) {
        if (tofu == null) initialize();
        return tofu.newRenderer(template).setData(data).render();
    }

    public String render(SoyRecord ij, String template, SoyRecord data) {
        if (tofu == null) initialize();
        return tofu.newRenderer(template)
            //.setIjData(ij)
            .setData(data)
            .render();
    }

    public void initialize() {
        logger.info("Initializing soy templates");
        loadTemplates();
        reloadOnInterval();
    }

    private void loadTemplates() {
        try {
            SoyFileSet.Builder builder = SoyFileSet.builder();
            List<URL> templates = loader.load();
            templates.forEach(builder::add);
            builder.setGeneralOptions(new SoyGeneralOptions());
            sfs = builder.build();
            tofu = sfs.compileToTofu();
        } catch (Exception e) {
            logger.info("Error building soy template", e);
        }
    }

    private void reloadOnInterval() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadTemplates();
            }
        }, RELOAD_INTERVAL_MILLIS, RELOAD_INTERVAL_MILLIS);
    }
}