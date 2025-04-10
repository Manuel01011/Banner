package com.example.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarCarrera : AppCompatActivity() {
    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var addBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_carrera) // Asegúrate de que este XML esté correctamente configurado

        cod = findViewById(R.id.agregar_cod)
        name = findViewById(R.id.agregar_name)
        title = findViewById(R.id.agregar_title)
        addBtn = findViewById(R.id.btn_agregar)

        // La edición del código es deshabilitada, pero si lo deseas, puedes habilitarla aquí.

        addBtn.setOnClickListener {
            // Validar si los campos no están vacíos
            val careerCod = cod.text.toString().toIntOrNull()
            val careerName = name.text.toString()
            val careerTitle = title.text.toString()

            if (careerCod != null && careerName.isNotBlank() && careerTitle.isNotBlank()) {
                // Si los datos son válidos, enviar los resultados
                val resultIntent = Intent().apply {
                    putExtra("cod", careerCod)
                    putExtra("name", careerName)
                    putExtra("title", careerTitle)
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
