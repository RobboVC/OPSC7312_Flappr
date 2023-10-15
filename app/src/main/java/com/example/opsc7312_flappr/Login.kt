package com.example.opsc7312_flappr

import LoginWorker
import android.content.Intent
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
        val AdminUsername = "Admin"
        val AdminPassword = "Admin"
        val name = i.getStringExtra("name")
        val btnGoToReg = findViewById<Button>(R.id.btnGoToRegister)

        btnGoToReg.setOnClickListener(){
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener(){
            if(givenUsername.toString().equals(username.toString()) && givenPassword.toString().equals(password.toString())) {
                redirectToNavigationDrawer()
            }
            else if(givenUsername.toString().equals("Admin") && givenPassword.toString().equals("Password")) {
                redirectToNavigationDrawer()
            }
            else{
                Toast.makeText(this, "There is no account with this username and password combination" + username + password, Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
    fun redirectToNavigationDrawer() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }
}