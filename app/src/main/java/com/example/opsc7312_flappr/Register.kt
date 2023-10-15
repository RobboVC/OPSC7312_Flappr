package com.example.opsc7312_flappr

import LoginWorker
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val txtUsername = findViewById<TextView>(R.id.emailEditText)
        val txtPassword = findViewById<TextView>(R.id.etPassword)
        val txtName = findViewById<TextView>(R.id.etName)
        var username = txtUsername.text
        var password = txtPassword.text
        var name = txtName.text
        val btnLogin = findViewById<TextView>(R.id.tvSubtitle)

        btnLogin.setOnClickListener(){
            val intent = Intent(this, Login::class.java)
            LoginWorker.username = username.toString()
            LoginWorker.password = password.toString()

            startActivity(intent)
        }

    }
}