package com.hwer.app

import android.app.Application
import com.hwer.app.data.DefaultUserContainer
import com.hwer.app.data.UserContainer

class HwerApplication : Application() {
    lateinit var container: UserContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultUserContainer()
    }
}