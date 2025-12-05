package com.example.trekking.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.trekking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationDialogFragment : DialogFragment() {

    interface OnRegistrationCompleteListener {
        fun onRegistrationComplete(username: String, password: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_registration, null)

        val nameEt: EditText = view.findViewById(R.id.etName)
        val ageEt: EditText = view.findViewById(R.id.etAge)
        val locationEt: EditText = view.findViewById(R.id.etLocation)
        val phoneEt: EditText = view.findViewById(R.id.etPhone)
        val usernameEt: EditText = view.findViewById(R.id.etRegUsername)
        val passwordEt: EditText = view.findViewById(R.id.etRegPassword)
        val confirmEt: EditText = view.findViewById(R.id.etConfirmPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("New Registration")
            .setView(view)
            .setPositiveButton("Register", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btn: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btn.setOnClickListener {
                val username = usernameEt.text.toString().trim()
                val password = passwordEt.text.toString()
                val confirm = confirmEt.text.toString()
                val name = nameEt.text.toString().trim()
                val age = ageEt.text.toString().trim()
                val location = locationEt.text.toString().trim()
                val phone = phoneEt.text.toString().trim()

                if (name.isEmpty() || age.isEmpty() || location.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password != confirm) {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val usernameNorm = username.lowercase()
                val emailForAuth = "$usernameNorm@app.local"

                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()

                btn.isEnabled = false

                auth.createUserWithEmailAndPassword(emailForAuth, password).addOnCompleteListener { createTask ->
                    if (!createTask.isSuccessful) {
                        btn.isEnabled = true
                        Toast.makeText(requireContext(), createTask.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val uid = auth.currentUser?.uid ?: run {
                        btn.isEnabled = true
                        Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val usernamesRef = db.collection("usernames").document(usernameNorm)
                    val usersRef = db.collection("users").document(uid)

                    db.runTransaction { tr ->
                        val exists = tr.get(usernamesRef).exists()
                        if (exists) throw IllegalStateException("USERNAME_TAKEN")
                        tr.set(usernamesRef, mapOf("uid" to uid))
                        tr.set(usersRef, mapOf(
                            "username" to username,
                            "name" to name,
                            "age" to age,
                            "location" to location,
                            "phone" to phone,
                            "createdAt" to FieldValue.serverTimestamp()
                        ))
                        null
                    }.addOnSuccessListener {
                        btn.isEnabled = true
                        (activity as? OnRegistrationCompleteListener)?.onRegistrationComplete(username, password)
                        dismiss()
                    }.addOnFailureListener { txe ->
                        auth.currentUser?.delete()
                        btn.isEnabled = true
                        val msg = if (txe is IllegalStateException && txe.message == "USERNAME_TAKEN") "Username already exists" else txe.localizedMessage ?: "Registration failed"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return dialog
    }
}
