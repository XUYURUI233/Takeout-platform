package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 处理数据完整性约束违反异常（包括秒杀场景的并发问题）
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(DataIntegrityViolationException ex){
        log.error("数据完整性约束违反异常：{}", ex.getMessage());
        String message = ex.getMessage();
        if(message != null && message.contains("Duplicate entry")) {
            if(message.contains("seckill_user_record")) {
                // 秒杀用户记录重复，说明是并发导致的
                return Result.error("系统繁忙，请稍后重试");
            } else {
                // 其他重复键异常
                return Result.error("数据已存在，请检查后重试");
            }
        } else {
            return Result.error("系统繁忙，请稍后重试");
        }
    }
}
