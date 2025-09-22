package SocialNetwork.SocialNetwork.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Log cho tất cả methods trong controllers
    @Around("execution(* SocialNetwork.SocialNetwork.controllers.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // Log request
        log.info("==> Calling {}.{} with arguments: {}", 
                className, methodName, Arrays.toString(joinPoint.getArgs()));
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // Log response
            log.info("<== {}.{} completed successfully in {} ms", 
                    className, methodName, (endTime - startTime));
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("<== {}.{} failed after {} ms with error: {}", 
                    className, methodName, (endTime - startTime), e.getMessage());
            throw e;
        }
    }

    // Log cho tất cả methods trong services
    @Around("execution(* SocialNetwork.SocialNetwork.services.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("Service call: {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            log.debug("Service {}.{} completed", className, methodName);
            return result;
        } catch (Exception e) {
            log.error("Service {}.{} failed: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}