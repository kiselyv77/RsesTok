package com.example.rsestok

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rsestok.databinding.ActivityMainBinding
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.UserStatus
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RsesTok)
        super.onCreate(savedInstanceState)
        APP_ACTIVITY = this
        initFirebase()
        initUser(){}

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navViewRegister: BottomNavigationView = binding.navViewRegister

        APP_NAV_CONTROLLER = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(APP_NAV_CONTROLLER)
        navViewRegister.setupWithNavController(APP_NAV_CONTROLLER)
        APP_NAV_CONTROLLER.addOnDestinationChangedListener(
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_home) {
                APP_ACTIVITY.setColorNavView(Color.BLACK, Color.WHITE)
                APP_ACTIVITY.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val attrib = APP_ACTIVITY.window.attributes
                attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

            } else {
                APP_ACTIVITY.setColorNavView(Color.WHITE, Color.BLACK)
                APP_ACTIVITY.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val attrib = APP_ACTIVITY.window.attributes
                attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        })
    }
    override fun onStart() {
        super.onStart()
        UserStatus.updateState(UserStatus.ONLINE)
        FirebaseApp.initializeApp(this)
        if(AUTH.currentUser==null){
            binding.navView.visibility = View.GONE
            binding.navViewRegister.visibility = View.VISIBLE
            APP_NAV_CONTROLLER.navigate(R.id.navigation_login)
        }
        else{
            binding.navView.visibility = View.VISIBLE
            binding.navViewRegister.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        UserStatus.updateState(UserStatus.OFFLINE)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun goneNavView(){
        binding.navView.visibility = View.GONE
    }
    fun visibleNavView(){
        binding.navView.visibility = View.VISIBLE
    }


    fun setColorNavView(colorBackound:Int, colorIcon:Int){
        binding.navView.background = ColorDrawable(colorBackound)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_enabled),
        )
        val colors = intArrayOf(
            Color.BLUE,
            colorIcon,
        )
        val stateList = ColorStateList(states, colors)
        binding.navView.itemTextColor = stateList
        binding.navView.itemIconTintList = stateList

    }

}