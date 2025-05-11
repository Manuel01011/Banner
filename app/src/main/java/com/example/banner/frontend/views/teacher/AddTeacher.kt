package com.example.banner.frontend.views.teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class AddTeacher : AppCompatActivity() {
    private lateinit var profesorId: EditText
    private lateinit var profesorName: EditText
    private lateinit var profesorTel: EditText
    private lateinit var profesorEmail: EditText
    private lateinit var saveProfesorBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_teacher)

        // Inicializar las vistas
        profesorId = findViewById(R.id.agregar_profesor_id)
        profesorName = findViewById(R.id.agregar_profesor_name)
        profesorTel = findViewById(R.id.agregar_profesor_telNumber)
        profesorEmail = findViewById(R.id.agregar_profesor_email)
        saveProfesorBtn = findViewById(R.id.btn_guardar_profesor)

        // Configurar el botón de guardar teacher
        saveProfesorBtn.setOnClickListener {
            // Obtener los valores de los campos de texto
            val profesorIdValue = profesorId.text.toString().toIntOrNull()
            val profesorNameValue = profesorName.text.toString()
            val profesorTelValue = profesorTel.text.toString()
            val profesorEmailValue = profesorEmail.text.toString()

            // Validar si los campos no están vacíos
            if (profesorIdValue != null && profesorNameValue.isNotEmpty() &&
                profesorTelValue.isNotEmpty() && profesorEmailValue.isNotEmpty()) {

                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("profesorId", profesorIdValue)
                    putExtra("profesorName", profesorNameValue)
                    putExtra("profesorTel", profesorTelValue)
                    putExtra("profesorEmail", profesorEmailValue)
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
