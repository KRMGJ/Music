package com.example.music.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@SuppressWarnings("unused")
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 예외의 최상위 원인 메시지를 반환합니다.
     * @param e 발생한 예외
     * @return 최상위 원인 메시지
     */
	private String getRootCauseMessage(Throwable e) {
	    Throwable cause = e;
	    while (cause.getCause() != null) {
	        cause = cause.getCause();
	    }
	    return Optional.ofNullable(cause.getMessage()).orElse(cause.toString());
	}

    @ExceptionHandler({
            Exception.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            NoHandlerFoundException.class,
            MaxUploadSizeExceededException.class,
            MultipartException.class,
            ConversionFailedException.class,
            TransactionException.class,
            AuthenticationException.class,
            AccessDeniedException.class,
            DataAccessException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            JsonProcessingException.class,
            BeanCreationException.class
    })
    public ResponseEntity<String> handleAll(Exception e) {
        String msg = getRootCauseMessage(e);
        log.error("예외 발생: {}", Optional.ofNullable(e.getMessage()).orElse(e.toString()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(msg);
    }
}