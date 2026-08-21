package com.bookflex.booking.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Spring AOP — Performance Monitoring Aspect.
 *
 * <p><b>Why AOP here?</b>
 * Measuring execution time of critical operations (booking creation, payment processing)
 * is essential for identifying bottlenecks. Without AOP, every method would need:
 * {@code long start = System.nanoTime(); ... long elapsed = System.nanoTime() - start;}
 *
 * This aspect automatically measures and logs execution time for all booking service
 * and saga orchestrator methods, providing observability without code duplication.</p>
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitorAspect {

    /**
     * Measures execution time of all booking service methods.
     */
    @Around("execution(* com.bookflex.booking.service.BookingServiceImpl.*(..)) || " +
            "execution(* com.bookflex.booking.saga.BookingSagaOrchestrator.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

            if (elapsedMs > 500) {
                log.warn("[PERF] {} took {}ms (SLOW)", methodName, elapsedMs);
            } else {
                log.debug("[PERF] {} took {}ms", methodName, elapsedMs);
            }

            return result;
        } catch (Throwable ex) {
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
            log.warn("[PERF] {} FAILED after {}ms: {}", methodName, elapsedMs, ex.getMessage());
            throw ex;
        }
    }
}
