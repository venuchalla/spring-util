package com.example.springutil.annotations.validators;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class RestControllerValidator implements Validator {

  @Override
  public void validate(ValidationContext ctx, ExecutableElement method) {

    TypeElement clazz = (TypeElement) method.getEnclosingElement();

    // Check if class has @RestController annotation
    if (clazz.getAnnotationMirrors().stream()
        .noneMatch(
            am ->
                am.getAnnotationType()
                    .toString()
                    .equals("org.springframework.web.bind.annotation.RestController"))) {

      ctx.error(method, "@LogRequest can only be used inside @RestController");
    }
  }
}
