package com.example.passwordmanager

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.passwordmanager.ioc.ApplicationComponent

class PasswordManagerApp: Application() {

    val applicationComponent by lazy { ApplicationComponent(applicationContext) }

    companion object{
        fun get(context: Context): PasswordManagerApp{
            Log.v("GES-21", "hy")
            return context.applicationContext as PasswordManagerApp
        }
    }
}