package com.example.banner.frontend.views.group

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditGroupActivity : AppCompatActivity(){
    private lateinit var etId: EditText
    private lateinit var etNumber: EditText
    private lateinit var etYear: EditText
    private lateinit var etHorario: EditText
    private lateinit var etCourseCode: EditText
    private lateinit var etTeacherId: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_group)

        etId = findViewById(R.id.etId)
        etNumber = findViewById(R.id.etNumber)
        etYear = findViewById(R.id.etYear)
        etHorario = findViewById(R.id.etHorario)
        etCourseCode = findViewById(R.id.etCourseCode)
        etTeacherId = findViewById(R.id.etTeacherId)
        btnGuardar = findViewById(R.id.btnGuardar)

        val position = intent.getIntExtra("position", -1)
        etId.setText(intent.getIntExtra("id", -1).toString())
        etNumber.setText(intent.getIntExtra("number", -1).toString())
        etYear.setText(intent.getIntExtra("year", -1).toString())
        etHorario.setText(intent.getStringExtra("horario"))
        etCourseCode.setText(intent.getIntExtra("courseCode", -1).toString())
        etTeacherId.setText(intent.getIntExtra("teacherId", -1).toString())

        btnGuardar.setOnClickListener {
            val resultIntent = intent
            resultIntent.putExtra("id", etId.text.toString().toInt())
            resultIntent.putExtra("number", etNumber.text.toString().toInt())
            resultIntent.putExtra("year", etYear.text.toString().toInt())
            resultIntent.putExtra("horario", etHorario.text.toString())
            resultIntent.putExtra("courseCode", etCourseCode.text.toString().toInt())
            resultIntent.putExtra("teacherId", etTeacherId.text.toString().toInt())
            resultIntent.putExtra("position", position)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}