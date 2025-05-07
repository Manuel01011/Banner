package com.example.banner.frontend.views.student

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class AddStudent : AppCompatActivity() {

    private lateinit var studentId: EditText
    private lateinit var studentName: EditText
    private lateinit var studentTelNumber: EditText
    private lateinit var studentEmail: EditText
    private lateinit var studentBornDate: EditText
    private lateinit var studentCareerCode: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_student)

        // Asociamos las vistas con las variables
        studentId = findViewById(R.id.agregar_estudiante_id)
        studentName = findViewById(R.id.agregar_estudiante_name)
        studentTelNumber = findViewById(R.id.agregar_estudiante_telNumber)
        studentEmail = findViewById(R.id.agregar_estudiante_email)
        studentBornDate = findViewById(R.id.agregar_estudiante_bornDate)
        studentCareerCode = findViewById(R.id.agregar_estudiante_careerCod)
        saveBtn = findViewById(R.id.btn_guardar_estudiante)

        // Configuramos el botón para guardar el estudiante
        saveBtn.setOnClickListener {
            // Obtener los valores de los campos
            val studentIdValue = studentId.text.toString().toIntOrNull()
            val studentNameValue = studentName.text.toString()
            val studentTelNumberValue = studentTelNumber.text.toString()
            val studentEmailValue = studentEmail.text.toString()
            val studentBornDateValue = studentBornDate.text.toString()
            val studentCareerCodeValue = studentCareerCode.text.toString().toIntOrNull()

            // Validar que todos los campos no estén vacíos o nulos
            if (studentIdValue != null && studentNameValue.isNotBlank() &&
                studentTelNumberValue.isNotBlank() && studentEmailValue.isNotBlank() &&
                studentBornDateValue.isNotBlank() && studentCareerCodeValue != null) {
                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("studentId", studentIdValue)
                    putExtra("studentName", studentNameValue)
                    putExtra("studentTelNumber", studentTelNumberValue)
                    putExtra("studentEmail", studentEmailValue)
                    putExtra("studentBornDate", studentBornDateValue)
                    putExtra("studentCareerCode", studentCareerCodeValue)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Finalizamos la actividad y volvemos a la anterior
            } else {
                // Mostrar mensaje si los campos están vacíos o inválidos
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}