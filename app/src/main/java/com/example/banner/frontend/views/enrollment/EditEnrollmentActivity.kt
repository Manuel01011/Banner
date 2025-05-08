package com.example.banner.frontend.views.enrollment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditEnrollmentActivity : AppCompatActivity() {
    private lateinit var etStudentId: EditText
    private lateinit var etGrupoId: EditText
    private lateinit var etGrade: EditText
    private lateinit var btnGuardar: Button

    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_enrollment)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        etStudentId = findViewById(R.id.etStudentId)
        etGrupoId = findViewById(R.id.etGrupoId)
        etGrade = findViewById(R.id.etGrade)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        etStudentId.setText(intent.getIntExtra("studentId", -1).toString())
        etGrupoId.setText(intent.getIntExtra("grupoId", -1).toString())
        etGrade.setText(intent.getDoubleExtra("grade", 0.0).toString())
    }

    private fun setupSaveButton() {
        btnGuardar.setOnClickListener {
            if (validateFields()) {
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("studentId", etStudentId.text.toString().toInt())
                    putExtra("grupoId", etGrupoId.text.toString().toInt())
                    putExtra("grade", etGrade.text.toString().toDouble())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            etStudentId.text.isNullOrEmpty() -> {
                etStudentId.error = "Ingrese ID de estudiante"
                false
            }
            etGrupoId.text.isNullOrEmpty() -> {
                etGrupoId.error = "Ingrese ID de grupo"
                false
            }
            etGrade.text.isNullOrEmpty() -> {
                etGrade.error = "Ingrese la calificación"
                false
            }
            etGrade.text.toString().toDoubleOrNull() == null -> {
                etGrade.error = "Calificación inválida"
                false
            }
            else -> true
        }
    }
}