package com.paranoid.mao.firebaseauthexample

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

private const val RC_SIGN_IN = 123
private const val NAN_STRING = "Unknown"

class MainActivity : AppCompatActivity() {

    private val signInButton by lazy { findViewById<Button>(R.id.sign_in) }
    private val signOutButton by lazy { findViewById<Button>(R.id.sign_out) }

    // Chose authentication providers
    private val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    showToast("Sign In Success")
                } else {
                    if (response == null) {
                        showToast("Sign In Cancelled")
                        return
                    }

                    if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                        showToast("Error: No Internet Connection")
                        return
                    }

                    showToast("Error: Unknown")
                }
                updateUI()
            }
        }
    }

    private fun initListener() {
        signInButton.setOnClickListener { signIn() }
        signOutButton.setOnClickListener { signOut() }
    }

    private fun updateUI() {
        val user = FirebaseAuth.getInstance().currentUser
        val isSignedIn = user != null
        val username = user?.displayName ?: NAN_STRING
        val email = user?.email ?: NAN_STRING
        val creationDate = user?.metadata?.creationTimestamp?.let {
            DateUtils.formatDateTime(
                    this,
                    it,
                    DateUtils.FORMAT_SHOW_DATE)
        } ?: NAN_STRING
        val lastSignInDate = user?.metadata?.lastSignInTimestamp?.let {
            DateUtils.formatDateTime(
                    this,
                    it,
                    DateUtils.FORMAT_SHOW_DATE
            )
        } ?: NAN_STRING


        findViewById<Button>(R.id.sign_in).isEnabled = !isSignedIn
        findViewById<Button>(R.id.sign_out).isEnabled = isSignedIn
        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.email).text = email
        findViewById<TextView>(R.id.creation_date).text = creationDate
        findViewById<TextView>(R.id.last_sign_in_date).text = lastSignInDate

        val tokenView = findViewById<TextView>(R.id.token_id)
        tokenView.text = NAN_STRING
        user?.getIdToken(false)?.addOnCompleteListener {
            if (it.isSuccessful) {
                tokenView.text = it.result!!.token
            }
        }
    }

    private fun signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN
        )
    }

    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    updateUI()
                }
    }

}
