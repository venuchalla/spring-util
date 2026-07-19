package com.example.springutil.annotations.validators;

import javax.lang.model.element.ExecutableElement;

public class MappingValidator implements Validator {

  private static final java.util.List<String> MAPPING_ANNOTATIONS =
      java.util.List.of(
          "org.springframework.web.bind.annotation.GetMapping",
          "org.springframework.web.bind.annotation.PostMapping",
          "org.springframework.web.bind.annotation.PutMapping",
          "org.springframework.web.bind.annotation.DeleteMapping",
          "org.springframework.web.bind.annotation.RequestMapping");

  @Override
  public void validate(ValidationContext ctx, ExecutableElement method) {

    int mappings =
        (int)
            method.getAnnotationMirrors().stream()
                .filter(am -> MAPPING_ANNOTATIONS.contains(am.getAnnotationType().toString()))
                .count();

    if (mappings != 1) {

      ctx.error(method, "Exactly one Spring mapping annotation is required.");
    }
  }
}
