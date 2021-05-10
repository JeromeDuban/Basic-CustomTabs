package com.jduban.customtabs

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CallbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.dataString?.let {
            val redirectUri = Uri.parse(intent.dataString)

            val parameters = redirectUri.queryParameterNames

            when {
                parameters.contains("code") -> {
                    Toast.makeText(
                        this@CallbackActivity,
                        "code = ${redirectUri.getQueryParameter("code")}",
                        Toast.LENGTH_SHORT
                    ).show()

                    this@CallbackActivity.finish()
                }
                parameters.contains("error") -> {
                    val error = redirectUri.getQueryParameter("error")
                    val errorDescription = redirectUri.getQueryParameter("error_description")

                    // Erreurs connues :
                    // "invalid_scope" : scope invalide renseigné,
                    // "access_denied" : l'utilisateur a refusé les consentements
                    Toast.makeText(this@CallbackActivity, "$error : $errorDescription", Toast.LENGTH_SHORT).show()
                    this@CallbackActivity.finish()
                }
                else -> {
                    Toast.makeText(this@CallbackActivity, "Unknown error", Toast.LENGTH_SHORT).show()
                    this@CallbackActivity.finish()
                }
            }
        } ?: kotlin.run {
            Toast.makeText(this@CallbackActivity, "Not coming from callback", Toast.LENGTH_SHORT).show()
            this@CallbackActivity.finish()
        }

    }
}