package com.example.rsestok.models

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

)
