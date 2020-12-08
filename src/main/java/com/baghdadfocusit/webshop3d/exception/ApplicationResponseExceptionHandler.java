package com.baghdadfocusit.webshop3d.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@ControllerAdvice
public class ApplicationResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationErrorResponse> customCategory(ApplicationException ex) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone("Asia/Baghdad").toZoneId());
        ApplicationErrorResponse errors = new ApplicationErrorResponse();
        errors.setTimestamp(LocalDateTime.from(zonedDateTime));
        errors.setErrorMessage(ex.getMessage());
        errors.setStatus(ex.getStatus().value());
        return new ResponseEntity<>(errors, ex.getStatus());
    }
}
