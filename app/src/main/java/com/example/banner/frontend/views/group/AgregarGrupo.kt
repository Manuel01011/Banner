package com.example.banner.frontend.views.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class AgregarGrupo : AppCompatActivity() {
    private lateinit var grupoId: EditText
    private lateinit var grupoNumber: EditText
    private lateinit var grupoYear: EditText
    private lateinit var grupoHorario: EditText
    private lateinit var grupoCourseCod: EditText
    private lateinit var grupoTeacherId: EditText
    private lateinit var saveGrupoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_grupo)

        grupoId = findViewById(R.id.agregar_grupo_id)
        grupoNumber = findViewById(R.id.agregar_grupo_number)
        grupoYear = findViewById(R.id.agregar_grupo_year)
        grupoHorario = findViewById(R.id.agregar_grupo_horario)
        grupoCourseCod = findViewById(R.id.agregar_grupo_courseCod)
        grupoTeacherId = findViewById(R.id.agregar_grupo_teacherId)
        saveGrupoBtn = findViewById(R.id.btn_guardar_grupo)

        saveGrupoBtn.setOnClickListener {
            // Obtener los valores de los campos de texto
            val grupoIdValue = grupoId.text.toString().toIntOrNull()
            val grupoNumberValue = grupoNumber.text.toString().toIntOrNull()
            val grupoYearValue = grupoYear.text.toString().toIntOrNull()
            val grupoHorarioValue = grupoHorario.text.toString()
            val grupoCourseCodValue = grupoCourseCod.text.toString().toIntOrNull()
            val grupoTeacherIdValue = grupoTeacherId.text.toString().toIntOrNull()

            // Validar si los campos no están vacíos
            if (grupoIdValue != null && grupoNumberValue != null && grupoYearValue != null &&
                grupoHorarioValue.isNotBlank() && grupoCourseCodValue != null && grupoTeacherIdValue != null) {

                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("grupoId", grupoIdValue)
                    putExtra("grupoNumber", grupoNumberValue)
                    putExtra("grupoYear", grupoYearValue)
                    putExtra("grupoHorario", grupoHorarioValue)
                    putExtra("grupoCourseCod", grupoCourseCodValue)
                    putExtra("grupoTeacherId", grupoTeacherIdValue)
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
