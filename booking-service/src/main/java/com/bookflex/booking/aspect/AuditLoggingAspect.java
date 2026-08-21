package com.bookflex.booking.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Spring AOP — Audit Logging Aspect.
 *
 * <p><b>Why AOP here?</b>
 * Every booking creation, confirmation, cancellation, and completion must be logged
 * for audit purposes. Without AOP, every service method would need logging boilerplate:
 * {@code log.info("Booking created by user {} for resource {}...")}
 *
 * AOP extracts this cross-cutting concern into a single place. The service methods
 * stay focused on business logic (SRP), and audit logging happens automatically
 * for any method matching the pointcut — including future methods added later (OCP).</p>
 */
@Aspect
@Component
@Slf4j
public class AuditLoggingAspect {

    /**
     * Pointcut matching all public methods in BookingServiceImpl.
     */
    @Pointcut("execution(* com.bookflex.booking.service.BookingServiceImpl.*(..))")
    public void bookingServiceMethods() {
        // Pointcut definition — no body needed
    }

    /**
     * Logs successful booking operations with method name and arguments.
     */
    @AfterReturning(pointcut = "bookingServiceMethods()", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        log.info("[AUDIT] {} completed successfully | Args: {}",
                joinPoint.getSignature().getName(),
                summarizeArgs(joinPoint.getArgs()));
    }

    /**
     * Logs failed booking operations with the exception details.
     */
    @AfterThrowing(pointcut = "bookingServiceMethods()", throwing = "exception")
    public void logAfterFailure(JoinPoint joinPoint, Throwable exception) {
        log.warn("[AUDIT] {} FAILED | Args: {} | Error: {}",
                joinPoint.getSignature().getName(),
                summarizeArgs(joinPoint.getArgs()),
                exception.getMessage());
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.stream(args)
                .map(arg -> arg != null ? arg.getClass().getSimpleName() + "=" + truncate(arg.toString()) : "null")
                .reduce((a, b) -> a + ", " + b)
                .orElse("[]");
    }

    private String truncate(String value) {
        return value.length() > 100 ? value.substring(0, 100) + "..." : value;
    }
}
