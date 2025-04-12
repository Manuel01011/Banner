package com.example.banner.frontend.views.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditarUsuario: AppCompatActivity() {
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRole: AutoCompleteTextView
    private var userId: Int = -1
    private var userIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edituser)

        // Inicializar los campos
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRole = findViewById(R.id.editTextRole)

        // Lista de roles predefinidos
        val roleOptions = listOf("Student", "Professor", "Admin")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roleOptions)
        editTextRole.setAdapter(roleAdapter)

        // Mostrar todas las opciones al hacer clic
        editTextRole.setOnClickListener {
            editTextRole.showDropDown()
        }

        // También mostrar la lista cuando gana foco
        editTextRole.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) editTextRole.showDropDown()
        }

        // Obtener datos del intent
        userId = intent.getIntExtra("usuarioId", -1)
        userIndex = intent.getIntExtra("userIndex", -1)
        val userPass = intent.getStringExtra("usuarioPassword")
        val userRole = intent.getStringExtra("usuarioRole")

        // Cargar datos en los campos
        editTextPassword.setText(userPass)
        editTextRole.setText(userRole, false) // <== evita filtrar opciones

        // Botón de guardar
        findViewById<Button>(R.id.btnGuardarUsuario).setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("usuarioId", userId)
                putExtra("usuarioPassword", editTextPassword.text.toString())
                putExtra("usuarioRole", editTextRole.text.toString())
                putExtra("userIndex", userIndex)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}