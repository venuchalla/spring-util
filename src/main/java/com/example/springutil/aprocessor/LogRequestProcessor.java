package com.example.springutil.aprocessor;

import com.example.springutil.annotations.LogRequest;
import com.example.springutil.annotations.validators.LogRequestValidatorChain;
import com.example.springutil.annotations.validators.ValidationContext;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("com.example.springutil.annotations.LogRequest")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class LogRequestProcessor extends AbstractProcessor {

  private ValidationContext context;

  private LogRequestValidatorChain chain;

  @Override
  public synchronized void init(ProcessingEnvironment env) {

    super.init(env);

    context = new ValidationContext(env);

    chain = new LogRequestValidatorChain();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    System.out.println("LogRequestProcessor.process");
    for (Element element : roundEnv.getElementsAnnotatedWith(LogRequest.class)) {
      chain.validate(context, (ExecutableElement) element);
    }

    return true;
  }
}
