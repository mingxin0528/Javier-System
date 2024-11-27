package com.hwer.admin.entity;


import java.io.Serializable;

public class R implements Serializable {
    public static final Integer OK_CODE = 1;
    public static final String OK_STATUS = "Ok";
    public static final String OK_MSG = "成功";
    public static final Integer NO_CODE = 0;
    public static final String NO_STATUS = "No";
    public static final String NO_MSG = "失败";
    private Integer code;
    private String msg;
    private Object data;
    private String status;

    public static R ok() {
        return new R(OK_CODE, OK_MSG, null, OK_STATUS);
    }

    public static R ok(Object data) {
        return new R(OK_CODE, OK_MSG, data, OK_STATUS);
    }

    public static R no() {
        return new R(NO_CODE, NO_MSG, null, NO_STATUS);
    }

    public static R no(String msg) {
        return new R(NO_CODE, msg, null, NO_STATUS);
    }

    public static R no(Integer code, String msg) {
        return new R(code, msg, null, NO_STATUS);
    }

    public R() {
    }

    public R(Integer code, String msg, Object data, String status) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.status = status;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
