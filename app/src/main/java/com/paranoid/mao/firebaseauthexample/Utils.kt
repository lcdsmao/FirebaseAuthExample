package com.paranoid.mao.firebaseauthexample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT)

fun Context.showToast(@StringRes resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT)

inline fun <reified T : Activity> Context.startActivity(options: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java).apply(options)
    startActivity(intent)
}
