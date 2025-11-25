package com.example.gym.interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CDI Interceptor that logs CRUD operations with user info, operation name, and resource ID.
 * Logs: username, operation name (CREATE/UPDATE/DELETE), resource ID
 */
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {

    private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class.getName());

    // Methods that represent CRUD operations
    private static final Set<String> CREATE_METHODS = Set.of("create", "save", "add", "insert");
    private static final Set<String> UPDATE_METHODS = Set.of("update", "edit", "modify");
    private static final Set<String> DELETE_METHODS = Set.of("delete", "remove");

    @Inject
    private SecurityContext securityContext;

    @AroundInvoke
    public Object logMethodInvocation(InvocationContext context) throws Exception {
        String className = context.getTarget().getClass().getSimpleName();
        // Remove proxy suffix from class name
        if (className.contains("$")) {
            className = className.substring(0, className.indexOf("$"));
        }
        String methodName = context.getMethod().getName();
        Object[] parameters = context.getParameters();

        // Determine operation type
        String operationType = determineOperationType(methodName);
        
        // Get current user
        String username = getCurrentUsername();
        
        // Extract resource ID from parameters
        String resourceId = extractResourceId(parameters);

        final String finalClassName = className;
        final String finalResourceId = resourceId;

        try {
            // Proceed with the method execution
            Object result = context.proceed();

            // Log only after successful completion for CRUD operations
            if (operationType != null) {
                String resultId = extractResultId(result);
                LOGGER.log(Level.INFO, String.format(
                    "[AUDIT] User: %s | Operation: %s | Resource: %s | ResourceId: %s | Method: %s.%s",
                    username,
                    operationType,
                    finalClassName.replace("Service", ""),
                    resultId != null ? resultId : finalResourceId,
                    finalClassName,
                    methodName
                ));
            }

            return result;
        } catch (Exception e) {
            // Log exception for CRUD operations
            if (operationType != null) {
                LOGGER.log(Level.WARNING, String.format(
                    "[AUDIT] FAILED | User: %s | Operation: %s | Resource: %s | ResourceId: %s | Error: %s",
                    username,
                    operationType,
                    finalClassName.replace("Service", ""),
                    finalResourceId,
                    e.getMessage()
                ));
            }

            throw e;
        }
    }

    /**
     * Determines the operation type based on method name.
     */
    private String determineOperationType(String methodName) {
        String lowerMethod = methodName.toLowerCase();
        
        for (String keyword : DELETE_METHODS) {
            if (lowerMethod.contains(keyword)) {
                return "DELETE";
            }
        }
        for (String keyword : UPDATE_METHODS) {
            if (lowerMethod.contains(keyword)) {
                return "UPDATE";
            }
        }
        for (String keyword : CREATE_METHODS) {
            if (lowerMethod.contains(keyword)) {
                return "CREATE";
            }
        }
        return null; // Not a CRUD operation
    }

    /**
     * Gets the current authenticated username.
     */
    private String getCurrentUsername() {
        try {
            if (securityContext != null) {
                Principal principal = securityContext.getCallerPrincipal();
                if (principal != null && !"ANONYMOUS".equals(principal.getName())) {
                    return principal.getName();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Could not get username from SecurityContext", e);
        }
        return "anonymous";
    }

    /**
     * Extracts resource ID from method parameters.
     * Looks for UUID parameters or objects with getId() method.
     */
    private String extractResourceId(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "N/A";
        }

        for (Object param : parameters) {
            if (param == null) continue;
            
            // Direct UUID parameter
            if (param instanceof UUID) {
                return param.toString();
            }
            
            // Try to get ID from entity object
            try {
                java.lang.reflect.Method getIdMethod = param.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(param);
                if (id != null) {
                    return id.toString();
                }
            } catch (Exception e) {
                // No getId method, continue
            }
        }

        return "N/A";
    }

    /**
     * Extracts resource ID from method result (for CREATE operations).
     */
    private String extractResultId(Object result) {
        if (result == null) {
            return null;
        }

        // Direct UUID result
        if (result instanceof UUID) {
            return result.toString();
        }

        // Try to get ID from returned entity
        try {
            java.lang.reflect.Method getIdMethod = result.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(result);
            if (id != null) {
                return id.toString();
            }
        } catch (Exception e) {
            // No getId method
        }

        return null;
    }
}
