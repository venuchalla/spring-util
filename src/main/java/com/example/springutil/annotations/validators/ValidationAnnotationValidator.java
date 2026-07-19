package com.example.springutil.annotations.validators;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class ValidationAnnotationValidator implements Validator {

  @Override
  public void validate(ValidationContext ctx, ExecutableElement method) {

    TypeElement clazz = (TypeElement) method.getEnclosingElement();

    //    if (clazz.getAnnotation(Validated.class) == null
    //        && method.getAnnotation(Validated.class) == null) {
    //
    //      ctx.error(method, "@Validated is required.");
    //    }
    // if (!hasAnnotation(clazz, VALIDATED) && !hasAnnotation(method, VALIDATED)) {

    //  ctx.error(method, "@Validated is required.");
    // }
  }

  private static final String VALIDATED = "org.springframework.validation.annotation.Validated";

  private boolean hasAnnotation(Element element, String annotationName) {
    return element.getAnnotationMirrors().stream()
        .anyMatch(a -> a.getAnnotationType().toString().equals(annotationName));
  }
}
