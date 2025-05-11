package com.example.banner.frontend.views.register
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Usuario_
import com.example.banner.MainActivity
import com.example.banner.R

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val userIdInput: EditText = findViewById(R.id.user_id_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val roleSpinner: Spinner = findViewById(R.id.role_spinner)
        val registerButton: Button = findViewById(R.id.register_btn)

        // Lista de roles
        val roles = listOf("Profesor", "Students", "Admin")

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        var selectedRole = roles[1] // Valor predeterminado del Spinner

        // Escuchar la selección del Spinner
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRole = roles[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Manejar clic en el botón de registro
        registerButton.setOnClickListener {
            val userId = userIdInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Crear usuario (Simulación)
            val usuario = Usuario_(userId.toInt(), password, selectedRole)

            // Simular registro exitoso
            Toast.makeText(this, "Registro exitoso como ${usuario.role}", Toast.LENGTH_LONG).show()

            // Redirigir a MainActivity después de 2 segundos
            registerButton.postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra la actividad de registro para evitar volver atrás
            }, 1000)
        }
    }
}