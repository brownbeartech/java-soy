# Soy library for Java

[Google Closure Templates](https://developers.google.com/closure/templates/) is a great templating
system for Java that has prebuild libraries for loading and rendering templates. This library provides
two main functions, loading and caching the templates from their files and serializing Java objects
for use in the templates.

## Soy templates

Soy templates can be simple. Each `.soy` file has a unique namespace and can contain multiple templates.

```soy
{namespace your.template.namespace}

/**
 * Comment
 */
{template .template}
  Hello world
{/template}
```

A template can take arguments like a view model.

```soy
{namespace your.template.namespace}

/**
 * Comment
 * @param paramName Comment about param
 */
{template .template}
  {$paramName}
{/template}
```

And templates can make local or fully qualified calls to other templates that have
been bundled together.

```soy
{namespace your.template.namespace}

/**
 * Comment
 */
{template .templateA}
  {call .templateB /}
  {call your.template.namespace.templateB /}
{/template}

/**
 * Comment
 */
{template .templateB}
  Hello world
{/template}

```

## Loading and rendering templates

Check out a seperate library, [Java Resource Management](https://github.com/brownbeartech/java-resources),
for loading resorces in Java.

```java
    FallbackResourceFetcher fetcher = ...; // https://github.com/brownbeartech/java-resources
    TemplateLoader loader = () -> fetcher.findAll(p -> p.getFileName().toString.endsWith(".soy"));
    SoyTemplateRenderer renderer = new SoyTemplates(loader);
    String html = renderer.render("your.template.namespace.template");
```

An if the template expects arguments they can be provided as a map

```java
    Map<String, Object> args = new HashMap<>();
    args.put("paramName", "value");
    String html = renderer.render("your.template.namespace.template", args);
```
