package com.divyanshu.musicplayerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val title: String,
    val uri: String,
    val artist: String,
    val duration: String
) : Parcelable
