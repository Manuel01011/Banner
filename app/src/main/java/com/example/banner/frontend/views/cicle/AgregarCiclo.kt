package com.example.banner.frontend.views.cicle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class AgregarCiclo : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var year: EditText
    private lateinit var number: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var isActive: CheckBox
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_ciclo)

        id = findViewById(R.id.agregar_id)
        year = findViewById(R.id.agregar_year)
        number = findViewById(R.id.agregar_number)
        startDate = findViewById(R.id.agregar_fecha_inicio)
        endDate = findViewById(R.id.agregar_fecha_fin)
        isActive = findViewById(R.id.agregar_activo)
        saveBtn = findViewById(R.id.btn_guardar_ciclo)

        saveBtn.setOnClickListener {
            // Validar si los campos no están vacíos
            val cycleId = id.text.toString().toIntOrNull()
            val cycleYear = year.text.toString().toIntOrNull()
            val cycleNumber = number.text.toString().toIntOrNull()
            val cycleStartDate = startDate.text.toString()
            val cycleEndDate = endDate.text.toString()

            if (cycleId != null && cycleYear != null && cycleNumber != null &&
                cycleStartDate.isNotBlank() && cycleEndDate.isNotBlank()) {
                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("id", cycleId)
                    putExtra("year", cycleYear)
                    putExtra("number", cycleNumber)
                    putExtra("startDate", cycleStartDate)
                    putExtra("endDate", cycleEndDate)
                    putExtra("isActive", isActive.isChecked)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Finalizar la actividad y volver a la anterior
            } else {
                // Mostrar mensaje si los campos están vacíos
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
