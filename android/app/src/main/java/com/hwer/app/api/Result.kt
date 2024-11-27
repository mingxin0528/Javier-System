package com.hwer.app.api

data class Result<T>(val code: Int, val msg: String, val data: T?) {
    constructor() : this(0, "", null)
}
