package com.example.banner.frontend.views.student

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R
import java.util.Calendar

class EditStudentActivity : AppCompatActivity() {
    private lateinit var nameEdit: EditText
    private lateinit var telEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var bornDateEdit: EditText
    private lateinit var careerCodEdit: EditText
    private lateinit var saveButton: Button

    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_student)

        initViews()
        loadIntentData()
        setupDatePicker()
        setupSaveButton()
    }

    private fun initViews() {
        nameEdit = findViewById(R.id.editName)
        telEdit = findViewById(R.id.editTel)
        emailEdit = findViewById(R.id.editEmail)
        bornDateEdit = findViewById(R.id.editBornDate)
        careerCodEdit = findViewById(R.id.editCareerCod)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        nameEdit.setText(intent.getStringExtra("name"))
        telEdit.setText(intent.getIntExtra("telNumber", 0).toString())
        emailEdit.setText(intent.getStringExtra("email"))
        bornDateEdit.setText(intent.getStringExtra("bornDate"))
        careerCodEdit.setText(intent.getIntExtra("careerCod", 0).toString())
    }

    private fun setupDatePicker() {
        bornDateEdit.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "${month + 1}/$dayOfMonth/$year"
                bornDateEdit.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateFields()) {
                val resultIntent = Intent().apply {
                    putExtra("position", position)
                    putExtra("id", intent.getIntExtra("id", -1))
                    putExtra("name", nameEdit.text.toString())
                    putExtra("telNumber", telEdit.text.toString().toInt())
                    putExtra("email", emailEdit.text.toString())
                    putExtra("bornDate", bornDateEdit.text.toString())
                    putExtra("careerCod", careerCodEdit.text.toString().toInt())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            nameEdit.text.isNullOrEmpty() -> {
                nameEdit.error = "Ingrese el nombre"
                false
            }
            telEdit.text.isNullOrEmpty() -> {
                telEdit.error = "Ingrese el teléfono"
                false
            }
            emailEdit.text.isNullOrEmpty() -> {
                emailEdit.error = "Ingrese el email"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches() -> {
                emailEdit.error = "Email inválido"
                false
            }
            bornDateEdit.text.isNullOrEmpty() -> {
                bornDateEdit.error = "Seleccione fecha de nacimiento"
                false
            }
            careerCodEdit.text.isNullOrEmpty() -> {
                careerCodEdit.error = "Ingrese código de carrera"
                false
            }
            else -> true
        }
    }
}