package com.whozm.yygh.common.handler;

import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @author HZM
 * @date 2023/1/14
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R handleException(Exception ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message("编译时异常");
    }

    @ExceptionHandler(SQLException.class)
    public R handleSqlException(SQLException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message("Sql异常");
    }

    @ExceptionHandler(ArithmeticException.class)
    public R handleArithmeticException(ArithmeticException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message("数学异常");
    }

    @ExceptionHandler(YyghException.class)
    public R handleYyghException(YyghException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message(ex.getMessage()).code(ex.getCode());
    }
}
