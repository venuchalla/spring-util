package com.example.springutil.codegen;

import static java.nio.file.Path.of;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.SpringCodegen;

@Slf4j
public class CodegenTool {

  public static final String CODEGEN_API_ID_KEY = "codegen.api.ids";
  public static final String SPEC_KEY = "spec";
  public static final String OUTPUT_DIR_KEY = "output.dir";
  public static final String API_BASEPACKAGE_KEY = "api.basepackage";

  public static void main(String[] args) {
    log.info("Running CodegenTool");

    String basedir = args.length > 0 ? args[0] : System.getProperty("user.dir");
    String specDir =
        args.length > 1 ? args[1] : of(basedir, "src/main/resources/swagger").toString();
    String propPath =
        args.length > 2 ? args[2] : of(specDir, "code-gen/code-gen.properties").toString();
    String templatepath = args.length > 3 ? args[3] : of(specDir, "templates").toString();

    log.info("basedir : {}", basedir);
    log.info("specDir : {}", specDir);
    log.info("propPath : {}", propPath);
    log.info("templatepath : {}", templatepath);
    Path propertiesFilePath = Path.of(propPath);
    final Properties appproperties = new Properties();
    try (FileInputStream fis = new FileInputStream(propertiesFilePath.toFile())) {
      appproperties.load(fis);
    } catch (Exception e) {
      log.error("Error occurred while generating code", e);
      return;
    }
    final List<String> apiIds =
        Arrays.stream(appproperties.getProperty(CODEGEN_API_ID_KEY).split(","))
            .map(String::trim)
            .toList();
    for (String apiId : apiIds) {
      log.info("Processing apiId : {}", apiId);
      String spec = appproperties.getProperty(apiId + "." + SPEC_KEY);
      Path specPath = resolvePath(basedir, spec);
      String outputDir = appproperties.getProperty(apiId + "." + OUTPUT_DIR_KEY);
      Path outputDirPath = resolvePath(basedir, outputDir);
      String apiBasePackage = appproperties.getProperty(apiId + "." + API_BASEPACKAGE_KEY);
      log.info(
          "Generating code for apiId : {}, spec : {}, outputDir : {}, apiBasePackage : {}",
          apiId,
          spec,
          outputDir,
          apiBasePackage);
      if (!Files.isReadable(specPath)) {
        log.error("Spec {} is not readable", specPath);
        continue;
      }
      try {
        final Path outputDir1 = Files.createDirectories(outputDirPath);
        if (!Files.isDirectory(outputDir1) || !Files.isWritable(outputDir1)) {
          log.error("Output directory {} is not writable", outputDirPath);
          continue;
        }
      } catch (Exception e) {
        log.error("could not create out put directory", e);
        continue;
      }
      generateFeignClients(
          specPath,
          outputDirPath,
          templatepath,
          apiBasePackage,
          extractProperties(apiId, appproperties));
    }
  }

  public static void generateFeignClients(
      Path specPath,
      Path outputDir,
      String templatepath,
      String apiBasePackage,
      Properties customProperties) {
    log.info(
        "Generating Feign clients for spec : {}, outputDir : {}, templatepath : {}, apiBasePackage : {}",
        specPath,
        outputDir,
        templatepath,
        apiBasePackage);
    // Implement the logic to generate Feign clients using the provided spec, outputDir,
    // templatepath, and apiBasePackage
    try {
      final OpenAPI openAPI = new OpenAPIV3Parser().read(specPath.toString());

      final CustomSpringCodegen customSpringCodegen =
          new CustomSpringCodegen(
              Arrays.stream(customProperties.getProperty("mask", "").split(","))
                  .map(String::trim)
                  .collect(Collectors.toSet()),
              Arrays.stream(customProperties.getProperty("ignore", "").split(","))
                  .map(String::trim)
                  .collect(Collectors.toSet()));

      customSpringCodegen.setOutputDir(outputDir.toString());
      customSpringCodegen.setTemplateDir(templatepath);
      Map<String, String> addProps = getDefaultProperties(apiBasePackage);
      if (!addProps.isEmpty()) {
        log.info("Adding properties to custom Spring properties {}", addProps);
        customProperties.forEach(
            (key, value) -> {
              log.info("Adding property {} = {}", key, value);
              addProps.put(key.toString(), value.toString());
            });
      }
      customSpringCodegen.additionalProperties().putAll(addProps);
      ClientOptInput clientOptInput =
          new ClientOptInput().openAPI(openAPI).config(customSpringCodegen);

      new DefaultGenerator().opts(clientOptInput).generate();
      addJsonIncludeImports(outputDir);
    } catch (Exception e) {
      log.error("Error occurred while generating Feign clients", e);
    }
  }

  private static Path resolvePath(String basedir, String path) {
    Path candidate = Path.of(path);
    if (candidate.isAbsolute()) {
      return candidate;
    }
    return of(basedir, path);
  }

  private static void addJsonIncludeImports(Path outputDir) throws IOException {
    try (Stream<Path> paths = Files.walk(outputDir)) {
      paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .forEach(
              path -> {
                try {
                  String content = Files.readString(path);
                  if (!content.contains("@JsonInclude(")
                      || content.contains("import com.fasterxml.jackson.annotation.JsonInclude;")) {
                    return;
                  }
                  String importLine = "import com.fasterxml.jackson.annotation.JsonInclude;\n";
                  int insertIndex = content.indexOf("@");
                  if (insertIndex < 0) {
                    insertIndex = content.indexOf("public ");
                  }
                  if (insertIndex < 0) {
                    return;
                  }
                  String updatedContent =
                      content.substring(0, insertIndex)
                          + importLine
                          + content.substring(insertIndex);
                  Files.writeString(path, updatedContent);
                } catch (IOException e) {
                  log.error("Unable to patch generated imports in {}", path, e);
                }
              });
    }
  }

  private static Properties extractProperties(String apiId, Properties aProperties) {
    Properties appproperties = new Properties();
    aProperties.stringPropertyNames().stream()
        .filter(
            propertyName ->
                propertyName.startsWith(apiId)
                    && !propertyName.endsWith(SPEC_KEY)
                    && !propertyName.endsWith(API_BASEPACKAGE_KEY)
                    && !propertyName.endsWith(OUTPUT_DIR_KEY))
        .forEach(
            propertyName -> {
              String propertyValue = propertyName.replace(apiId + ".", "");
              appproperties.setProperty(propertyValue, aProperties.getProperty(propertyName));
            });

    return appproperties;
  }

  private static void generateFeignClientsuu() {
    CodegenConfigurator configurator = new CodegenConfigurator();

    configurator.setInputSpec(
        "/Users/venuchalla/Downloads/Development/BackEnd/resilience-example/src/main/resources/swagger/templates/demo.yaml");

    // Generator to use
    configurator.setGeneratorName("spring");

    configurator.setOutputDir(
        "/Users/venuchalla/Downloads/Development/BackEnd/resilience-example/src/main/resources/swagger/code-gen/generated-sources");

    configurator.setApiPackage("com.example.api");
    configurator.setModelPackage("com.example.model");
    configurator.setInvokerPackage("com.example.client");

    configurator.addAdditionalProperty("library", "spring-cloud");

    configurator.addAdditionalProperty("interfaceOnly", true);

    configurator.addAdditionalProperty("useFeignClient", true);

    configurator.addAdditionalProperty("useSpringBoot3", true);

    ClientOptInput input = configurator.toClientOptInput();

    DefaultGenerator generator = new DefaultGenerator();

    generator.opts(input).generate();

    System.out.println("Generation completed");
  }

  /**
   * code gen props
   *
   * @param packageName
   * @return
   */
  private static Map<String, String> getDefaultProperties(String packageName) {
    Map<String, String> defaultProperties = new HashMap<>();
    // defaultProperties.put(CodegenConstants.GEM_NAME, "Spring");
    defaultProperties.put(CodegenConstants.LIBRARY, "spring-cloud");
    defaultProperties.put(CodegenConstants.API_PACKAGE, packageName + ".api");
    defaultProperties.put(CodegenConstants.MODEL_PACKAGE, packageName + ".model");
    defaultProperties.put(CodegenConstants.INVOKER_PACKAGE, packageName + ".invoker");
    defaultProperties.put(CodegenConstants.SOURCE_FOLDER, "src/main/java");
    defaultProperties.put(SpringCodegen.DATE_LIBRARY, "java8");
    defaultProperties.put(SpringCodegen.USE_TAGS, "true");
    defaultProperties.put(SpringCodegen.USE_JAKARTA_EE, "true");
    defaultProperties.put(SpringCodegen.DELEGATE_PATTERN, "true");
    defaultProperties.put(SpringCodegen.SKIP_DEFAULT_INTERFACE, "true");
    defaultProperties.put(SpringCodegen.OPENAPI_NULLABLE, "true");
    defaultProperties.put(SpringCodegen.USE_SPRING_BOOT3, "true");
    defaultProperties.put(SpringCodegen.USE_FEIGN_CLIENT_CONTEXT_ID, "false");
    defaultProperties.put(
        SpringCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS,
        """
            @lombok.Getter
            @lombok.Setter
            @lombok.EqualsAndHashCode
            @lombok.ToString""");

    return defaultProperties;
  }
}
