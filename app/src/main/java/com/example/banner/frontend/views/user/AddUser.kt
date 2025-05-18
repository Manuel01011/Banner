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
import org.json.JSONObject

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
            usuarioId.text.isNullOrEmpty() -> {
                usuarioId.error = "Enter user ID"
                false
            }
            !usuarioId.text.toString().matches(Regex("\\d+")) -> {
                usuarioId.error = "ID must be a number"
                false
            }
            usuarioPassword.text.isNullOrEmpty() -> {
                usuarioPassword.error = "Enter password"
                false
            }
            usuarioPassword.text.toString().length < 4 -> {
                usuarioPassword.error = "Password must be at least 4 characters"
                false
            }
            usuarioRole.text.isNullOrEmpty() -> {
                usuarioRole.error = "Enter role"
                false
            }
            !usuarioRole.text.toString().lowercase().matches(Regex("admin|teacher|student")) -> {
                usuarioRole.error = "Role must be admin, teacher or student"
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

        // Validar que el ID sea un número válido
        val userId = try {
            usuarioId.text.toString().toInt()
        } catch (e: NumberFormatException) {
            progressDialog.dismiss()
            usuarioId.error = "ID must be a number"
            return
        }

        val user = Usuario_(
            id = userId, // Usar el ID convertido a Int
            password = usuarioPassword.text.toString(),
            role = usuarioRole.text.toString().lowercase() // Convertir a minúsculas para consistencia
        )

        object : AsyncTask<Void, Void, Pair<Boolean, String?>>() {
            override fun doInBackground(vararg params: Void?): Pair<Boolean, String?> {
                return try {
                    val response = HttpHelper.sendRequest(
                        "users",
                        "POST",
                        user,
                        String::class.java
                    )
                    Log.d("API_RESPONSE", "Response: $response")

                    if (response != null) {
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.optBoolean("success", false)
                        val message = jsonResponse.optString("message", null)
                        Pair(success, message)
                    } else {
                        Pair(false, "No response from server")
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error saving user", e)
                    Pair(false, e.message)
                }
            }

            override fun onPostExecute(result: Pair<Boolean, String?>) {
                progressDialog.dismiss()
                if (result.first) {
                    Toast.makeText(this@AddUser, "User saved successfully", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    val errorMsg = result.second ?: "Error saving user"
                    Toast.makeText(
                        this@AddUser,
                        "Error: $errorMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.execute()
    }
}
