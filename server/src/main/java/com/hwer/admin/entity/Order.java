package com.hwer.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order")
public class Order implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Integer userId;
    @TableField("order_id")
    private String orderId;
    @TableField("status")
    private String status;
    @TableField("type")
    private String type;
    @TableField("close_position")
    private Boolean closePosition;
    @TableField("client_order_id")
    private String clientOrderId;
    @TableField("price")
    private BigDecimal price;
    @TableField("avg_price")
    private BigDecimal avgPrice;
    @TableField("orig_qty")
    private BigDecimal origQty;
    @TableField("executed_qty")
    private BigDecimal executedQty;
    @TableField("time_in_force")
    private String timeInForce;
    @TableField("symbol")
    private String symbol;
    @TableField("side")
    private String side;
    @TableField("position_side")
    private String positionSide;
    @TableField("stop_price")
    private BigDecimal stopPrice;
    @TableField("working_type")
    private String workingType;
    @TableField("price_protect")
    private Boolean priceProtect;
    @TableField("orig_type")
    private String origType;
    @TableField("price_match")
    private String priceMatch;
    @TableField("self_trade_prevention_mode")
    private String selfTradePreventionMode;
    @TableField("good_till_date")
    private String goodTillDate;
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @TableField("relations")
    private String relations;
    @TableField("loss_rate")
    private BigDecimal lossRate;

    public Order(Integer userId, String orderId, String relations,String side,String positionSide,BigDecimal lossRate,Boolean closePosition) {
        this.userId = userId;
        this.orderId = orderId;
        this.relations = relations;
        this.side = side;
        this.positionSide = positionSide;
        this.lossRate=lossRate;
        this.closePosition=closePosition;
    }
}
