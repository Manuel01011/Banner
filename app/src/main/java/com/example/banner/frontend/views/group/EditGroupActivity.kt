package com.example.banner.frontend.views.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditGroupActivity : AppCompatActivity() {
    private lateinit var etId: EditText
    private lateinit var etNumber: EditText
    private lateinit var etYear: EditText
    private lateinit var etHorario: EditText
    private lateinit var etCourseCode: EditText
    private lateinit var etTeacherId: EditText
    private lateinit var btnGuardar: Button

    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_group)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        etId = findViewById(R.id.etId)
        etNumber = findViewById(R.id.etNumber)
        etYear = findViewById(R.id.etYear)
        etHorario = findViewById(R.id.etHorario)
        etCourseCode = findViewById(R.id.etCourseCode)
        etTeacherId = findViewById(R.id.etTeacherId)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        etId.setText(intent.getIntExtra("id", -1).toString())
        etNumber.setText(intent.getIntExtra("number", -1).toString())
        etYear.setText(intent.getIntExtra("year", -1).toString())
        etHorario.setText(intent.getStringExtra("horario"))
        etCourseCode.setText(intent.getIntExtra("courseCode", -1).toString())
        etTeacherId.setText(intent.getIntExtra("teacherId", -1).toString())
    }

    private fun setupSaveButton() {
        btnGuardar.setOnClickListener {
            if (validateFields()) {
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("id", etId.text.toString().toInt())
                    putExtra("number", etNumber.text.toString().toInt())
                    putExtra("year", etYear.text.toString().toInt())
                    putExtra("horario", etHorario.text.toString())
                    putExtra("courseCode", etCourseCode.text.toString().toInt())
                    putExtra("teacherId", etTeacherId.text.toString().toInt())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            etId.text.isNullOrEmpty() -> {
                etId.error = "Ingrese ID del grupo"
                false
            }
            etNumber.text.isNullOrEmpty() -> {
                etNumber.error = "Ingrese número de grupo"
                false
            }
            etYear.text.isNullOrEmpty() -> {
                etYear.error = "Ingrese el año"
                false
            }
            etHorario.text.isNullOrEmpty() -> {
                etHorario.error = "Ingrese el horario"
                false
            }
            etCourseCode.text.isNullOrEmpty() -> {
                etCourseCode.error = "Ingrese código de curso"
                false
            }
            etTeacherId.text.isNullOrEmpty() -> {
                etTeacherId.error = "Ingrese ID del profesor"
                false
            }
            else -> true
        }
    }
}