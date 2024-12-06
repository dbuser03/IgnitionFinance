package com.unimib.ignitionfinance.data.remote.auth

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Activity to handle user authentication via email and password using Firebase.
 */
class EmailPasswordActivity : Activity() {

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    /**
     * Called when the activity is created. Initializes Firebase Authentication.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    /**
     * Called when the activity starts. Checks if a user is already signed in.
     */
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    /**
     * Creates a new account with the provided email and password.
     * @param email The email address to register.
     * @param password The password for the new account.
     */
    private fun createAccount(email: String, password: String) {
        if (!isValidEmail(email)) {
            Toast.makeText(baseContext, "Invalid email.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    val errorMessage = task.exception?.localizedMessage ?: "Authentication failed."
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    /**
     * Signs in a user with the provided email and password.
     * @param email The email address to sign in with.
     * @param password The password for the account.
     */
    private fun signIn(email: String, password: String) {
        if (!isValidEmail(email)) {
            Toast.makeText(baseContext, "Invalid email.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val errorMessage = task.exception?.localizedMessage ?: "Authentication failed."
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }