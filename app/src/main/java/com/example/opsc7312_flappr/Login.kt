package com.example.opsc7312_flappr

import LoginWorker
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var givenUsername = findViewById<TextView>(R.id.emailEditText).text
        var givenPassword = findViewById<TextView>(R.id.etPassword).text
        val i = getIntent()
        val username = LoginWorker.username
        val password = LoginWorker.password
        val name = i.getStringExtra("name")


        val tvRedirectToRegister = findViewById<TextView>(R.id.tvRedirectToRegister)
        tvRedirectToRegister.paintFlags = tvRedirectToRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvRedirectToRegister.setOnClickListener {
            navigateToRegister()
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener(){view ->
            if(givenUsername.toString().equals(username.toString()) && givenPassword.toString().equals(password.toString())) {
                redirectToNavigationDrawer(view)
            }
            else if(givenUsername.toString().equals("Admin") && givenPassword.toString().equals("Password")) {
                redirectToNavigationDrawer(view)
                LoginWorker.FirstName = "Admin"

            }
            else{
                Toast.makeText(this, "There is no account with this username and password combination", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun navigateToRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    fun redirectToNavigationDrawer(view: View) {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }
}