package com.example.opsc7312_flappr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun redirectToNavigationDrawer(view: View) {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }
}