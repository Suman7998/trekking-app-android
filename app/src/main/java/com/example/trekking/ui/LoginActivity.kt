package com.example.trekking.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.trekking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity(), RegistrationDialogFragment.OnRegistrationCompleteListener, ForgotPasswordDialogFragment.OnForgotListener {

    private lateinit var usernameEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var forgotBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEt = findViewById(R.id.etUsername)
        passwordEt = findViewById(R.id.etPassword)
        loginBtn = findViewById(R.id.btnLogin)
        registerBtn = findViewById(R.id.btnRegister)
        forgotBtn = findViewById(R.id.btnForgot)

        loginBtn.setOnClickListener { attemptLogin() }
        registerBtn.setOnClickListener { showRegistration() }
        forgotBtn.setOnClickListener { showForgotPassword() }
    }

    private fun attemptLogin() {
        val inputUser = usernameEt.text.toString().trim()
        val inputPass = passwordEt.text.toString()

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            return
        }
        val emailForAuth = "${inputUser.lowercase()}@app.local"
        loginBtn.isEnabled = false
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.signInWithEmailAndPassword(emailForAuth, inputPass).addOnCompleteListener { task ->
            loginBtn.isEnabled = true
            if (!task.isSuccessful) {
                Toast.makeText(this, task.exception?.localizedMessage ?: "Invalid credentials", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }
            val uid = auth.currentUser?.uid
            if (uid != null) {
                db.collection("users").document(uid).update("lastLoginAt", FieldValue.serverTimestamp())
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showRegistration() {
        val fm: FragmentManager = supportFragmentManager
        RegistrationDialogFragment().show(fm, "register")
    }

    private fun showForgotPassword() {
        val fm: FragmentManager = supportFragmentManager
        ForgotPasswordDialogFragment().show(fm, "forgot")
    }

    override fun onRegistrationComplete(username: String, password: String) {
        usernameEt.setText(username)
        passwordEt.setText(password)
        Toast.makeText(this, "Registered successfully. You can login now.", Toast.LENGTH_SHORT).show()
    }

    override fun onPasswordRecovered(username: String, password: String?) {
        if (password.isNullOrEmpty()) {
            Toast.makeText(this, "No password stored for $username. Default is cheryl/123.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Password for $username is $password", Toast.LENGTH_LONG).show()
        }
    }
}
