package com.hwer.admin.config;

import com.hwer.admin.entity.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class OtherExceptionHandle {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R handleValidationException(Exception e) {
        return R.no(String.valueOf(e));
    }
}
