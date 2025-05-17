package com.example.banner.frontend.views.user

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Usuario_
import com.example.banner.R

class EditUser: AppCompatActivity() {
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRole: AutoCompleteTextView
    private var userId: Int = -1
    private var userIndex: Int = -1
    private lateinit var btnGuardarUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_user)

        // Inicializar los campos
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRole = findViewById(R.id.editTextRole)
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario)


        // Lista de roles predefinidos
        val roleOptions = listOf("Student", "Teacher", "Admin")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roleOptions)
        editTextRole.setAdapter(roleAdapter)

        // Obtener datos del intent
        userId = intent.getIntExtra("usuarioId", -1)
        userIndex = intent.getIntExtra("userIndex", -1)
        val userPass = intent.getStringExtra("usuarioPassword") ?: ""
        val userRole = intent.getStringExtra("usuarioRole") ?: ""

        // Cargar datos en los campos
        editTextPassword.setText(userPass)
        editTextRole.setText(userRole, false)

        btnGuardarUsuario.setOnClickListener {
            if (validateFields()) {
                updateUserInBackend()
            }
        }
    }
    private fun validateFields(): Boolean {
        return when {
            editTextPassword.text.isNullOrEmpty() -> {
                editTextPassword.error = "Enter password"
                false
            }
            editTextRole.text.isNullOrEmpty() -> {
                editTextRole.error = "Select role"
                false
            }
            else -> true
        }
    }

    private fun updateUserInBackend() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Updating user...")
            setCancelable(false)
            show()
        }

        val updatedUser = Usuario_(
            id = userId,
            password = editTextPassword.text.toString(),
            role = editTextRole.text.toString()
        )

        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return try {
                    val response = HttpHelper.sendRequest(
                        "users",
                        "PUT",
                        updatedUser,
                        String::class.java
                    )
                    response != null && response.contains("\"success\":true")
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error updating user", e)
                    false
                }
            }

            override fun onPostExecute(success: Boolean) {
                progressDialog.dismiss()
                if (success) {
                    val resultIntent = Intent().apply {
                        putExtra("userIndex", userIndex)
                        putExtra("usuarioId", userId)
                        putExtra("usuarioPassword", updatedUser.password)
                        putExtra("usuarioRole", updatedUser.role)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                    Toast.makeText(this@EditUser, "User updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@EditUser,
                        "Error updating user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.execute()
    }
}