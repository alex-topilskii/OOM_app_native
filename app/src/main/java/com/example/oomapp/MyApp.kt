package com.example.oomapp

import android.app.Application

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            println("Intag: Uncaught exception in thread: $thread, $throwable")
            throwable.printStackTrace()
        }
    }
}