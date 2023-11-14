package com.example.opsc7312_flappr

import EBirdApiServiceKotlin
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Settings : AppCompatActivity() {
    // Declare variables to store user preferences
    private var isMetric = true
    private var maxDistance = 5

    private lateinit var btnBack: MaterialButton
    private lateinit var etMaxDistance: EditText

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        val switchUnits = findViewById<Switch>(R.id.switchUnits)
        val sbMaxDistance = findViewById<SeekBar>(R.id.sbMaxDistance)
        val etMaxDistance = findViewById<EditText>(R.id.etMaxDistance)

        fetchMaxDistance(EBirdApiServiceKotlin.userID){etMaxDistance.setText(maxDistance.toString())}

        btnBack = findViewById(R.id.btnBack)

        // Set an OnClickListener for the back button
        btnBack.setOnClickListener {
            // Handle the back button click event here
            startActivity(Intent(this@Settings, HomePage::class.java))
            finish() // Finish the current activity to return to the home page
        }

        // Set listeners for the switch and seekbar
        switchUnits.isChecked = EBirdApiServiceKotlin.getUnits()
        updateMaxDistanceLabel(EBirdApiServiceKotlin.getUnits())
        //maxDistance = EBirdApiServiceKotlin.getMaxDistance()
        etMaxDistance.setText(maxDistance.toString())

        // Set listeners for the switch and seekbar
        switchUnits.setOnCheckedChangeListener { buttonView, isChecked ->
            isMetric = isChecked
            updateMaxDistanceLabel(isMetric)
        }

        etMaxDistance.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    val value = text.toIntOrNull()
                    if (value != null && value in 1..50) {
                        maxDistance = value
                        sbMaxDistance.progress = maxDistance
                        etMaxDistance.error = null
                    } else {
                        etMaxDistance.error = "Enter a value from 1 to 50"
                    }
                }
            }
        })

        sbMaxDistance.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                maxDistance = progress
                etMaxDistance.setText(maxDistance.toString())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Set listener for the Save button
        findViewById<View>(R.id.btnSaveSettings).setOnClickListener { v: View? ->
            // Save user preferences to variables or perform desired actions
            saveUserPreferences(isMetric, maxDistance)
            EBirdApiServiceKotlin.dist = maxDistance

            val newSettings = hashMapOf(
                "maxDistance" to maxDistance
            )

            db.collection("userSettings").document(EBirdApiServiceKotlin.userID)
                .set(newSettings, SetOptions.merge())

        }
    }



    // Set listener for the Save button
    private fun saveUserPreferences(isMetric: Boolean, maxDistance: Int) {
    EBirdApiServiceKotlin.setUnits(isMetric)
    EBirdApiServiceKotlin.setMaxDistance(maxDistance)
    }

    fun fetchMaxDistance(userId: String?, callback: (Int) -> Unit) {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Reference to the userSettings document
        val docRef = db.collection("userSettings").document(userId!!)

        // Get the document from Firestore
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    // Document found, retrieve maxDistance
                    val maxDistance = document.getLong("maxDistance")

                    // Use the maxDistance value in your application
                    if (maxDistance != null) {
                        this.maxDistance = maxDistance.toInt()
                        callback(maxDistance.toInt())
                    } else {
                        Toast.makeText(this, "Error: maxDistance is not a valid number", Toast.LENGTH_SHORT).show()
                        callback(1) // Provide a default value in case of an error
                    }
                } else {
                    Toast.makeText(this, "Error: no user settings for this user", Toast.LENGTH_SHORT).show()
                    callback(1) // Provide a default value in case of an error
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                callback(1) // Provide a default value in case of an error
            }
        }
    }



    private fun updateMaxDistanceLabel(isMetric: Boolean) {
        val tvUnits = findViewById<TextView>(R.id.tvUnits)
        if (isMetric) {
            tvUnits.text = "Kilometres"
        } else {
            tvUnits.text = "Miles"
        }
    }

}
