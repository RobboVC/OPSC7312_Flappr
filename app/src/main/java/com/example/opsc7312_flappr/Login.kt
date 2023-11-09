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
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val tvRedirectToRegister = findViewById<TextView>(R.id.tvRedirectToRegister)
        tvRedirectToRegister.paintFlags = tvRedirectToRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvRedirectToRegister.setOnClickListener {
            navigateToRegister()
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener(){view ->
            firebaseAuth.signInWithEmailAndPassword(findViewById<TextView>(R.id.emailEditText).text.toString(), findViewById<TextView>(R.id.etPassword).text.toString())
                .addOnSuccessListener { redirectToNavigationDrawer(view) }
                .addOnFailureListener(){Toast.makeText(this, it.localizedMessage.toString(), Toast.LENGTH_SHORT).show()}


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