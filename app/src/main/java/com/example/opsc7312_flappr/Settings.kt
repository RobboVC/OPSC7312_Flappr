package com.example.opsc7312_flappr

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Settings : AppCompatActivity() {
    // Declare variables to store user preferences
    private var isMetric = true
    private var maxDistance = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        val switchUnits = findViewById<Switch>(R.id.switchUnits)
        val sbMaxDistance = findViewById<SeekBar>(R.id.sbMaxDistance)
        val etMaxDistance = findViewById<EditText>(R.id.etMaxDistance)

        // Set listeners for the switch and seekbar
        switchUnits.setOnCheckedChangeListener { buttonView, isChecked ->
            isMetric = isChecked
            updateMaxDistanceLabel(isMetric)
        }
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

    private fun saveUserPreferences(isMetric: Boolean, maxDistance: Int) {
        // Implement saving user preferences to variables or storage here
    }
}