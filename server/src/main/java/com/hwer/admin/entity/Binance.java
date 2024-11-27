package com.hwer.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName("t_binance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Binance implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("asset")
    private String asset;
    @TableField("balance")
    private BigDecimal walletBalance;
    @TableField("cross_wallet_balance")
    private BigDecimal crossWalletBalance;
    @TableField("cross_un_pnl")
    private BigDecimal crossUnPnl;
    @TableField("available_balance")
    private BigDecimal availableBalance;
    @TableField("max_withdraw_amount")
    private BigDecimal maxWithdrawAmount;
    @TableField("margin_available")
    private Boolean marginAvailable;
}
