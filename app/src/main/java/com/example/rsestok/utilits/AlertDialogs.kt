package com.example.rsestok.utilits

import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.example.rsestok.R
import com.example.rsestok.setDescriptionToDataBase
import com.example.rsestok.setNameToDataBase
import com.example.rsestok.updateCurrentUsername

class DialogChangeFullname(oldFullname:String){
    val dialog = Dialog(APP_ACTIVITY)


    init{
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_fullname)

        dialog.findViewById<EditText>(R.id.input_name).setText(oldFullname.substringBefore(" "))
        dialog.findViewById<EditText>(R.id.input_surname).setText(oldFullname.substringAfter(" "))

        dialog.findViewById<Button>(R.id.change_fullname).setOnClickListener{
            val name = dialog.findViewById<EditText>(R.id.input_name).text.toString().replace(" ", "")
            val surname = dialog.findViewById<EditText>(R.id.input_surname).text.toString().replace(" ", "")
            setNameToDataBase(name+" "+surname)
            dialog.hide()
        }
    }
    fun show(){
        dialog.show()
    }

}

class DialogChangeUsername(oldUsername: String){
    val dialog = Dialog(APP_ACTIVITY)


    init{
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_username)
        dialog.findViewById<EditText>(R.id.input_username).setText(oldUsername)

        dialog.findViewById<Button>(R.id.change_username).setOnClickListener{
            val username = dialog.findViewById<EditText>(R.id.input_username).text.toString().replace(" ", "")
            updateCurrentUsername(username)
            dialog.hide()

        }
    }
    fun show(){
        dialog.show()
    }

}


class DialogChangeDescription(oldDescription:String){
    val dialog = Dialog(APP_ACTIVITY)


    init{
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_description)
        dialog.findViewById<EditText>(R.id.input_description).setText(oldDescription)

        dialog.findViewById<Button>(R.id.change_description).setOnClickListener{
            val description = dialog.findViewById<EditText>(R.id.input_description).text.toString()
            setDescriptionToDataBase(description)
            dialog.hide()

        }
    }
    fun show(){
        dialog.show()
    }

}