package com.spring.eventbooking.exception;

import com.spring.eventbooking.dto.Response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> handleGlobalException(GlobalException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = getLocalizedMessage(ex.getMessage(), null, locale, ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                message,
                request.getDescription(false)
        );
        return ResponseEntity.status(ex.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        Map<String, List<String>> validationErrors = ex.getBindingResult().getAllErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        error -> {
                            if (error instanceof FieldError) {
                                return ((FieldError) error).getField();
                            }
                            return error.getObjectName();
                        },
                        Collectors.mapping(
                                error -> getLocalizedErrorMessage(error, locale),
                                Collectors.toList()
                        )
                ));

        ValidationErrorResponse response = new ValidationErrorResponse(
                new Date(),
                getLocalizedMessage("validation.error", null, locale, "Validation failed"),
                request.getDescription(false),
                validationErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        Locale locale = request.getLocale();
        ErrorDetails errorResponse = new ErrorDetails(
                new Date(),
                messageSource.getMessage("security.permission", null, locale),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
//        Locale locale = LocaleContextHolder.getLocale();
//        String message = getLocalizedMessage("error.generic", null, locale, ex.getMessage());
//
//        ErrorDetails errorDetails = new ErrorDetails(
//                new Date(),
//                message,
//                request.getDescription(false)
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
//    }

    private String getLocalizedErrorMessage(ObjectError error, Locale locale) {
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            String messageKey = fieldError.getDefaultMessage();

            // Check if the message is a key wrapped in curly braces (e.g., "{validation.email.required}")
            if (messageKey != null && messageKey.startsWith("{") && messageKey.endsWith("}")) {
                messageKey = messageKey.substring(1, messageKey.length() - 1);
            }

            try {
                String localizedMessage = messageSource.getMessage(messageKey, fieldError.getArguments(), locale);
                // Ensure proper UTF-8 encoding
                return new String(localizedMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            } catch (NoSuchMessageException e) {
                return fieldError.getDefaultMessage();
            }
        }
        return error.getDefaultMessage();
    }

    private String getLocalizedMessage(String code, Object[] args, Locale locale, String defaultMessage) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }
}