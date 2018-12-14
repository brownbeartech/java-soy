package(default_visibility = ["//visibility:public"])

java_import(
    name = "jars",
    jars = glob(["jars/**/*.jar"]),
)

java_library(
    name = "gson",
    exports = [
      "@com_google_code_gson//jar",
    ]
)

java_library(
    name = "closure_templates",
    exports = [
        "@com_google_template_soy//jar",
        "@com_google_inject_guice_multibindings//jar",
        "@com_google_inject_guice//jar",
        "@com_google_inject_assistedinject//jar",
        "@com_google_protobuf_3.3//jar",
        "@com_google_common_html_types//jar",
        ":gson",
        ":jars",
    ]
)

java_library(
    name = "json",
    exports = [
        "@org_json//jar",
    ],
)

java_library(
    name = "guava",
    exports = [
        "@com_google_guava//jar",
        "@org_json//jar",
    ],
)

java_library(
    name = "logging",
    exports = [
        "@org_slf4j_api//jar",
    ],
)

java_library(
    name = "soy",
    srcs = glob(["tech/brownbear/soy/*.java"]),
    deps = [
        ":closure_templates",
        ":guava",
        ":logging",
    ],
)