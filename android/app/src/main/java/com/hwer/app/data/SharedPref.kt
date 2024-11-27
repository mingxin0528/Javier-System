package com.hwer.app.data

import android.content.Context
import android.content.SharedPreferences
import com.hwer.app.config.Constant

fun getSharedPref(ctx: Context): SharedPreferences {
    return ctx.getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE)
}

fun clear(ctx: Context) {
    val sharedPref = getSharedPref(ctx)
    with(sharedPref.edit()) {
        clear()
        apply()
    }
}

fun getUsername(ctx: Context): String? {
    val sp = getSharedPref(ctx)
    return sp.getString(Constant.SP_KEY_USERNAME, "")
}

fun setUsername(ctx: Context, username: String) {
    val sp = getSharedPref(ctx)
    with(sp.edit()) {
        putString(Constant.SP_KEY_USERNAME, username)
        apply()
    }
}

fun getApiKey(ctx: Context): String? {
    val sp = getSharedPref(ctx)
    return sp.getString(Constant.SP_KEY_API_KEY, "")
}

fun setApiKey(ctx: Context, apiKey: String) {
    val sp = getSharedPref(ctx)
    with(sp.edit()) {
        putString(Constant.SP_KEY_API_KEY, apiKey)
        apply()
    }
}

fun getSecretKey(ctx: Context): String? {
    val sp = getSharedPref(ctx)
    return sp.getString(Constant.SP_KEY_SECRET_KEY, "")
}

fun setSecretKey(ctx: Context, secretKey: String) {
    val sp = getSharedPref(ctx)
    with(sp.edit()) {
        putString(Constant.SP_KEY_SECRET_KEY, secretKey)
        apply()
    }
}
