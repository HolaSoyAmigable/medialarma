package com.medialarma.app

import android.app.Application
import com.medialarma.app.di.AppContainer

class MediAlarmaApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
