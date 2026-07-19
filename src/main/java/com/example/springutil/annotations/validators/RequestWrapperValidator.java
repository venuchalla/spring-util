package com.example.springutil.annotations.validators;

import com.example.springutil.annotations.LogRequest;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class RequestWrapperValidator implements Validator {

  private static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";

  private static final String REQUEST_WRAPPER = "com.example.springutil.dto.RequestWrapper";

  @Override
  public void validate(ValidationContext ctx, ExecutableElement method) {

    LogRequest log = method.getAnnotation(LogRequest.class);

    if (log == null || !log.enforceRequestWrapper()) {
      return;
    }

    TypeElement wrapperElement = ctx.elements().getTypeElement(REQUEST_WRAPPER);

    if (wrapperElement == null) {
      ctx.error(method, "RequestWrapper type not found in classpath");
      return;
    }

    boolean found = false;

    for (VariableElement parameter : method.getParameters()) {

      if (!hasAnnotation(parameter, REQUEST_BODY)) {
        continue;
      }

      TypeMirror parameterType = parameter.asType();

      if (parameterType instanceof DeclaredType declaredType) {

        Element element = declaredType.asElement();

        if (element instanceof TypeElement typeElement
            && typeElement.getQualifiedName().contentEquals(REQUEST_WRAPPER)) {

          found = true;
          break;
        }
      }
    }

    if (!found) {
      ctx.error(method, "Method with @LogRequest must have @RequestBody RequestWrapper parameter");
    }
  }

  private boolean hasAnnotation(Element element, String annotationName) {

    return element.getAnnotationMirrors().stream()
        .anyMatch(a -> a.getAnnotationType().toString().equals(annotationName));
  }
}
