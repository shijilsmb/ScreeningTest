package com.example.screeningtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.screeningtest.utils.SessionUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SessionUtils.isSessionActive){
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
            val navController = navHostFragment.navController
            //val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            val navGraph = navController.graph
            navGraph.startDestination = R.id.mainFragment;
            navController.graph = navGraph;
        }
    }
}