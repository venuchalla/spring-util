package com.example.springutil.annotations.validators;

import com.example.springutil.annotations.LogRequest;
import javax.lang.model.element.ExecutableElement;

public class RequestBodyValidator implements Validator {

  @Override
  public void validate(ValidationContext ctx, ExecutableElement method) {

    LogRequest log = method.getAnnotation(LogRequest.class);

    if (!log.enforceRequestWrapper()) return;

    int count =
        (int)
            method.getParameters().stream()
                .filter(
                    param ->
                        param.getAnnotationMirrors().stream()
                            .anyMatch(
                                am ->
                                    am.getAnnotationType()
                                        .toString()
                                        .equals(
                                            "org.springframework.web.bind.annotation.RequestBody")))
                .count();

    if (count != 1) {

      ctx.error(method, "@LogRequest requires exactly one @RequestBody parameter");
    }
  }
}
