package com.funsol.funnotify

import android.app.Application
import android.util.Log
import com.funsol.fcm.FunFCM

class FunApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val myFcm = FunFCM()

        with(myFcm) {
            setup(this@FunApplication, this@FunApplication.packageName)
            getToken(
                onSuccess = { token ->
                    // TOKEN
                    Log.d("FCM_TAG", "token: %$token")
                },
                onFailure = { error ->
                    // ERROR
                }
            )
        }

    }
}