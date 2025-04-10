package com.example.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarMatricula : AppCompatActivity() {
    private lateinit var studentId: EditText
    private lateinit var grupoId: EditText
    private lateinit var grade: EditText
    private lateinit var saveMatriculaBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_matricula)

        // Inicializar las vistas
        studentId = findViewById(R.id.agregar_id_estudiante)
        grupoId = findViewById(R.id.agregar_id_grupo)
        grade = findViewById(R.id.agregar_calificacion)
        saveMatriculaBtn = findViewById(R.id.btn_guardar_matricula)

        // Configurar el botón de guardar matrícula
        saveMatriculaBtn.setOnClickListener {
            // Obtener los valores de los campos de texto
            val studentIdValue = studentId.text.toString().toIntOrNull()
            val grupoIdValue = grupoId.text.toString().toIntOrNull()
            val gradeValue = grade.text.toString().toDoubleOrNull()

            // Validar si los campos no están vacíos
            if (studentIdValue != null && grupoIdValue != null && gradeValue != null) {

                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("studentId", studentIdValue)
                    putExtra("grupoId", grupoIdValue)
                    putExtra("grade", gradeValue)
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
