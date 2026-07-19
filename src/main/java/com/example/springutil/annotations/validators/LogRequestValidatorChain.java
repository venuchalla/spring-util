package com.example.springutil.annotations.validators;

import java.util.List;
import javax.lang.model.element.ExecutableElement;

public class LogRequestValidatorChain {

  private final List<Validator> validators =
      List.of(
          new RestControllerValidator(),
          new MappingValidator(),
          new RequestBodyValidator(),
          new RequestWrapperValidator(),
          new ResponseWrapperValidator(),
          new ResponseEntityValidator(),
          new ValidationAnnotationValidator());

  public void validate(ValidationContext ctx, ExecutableElement method) {

    validators.forEach(v -> v.validate(ctx, method));
  }
}
