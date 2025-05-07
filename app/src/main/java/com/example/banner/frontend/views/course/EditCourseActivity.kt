package com.example.banner.frontend.views.course

import Course_
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditCourseActivity : AppCompatActivity() {
    private lateinit var course: Course_
    private lateinit var edtCod: EditText
    private lateinit var edtName: EditText
    private lateinit var edtCredits: EditText
    private lateinit var edtHours: EditText
    private lateinit var edtCicloId: EditText
    private lateinit var edtCareerCod: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_course)

        edtCod = findViewById(R.id.edtCod)
        edtName = findViewById(R.id.edtName)
        edtCredits = findViewById(R.id.edtCredits)
        edtHours = findViewById(R.id.edtHours)
        edtCicloId = findViewById(R.id.edtCicloId)
        edtCareerCod = findViewById(R.id.edtCareerCod)
        saveButton = findViewById(R.id.saveButton)

        // Obtener los datos del curso a editar
        val cod = intent.getIntExtra("cod", -1)
        val name = intent.getStringExtra("name") ?: ""
        val credits = intent.getIntExtra("credits", 0)
        val hours = intent.getIntExtra("hours", 0)
        val cicloId = intent.getIntExtra("cicloId", 0)
        val careerCod = intent.getIntExtra("careerCod", 0)

        course = Course_(cod, name, credits, hours, cicloId, careerCod)

        // Rellenar los campos con los datos actuales
        edtCod.setText(course.cod.toString())
        edtName.setText(course.name)
        edtCredits.setText(course.credits.toString())
        edtHours.setText(course.hours.toString())
        edtCicloId.setText(course.cicloId.toString())
        edtCareerCod.setText(course.careerCod.toString())

        saveButton.setOnClickListener {
            // Obtener los valores editados
            course.cod = edtCod.text.toString().toInt()
            course.name = edtName.text.toString()
            course.credits = edtCredits.text.toString().toInt()
            course.hours = edtHours.text.toString().toInt()
            course.cicloId = edtCicloId.text.toString().toInt()
            course.careerCod = edtCareerCod.text.toString().toInt()

            // Retornar los datos al llamar a la actividad
            val resultIntent = Intent()
            resultIntent.putExtra("cod", course.cod)
            resultIntent.putExtra("name", course.name)
            resultIntent.putExtra("credits", course.credits)
            resultIntent.putExtra("hours", course.hours)
            resultIntent.putExtra("cicloId", course.cicloId)
            resultIntent.putExtra("careerCod", course.careerCod)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}