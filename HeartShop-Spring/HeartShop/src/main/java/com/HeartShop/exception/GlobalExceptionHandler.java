package com.HeartShop.exception;
import com.HeartShop.common.ApiResponse;
import com.HeartShop.common.ResponseCode;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.warn("業務邏輯例外: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("資源不存在: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("參數驗證失敗: {}", errors);
        return ApiResponse.error(
                ResponseCode.VALIDATION_ERROR.getCode(),
                "參數驗證失敗：" + errors
        );
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleJwtException(JwtException e) {
        log.warn("JWT 驗證失敗: {}", e.getMessage());
        return ApiResponse.error(ResponseCode.UNAUTHORIZED.getCode(), "登入憑證無效或已過期，請重新登入");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleMissingRequestHeader(MissingRequestHeaderException e) {
        if ("Authorization".equalsIgnoreCase(e.getHeaderName())) {
            log.warn("缺少 Authorization header");
            return ApiResponse.error(ResponseCode.UNAUTHORIZED.getCode(), "請先登入");
        }
        log.warn("缺少必要 header: {}", e.getHeaderName());
        return ApiResponse.error(ResponseCode.BAD_REQUEST.getCode(), "缺少必要 header: " + e.getHeaderName());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("不支援的 Content-Type: {}", e.getMessage());
        return ApiResponse.error(ResponseCode.BAD_REQUEST.getCode(), "請求 Content-Type 不正確，請使用 application/json");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("請求體解析失敗: {}", e.getMessage());
        return ApiResponse.error(ResponseCode.BAD_REQUEST.getCode(), "請求格式錯誤");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("系統錯誤", e);
        return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}