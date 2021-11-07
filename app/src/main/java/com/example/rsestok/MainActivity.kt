package com.example.rsestok

import android.os.Bundle
import android.view.View
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




    override fun onCreate(savedInstanceState: Bundle?) {
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

}