package com.divyanshu.musicplayerapp.helper

import android.content.Context
import android.widget.Toast
import java.util.concurrent.TimeUnit

object Constants {

    const val seekForwardTime = 5000
    const val seekBackwardTime = 5000

    fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun Long.toMusicDuration(): String {
        val minutes = TimeUnit.MINUTES.convert(this, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(this, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }
}