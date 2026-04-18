package com.flow.common.exception;

import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        String message = extractErrorMessage(e);
        return Result.error(ResultCode.ERROR.getCode(), message);
    }

    /**
     * 提取友好的错误信息
     */
    private String extractErrorMessage(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            return "系统繁忙，请稍后重试";
        }
        
        // BPMN 验证错误 - Flowable
        if (errorMessage.contains("messageRef") || errorMessage.contains("messageExpression")) {
            return "流程定义错误：消息开始事件必须配置 messageRef 或 messageExpression";
        }
        if (errorMessage.contains("flowable-executable-process") || errorMessage.contains("Validation set")) {
            // 提取具体的验证错误
            if (errorMessage.contains("Problem:")) {
                int start = errorMessage.indexOf("Problem:") + 8;
                int end = errorMessage.indexOf("]", start);
                if (end > start) {
                    String problem = errorMessage.substring(start, end).trim();
                    return "流程定义验证失败：" + problem;
                }
            }
            return "流程定义验证失败，请检查 BPMN 配置";
        }
        if (errorMessage.contains("Errors while parsing")) {
            return "流程定义解析失败，请检查 BPMN XML 格式";
        }
        
        // Flowable 流程相关错误
        if (errorMessage.contains("does not exist") && errorMessage.contains("process")) {
            return "流程定义不存在或已被删除";
        }
        if (errorMessage.contains("already exists") && errorMessage.contains("process")) {
            return "流程定义已存在";
        }
        if (errorMessage.contains("suspended")) {
            return "流程已挂起，无法执行此操作";
        }
        
        // 权限相关错误
        if (errorMessage.contains("Access is denied") || errorMessage.contains("不允许访问")) {
            return "权限不足，请联系管理员";
        }
        
        // 数据库错误
        if (errorMessage.contains("BadSqlGrammarException") || errorMessage.contains("SQLSyntaxErrorException")) {
            return "数据库错误，请联系管理员";
        }
        
        return "系统繁忙，请稍后重试";
    }
}
