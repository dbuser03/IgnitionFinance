package com.unimib.ignitionfinance.Auth

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class EmailPasswordActivity : Activity() {

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // Lifecycle method called when the activity is created
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Authentication
        auth = Firebase.auth
    }

    // Lifecycle method called when the activity becomes visible
    public override fun onStart() {
        super.onStart()
        // Check if a user is already signed in and update the UI accordingly
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    // Method to create a new account using email and password
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation successful, update the UI with the user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Account creation failed, display a message to the user
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }
    // Method to create a new account using email and password
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation successful, update the UI with the user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Account creation failed, display a message to the user
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }
    // Method to send an email verification to the currently signed-in user
    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email verification has been sent (no additional action here)
            }
    }