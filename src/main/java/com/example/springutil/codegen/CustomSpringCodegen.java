package com.example.springutil.codegen;

import static java.lang.String.format;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.SpringCodegen;

@Slf4j
public class CustomSpringCodegen extends SpringCodegen {

  public static final String X_LOG_MASK = "x-log-mask";
  public static final String X_LOG_IGNORE = "x-log-ignore";

  private final Collection<String> mask;
  private final Collection<String> ignore;

  public CustomSpringCodegen(Collection<String> mask, Collection<String> ignore) {

    this.mask = mask;
    this.ignore = ignore;
    // Your custom generator name
    // embeddedTemplateDir = templateDir = "custom-spring";

    // Optional defaults
    // additionalProperties.put("interfaceOnly", true);
    // additionalProperties.put("useSpringBoot3", true);
    log.info("Loaded {} mask and {} ignore", mask, ignore);
  }

  //    @Override
  //    public Map<String, Object> postProcessOperationsWithModels(
  //            Map<String, Object> objs,
  //            java.util.List<Object> allModels) {
  //
  //        Map<String, Object> operations =
  //                super.postProcessOperationsWithModels(objs, allModels);
  //
  //        return operations;
  //    }

  @Override
  public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
    super.postProcessModelProperty(model, property);
    final String propertyPath =
        format("%s.%s.%s", this.modelPackage(), model.getClassname(), property.getName());
    final boolean isMasked = mask.contains(propertyPath);
    property.getVendorExtensions().put(X_LOG_MASK, isMasked);
    property.getVendorExtensions().put(X_LOG_IGNORE, ignore.contains(propertyPath));
  }
}
