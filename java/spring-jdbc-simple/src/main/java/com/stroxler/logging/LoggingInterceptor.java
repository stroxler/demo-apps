package com.stroxler.logging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class LoggingInterceptor {

    public LoggingInterceptor() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Around("@annotation(com.stroxler.logging.LogCall)")
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        String classname = pjp.getSignature().getDeclaringTypeName();
        String methodname = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        String callDescription = functionCall(classname, methodname, args);
        try {
            LOGGER.info(String.format("Calling %s", callDescription));
            Object result = pjp.proceed();
            LOGGER.info(String.format("Call %s => %s", callDescription, String.valueOf(result)));
            return result;
        } catch (Throwable t) {
            LOGGER.error(String.format("Encountered exception in call %s", callDescription), t);
            throw t;
        }
    }

    private static String functionCall(String classname, String methodname, Object[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s.%s(", classname, methodname));
        List<String> argStrings = Arrays.stream(args)
                .map(String::valueOf)
                .collect(Collectors.toList());
        builder.append(String.join(", ", argStrings));
        builder.append(")");
        return builder.toString();
    }

}

