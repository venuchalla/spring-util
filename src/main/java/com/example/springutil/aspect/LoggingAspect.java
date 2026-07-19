package com.example.springutil.aspect;

import com.example.springutil.annotations.LogAfterMethod;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@CustomLog
@RequiredArgsConstructor
public class LoggingAspect {

  private final HttpServletRequest request;

  @Before("execution(* com.example.resilience.services.*.*(..))")
  public void beforeEnterService(JoinPoint joinPoint) {
    log.info("log before service method execution : {} ", joinPoint.getClass());
  }

  // @After("execution(* com.example.resilience.example.services.*.*(..))")
  @After("@annotation(com.example.springutil.annotations.LogAfterMethod)")
  public void logAfterMethodExecution(JoinPoint joinPoint) {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    LogAfterMethod annotation = method.getAnnotation(LogAfterMethod.class);
    // Access the annotation value
    String value = annotation.value();
    log.info("After advice value passed from the annotation : {}", value);
    String methodName = joinPoint.getSignature().toShortString();
    log.info("After advice Executed: {} ", methodName);
  }

  @Around("execution(* com.example.resilience.services.*.*(..))")
  public Object logWhileEnterAndExistServiceMethod(ProceedingJoinPoint proceedingJoinPoint)
      throws Throwable {
    log.info(
        "Around advice for method {} is started ",
        proceedingJoinPoint.getSignature().toShortString());
    Object result = proceedingJoinPoint.proceed();
    log.info(
        "Around advice method {} is completed", proceedingJoinPoint.getSignature().toShortString());
    return result;
  }

  @Around("@annotation(com.example.resilience.annotations.LogRequest)")
  public Object aroundLogReqeust(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    String className = signature.getDeclaringTypeName();
    Method method = signature.getMethod();
    // Parameter[] args= method.getParameters();
    Object[] argValues = proceedingJoinPoint.getArgs();

    log.info(
        "class name {} Request method {} is called with arguments: {} ",
        className,
        method.getName(),
        argValues);
    return proceedingJoinPoint.proceed();
  }
}
