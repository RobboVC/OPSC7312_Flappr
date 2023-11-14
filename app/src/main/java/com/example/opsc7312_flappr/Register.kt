package com.example.opsc7312_flappr

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Register : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        val txtUsername = findViewById<TextView>(R.id.emailEditText)
        val txtPassword = findViewById<TextView>(R.id.etPassword)
        val txtName = findViewById<TextView>(R.id.etName)
        var username = txtUsername.text
        var password = txtPassword.text
        var name = txtName.text
        val btnLogin = findViewById<TextView>(R.id.tvSubtitleClick)
        val btnSignUp = findViewById<Button>(R.id.btnRegister)

        btnLogin.paintFlags = btnLogin.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btnLogin.setOnClickListener(){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener(){
            var allFilled = true
            if(username.isNullOrEmpty() or password.isNullOrEmpty() or name.isNullOrEmpty()){
                allFilled = false
            }


            if(allFilled && isValidEmail(username.toString())){
                registerNewUser(name.toString(), username.toString(), password.toString())
            }
        }
    }
    fun isValidEmail(target: CharSequence?): Boolean {
        //source - https://stackoverflow.com/a/7882950
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    private fun registerNewUser(Name: String, Email: String, Password: String){
        var passwordResult = validateUserPassword(Password)

        if(passwordResult == "")
        {
            firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        val userInfo = firebaseAuth.currentUser!!.uid
                        val userSettings = hashMapOf(
                            "userId" to userInfo,
                            "maxDistance" to 5
                        )
                        db.collection("userSettings").document(userInfo)
                            .set(userSettings, SetOptions.merge()).addOnSuccessListener {runOnUiThread {
                                Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                            }
                                }
                            .addOnFailureListener{Toast.makeText(this, "Failed to register user's settings", Toast.LENGTH_SHORT).show()}
                    }
                    else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else{
            Toast.makeText(this, passwordResult, Toast.LENGTH_SHORT).show()
        }
    }

    fun validateUserPassword(attemptedPassword : String): String
    {

        //the errors found with the users password
        var validationErrors = ""

        //check the length of the password
        if (attemptedPassword.length < 8)
        {
            validationErrors += "Password too short, password must be 8 or more characters long" + "\n"
        }

        //check the amount of numbers in the password
        if (attemptedPassword.count(Char::isDigit) == 0)
        {
            validationErrors+="Password needs to contain at least 1 digit"+ "\n"
        }

        //check if the password contains any lower case characters
        if (!attemptedPassword.any(Char::isLowerCase))
        {
            validationErrors+="Password needs to contain at least 1 lower case character"+ "\n"
        }

        //check if the user password contains any uppercase characters
        if (!attemptedPassword.any(Char::isUpperCase))
        {
            validationErrors+="Password needs to contain at least 1 upper case character"+ "\n"
        }

        //check if the users passwords first character is uppercase
        if (!attemptedPassword[0].isUpperCase())
        {
            validationErrors+="Password needs to start with an upper case character"+ "\n"
        }

        //check if the users password contains a valid special character
        if (!attemptedPassword.any { it in "-?!@#$%^&*" })
        {
            validationErrors+="Password needs to contain one of the following special characters: -?!@#$%^&*"+ "\n"
        }

        //return the password errors
        return validationErrors


    }
}