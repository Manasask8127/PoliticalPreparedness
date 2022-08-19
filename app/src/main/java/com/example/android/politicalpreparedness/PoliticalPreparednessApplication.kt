package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber

class PoliticalPreparednessApplication : Application() {

    lateinit var appRepoContainer: AppRepoContainer
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appRepoContainer = AppRepoContainer(applicationContext)
    }
}