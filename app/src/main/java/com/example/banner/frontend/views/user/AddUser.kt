package com.example.banner.frontend.views.user

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Usuario_
import com.example.banner.R

class AddUser : AppCompatActivity() {
    private lateinit var usuarioId: EditText
    private lateinit var usuarioPassword: EditText
    private lateinit var usuarioRole: EditText
    private lateinit var saveUsuarioBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_user)

        // Inicializar las vistas
        usuarioId = findViewById(R.id.agregar_usuario_id)
        usuarioPassword = findViewById(R.id.agregar_usuario_password)
        usuarioRole = findViewById(R.id.agregar_usuario_role)
        saveUsuarioBtn = findViewById(R.id.btn_guardar_usuario)

        // Configurar el botón de guardar usuario
        saveUsuarioBtn.setOnClickListener {
            if (validateFields()) {
                saveUserToBackend()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            usuarioPassword.text.isNullOrEmpty() -> {
                usuarioPassword.error = "Enter password"
                false
            }
            usuarioRole.text.isNullOrEmpty() -> {
                usuarioRole.error = "Enter role"
                false
            }
            usuarioId.text.isNullOrEmpty() -> {
                usuarioId.error = "Enter user ID"
                false
            }
            else -> true
        }
    }

    private fun saveUserToBackend() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Saving user...")
            setCancelable(false)
            show()
        }

        val user = Usuario_(
            id = usuarioId.id,
            password = usuarioPassword.text.toString(),
            role = usuarioRole.text.toString(),
        )

        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return try {
                    val response = HttpHelper.sendRequest(
                        "users",
                        "POST",
                        user,
                        String::class.java
                    )
                    Log.d("API_RESPONSE", "Response: $response")
                    response != null && response.contains("\"success\":true")
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error saving user", e)
                    false
                }
            }

            override fun onPostExecute(success: Boolean) {
                progressDialog.dismiss()
                if (success) {
                    Toast.makeText(this@AddUser, "User saved successfully", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@AddUser,
                        "Error saving user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.execute()
    }
}
