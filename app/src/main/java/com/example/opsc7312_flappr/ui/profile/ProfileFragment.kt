package com.example.opsc7312_flappr.ui.profile

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.opsc7312_flappr.ChangePassword
import com.example.opsc7312_flappr.R
import com.example.opsc7312_flappr.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvChangePassword = view.findViewById<TextView>(R.id.tvChangePassword)
        tvChangePassword.paintFlags = tvChangePassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvChangePassword.setOnClickListener {
            navigateToChngPass()
        }

    }

    fun navigateToChngPass() {
        val intent = Intent(activity, ChangePassword::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}