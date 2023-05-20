package com.kodlamaio.commonpackage.configuration.exceptions;

import com.kodlamaio.commonpackage.utils.constants.ExceptionTypes;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.commonpackage.utils.results.ExceptionResult;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalRestExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ExceptionResult<Object> handleBusinessException(BusinessException exception) {
        return new ExceptionResult<Object>(
                exception.getMessage(),
                ExceptionTypes.Exception.Business,
                HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<String, String>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ExceptionResult<Object> exceptionResult = new ExceptionResult<Object>(
                validationErrors,
                ExceptionTypes.Exception.Validation,
                HttpStatus.BAD_REQUEST.value()
        );

        return exceptionResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ExceptionResult<Object> handleValidationException(ValidationException exception) {
        return new ExceptionResult<>(
                exception.getMessage(),
                ExceptionTypes.Exception.Validation,
                HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ExceptionResult<Object> handleDataIntegrityViolationExceptionException(DataIntegrityViolationException exception) {
        return new ExceptionResult<Object>(
                exception.getMessage(),
                ExceptionTypes.Exception.DataIntegrityViolation,
                HttpStatus.CONFLICT.value()
        );
    }
}