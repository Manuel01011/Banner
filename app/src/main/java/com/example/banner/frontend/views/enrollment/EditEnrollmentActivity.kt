package com.example.banner.frontend.views.enrollment

import android.app.Activity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editenrollment)

        etStudentId = findViewById(R.id.etStudentId)
        etGrupoId = findViewById(R.id.etGrupoId)
        etGrade = findViewById(R.id.etGrade)
        btnGuardar = findViewById(R.id.btnGuardar)

        val studentId = intent.getIntExtra("studentId", -1)
        val grupoId = intent.getIntExtra("grupoId", -1)
        val grade = intent.getDoubleExtra("grade", 0.0)

        etStudentId.setText(studentId.toString())
        etGrupoId.setText(grupoId.toString())
        etGrade.setText(grade.toString())

        btnGuardar.setOnClickListener {
            val resultIntent = intent
            resultIntent.putExtra("studentId", etStudentId.text.toString().toInt())
            resultIntent.putExtra("grupoId", etGrupoId.text.toString().toInt())
            resultIntent.putExtra("grade", etGrade.text.toString().toDouble())
            resultIntent.putExtra("position", intent.getIntExtra("position", -1))
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}