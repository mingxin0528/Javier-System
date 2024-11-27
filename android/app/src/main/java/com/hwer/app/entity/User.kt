package com.hwer.app.entity

import java.math.BigDecimal

data class User(
    val id: Int?,
    var username: String?,
    val password: String?,
    val binance: BigDecimal?,
    var secret: String?,
    val selfCode: String?,
    val usedCode: String?,
    val token: String?,
    var apiKey: String?,
    var secretKey: String?,
    var allow: Int,
    val status: Int,
    val binanceList: List<Binance>
) {
    constructor(username: String,password: String,usedCode: String?=null) : this(null, username, password, BigDecimal(0),"", "", "","", "", "", 0, 1,ArrayList<Binance>())
}