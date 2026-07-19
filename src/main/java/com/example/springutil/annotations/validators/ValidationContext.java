package com.example.springutil.annotations.validators;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class ValidationContext {

  private final ProcessingEnvironment processingEnv;

  public ValidationContext(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  public Types types() {
    return processingEnv.getTypeUtils();
  }

  public Elements elements() {
    return processingEnv.getElementUtils();
  }

  public Messager messager() {
    return processingEnv.getMessager();
  }

  public void error(Element element, String message) {

    messager().printMessage(Diagnostic.Kind.ERROR, message, element);
  }
}
