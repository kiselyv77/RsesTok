package com.example.rsestok.utilits

import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import com.example.rsestok.MainActivity
import com.example.rsestok.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message:String){
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity(){
    APP_ACTIVITY.startActivity(Intent(APP_ACTIVITY, MainActivity::class.java))
    APP_ACTIVITY.finish()
}


fun ImageView.downloadAndSetImage(uri: String, defaultPhoto:Int = R.drawable.profile){
    Picasso.get()
        .load(uri)
        .placeholder(defaultPhoto)
        .into(this)
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)

}

