package com.example.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarCurso : AppCompatActivity() {
    private lateinit var courseCode: EditText
    private lateinit var courseName: EditText
    private lateinit var credits: EditText
    private lateinit var hours: EditText
    private lateinit var cycleId: EditText
    private lateinit var careerCode: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_curso)

        // Asocia las vistas de la interfaz con las variables de Kotlin
        courseCode = findViewById(R.id.agregar_cod_curso)
        courseName = findViewById(R.id.agregar_nombre)
        credits = findViewById(R.id.agregar_creditos)
        hours = findViewById(R.id.agregar_horas)
        cycleId = findViewById(R.id.agregar_id_ciclo)
        careerCode = findViewById(R.id.agregar_cod_carrera)
        saveBtn = findViewById(R.id.btn_guardar_curso)

        saveBtn.setOnClickListener {
            // Obtener los valores de los campos de texto
            val courseCodeValue = courseCode.text.toString().toIntOrNull()
            val courseNameValue = courseName.text.toString()
            val creditsValue = credits.text.toString().toIntOrNull()
            val hoursValue = hours.text.toString().toIntOrNull()
            val cycleIdValue = cycleId.text.toString().toIntOrNull()
            val careerCodeValue = careerCode.text.toString().toIntOrNull()

            // Validar si los campos no están vacíos
            if (courseCodeValue != null && courseNameValue.isNotBlank() &&
                creditsValue != null && hoursValue != null &&
                cycleIdValue != null && careerCodeValue != null) {
                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("courseCode", courseCodeValue)
                    putExtra("courseName", courseNameValue)
                    putExtra("credits", creditsValue)
                    putExtra("hours", hoursValue)
                    putExtra("cycleId", cycleIdValue)
                    putExtra("careerCode", careerCodeValue)
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
