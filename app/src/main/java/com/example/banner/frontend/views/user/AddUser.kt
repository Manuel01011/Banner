package com.example.banner.frontend.views.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            // Obtener los valores de los campos de texto
            val usuarioIdValue = usuarioId.text.toString().toIntOrNull()
            val usuarioPasswordValue = usuarioPassword.text.toString()
            val usuarioRoleValue = usuarioRole.text.toString()

            // Validar si los campos no están vacíos
            if (usuarioIdValue != null && usuarioPasswordValue.isNotEmpty() && usuarioRoleValue.isNotEmpty()) {
                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("usuarioId", usuarioIdValue)
                    putExtra("usuarioPassword", usuarioPasswordValue)
                    putExtra("usuarioRole", usuarioRoleValue)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Finalizar la actividad y volver a la anterior
            } else {
                // Mostrar mensaje si los campos están vacíos o inválidos
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
