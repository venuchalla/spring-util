package com.example.springutil.annotations.validators;

import javax.lang.model.element.ExecutableElement;

public interface Validator {

  void validate(ValidationContext context, ExecutableElement method);
}
