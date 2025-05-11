package com.example.banner.frontend.views.course
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditCourseActivity : AppCompatActivity() {
    private lateinit var edtCod: EditText
    private lateinit var edtName: EditText
    private lateinit var edtCredits: EditText
    private lateinit var edtHours: EditText
    private lateinit var edtCicloId: EditText
    private lateinit var edtCareerCod: EditText
    private lateinit var saveButton: Button

    private var position: Int = -1  // Added to track position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_course)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        edtCod = findViewById(R.id.edtCod)
        edtName = findViewById(R.id.edtName)
        edtCredits = findViewById(R.id.edtCredits)
        edtHours = findViewById(R.id.edtHours)
        edtCicloId = findViewById(R.id.edtCicloId)
        edtCareerCod = findViewById(R.id.edtCareerCod)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        edtCod.setText(intent.getIntExtra("cod", 0).toString())
        edtName.setText(intent.getStringExtra("name"))
        edtCredits.setText(intent.getIntExtra("credits", 0).toString())
        edtHours.setText(intent.getIntExtra("hours", 0).toString())
        edtCicloId.setText(intent.getIntExtra("cicloId", 0).toString())
        edtCareerCod.setText(intent.getIntExtra("careerCod", 0).toString())
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateFields()) {
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("cod", edtCod.text.toString().toInt())
                    putExtra("name", edtName.text.toString())
                    putExtra("credits", edtCredits.text.toString().toInt())
                    putExtra("hours", edtHours.text.toString().toInt())
                    putExtra("cicloId", edtCicloId.text.toString().toInt())
                    putExtra("careerCod", edtCareerCod.text.toString().toInt())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            edtCod.text.isNullOrEmpty() -> {
                edtCod.error = "Ingrese el código"
                false
            }
            edtName.text.isNullOrEmpty() -> {
                edtName.error = "Ingrese el nombre"
                false
            }
            edtCredits.text.isNullOrEmpty() -> {
                edtCredits.error = "Ingrese los créditos"
                false
            }
            edtHours.text.isNullOrEmpty() -> {
                edtHours.error = "Ingrese las horas"
                false
            }
            edtCicloId.text.isNullOrEmpty() -> {
                edtCicloId.error = "Ingrese el ID del ciclo"
                false
            }
            edtCareerCod.text.isNullOrEmpty() -> {
                edtCareerCod.error = "Ingrese el código de carrera"
                false
            }
            else -> true
        }
    }
}