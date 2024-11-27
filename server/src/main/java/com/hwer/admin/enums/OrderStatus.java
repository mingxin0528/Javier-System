package com.hwer.admin.enums;

public enum OrderStatus {
    /**
     * 新生成
     */
    GENERATED(0),
    /**
     * 成功被交易中心接受
     */
    SUBMITTED(1),
    /**
     * 被取消
     */
    CANCELED(2),
    /**
     * 被平仓
     */
    CLOSED(3),
    /**
     * 未知状态
     */
    UNKNOWN(4);
    private final Integer status;

    OrderStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
