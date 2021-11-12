package com.example.rsestok.models

import android.os.Parcel
import android.os.Parcelable

data class VideoModel(
    var id:String = "",
    var title:String= "",
    var userId: String = "",
    var videoURI:String = "emphy",
    var audioURI:String = "emphy",
    var thumbnailUrl:String = "emphy",
    var description:String = "",
    var timeStamp:Any = "",
    var likes:HashMap<String, String> = HashMap()

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        ) {}

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<VideoModel> {
        override fun createFromParcel(parcel: Parcel): VideoModel {
            return VideoModel(parcel)
        }

        override fun newArray(size: Int): Array<VideoModel?> {
            return arrayOfNulls(size)
        }
    }
}
