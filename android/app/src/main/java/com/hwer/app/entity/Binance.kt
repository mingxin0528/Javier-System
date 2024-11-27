package com.hwer.app.entity

import java.math.BigDecimal

data class Binance(val id: Int, val userId: Int, val asset:String,val binance: BigDecimal, val availableBalance: BigDecimal)
