package com.example.rsestok.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class VideoModel(
    var id:String = "",
    var title:String= "",
    var userId: String = "",
    var videoURI:String = "emphy",
    var audioURI:String = "emphy",
    var thumbnailUrl:String = "emphy",
    var description:String = "",
    var timeStamp: @RawValue Any = "",
    var likes:HashMap<String, String> = HashMap()

): Parcelable
