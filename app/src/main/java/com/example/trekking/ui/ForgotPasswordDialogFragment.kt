package com.example.trekking.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.trekking.R

class ForgotPasswordDialogFragment : DialogFragment() {

    interface OnForgotListener {
        fun onPasswordRecovered(username: String, password: String?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password, null)
        val usernameEt: EditText = view.findViewById(R.id.etForgotUsername)

        return AlertDialog.Builder(requireContext())
            .setTitle("Forgot Password")
            .setView(view)
            .setPositiveButton("Retrieve") { _, _ ->
                val username = usernameEt.text.toString().trim()
                val prefs = requireContext().getSharedPreferences("users", Context.MODE_PRIVATE)
                val pass = prefs.getString("user_$username", null)
                (activity as? OnForgotListener)?.onPasswordRecovered(username, pass)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
