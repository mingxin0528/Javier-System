package com.hwer.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("t_income")
public class Income implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Integer userId;
    @TableField("income_type")
    private String incomeType;
    @TableField("symbol")
    private String symbol;
    @TableField("income")
    private BigDecimal income;
    @TableField("asset")
    private String asset;
    @TableField("tran_id")
    private String tranId;
    @TableField("trade_id")
    private String tradeId;
    @TableField("info")
    private String info;
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp time;
}

