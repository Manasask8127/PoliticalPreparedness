package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase

class AppRepoContainer(private val applicationContext: Context) {
    private val database = ElectionDatabase.getInstance(applicationContext)
    val repository = Repository(database)
}