package com.example.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditStudentActivity: AppCompatActivity() {
    private lateinit var nameEdit: EditText
    private lateinit var telEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var bornDateEdit: EditText
    private lateinit var careerCodEdit: EditText
    private lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editstudent)

        nameEdit = findViewById(R.id.editName)
        telEdit = findViewById(R.id.editTel)
        emailEdit = findViewById(R.id.editEmail)
        bornDateEdit = findViewById(R.id.editBornDate)
        careerCodEdit = findViewById(R.id.editCareerCod)
        saveButton = findViewById(R.id.saveButton)

        // Recuperar datos del intent
        nameEdit.setText(intent.getStringExtra("name") ?: "")
        telEdit.setText(intent.getIntExtra("telNumber", 0).toString())
        emailEdit.setText(intent.getStringExtra("email") ?: "")
        bornDateEdit.setText(intent.getStringExtra("bornDate") ?: "")
        careerCodEdit.setText(intent.getIntExtra("careerCod", 0).toString())

        saveButton.setOnClickListener {
            val name = nameEdit.text.toString()
            val tel = telEdit.text.toString().toIntOrNull()
            val email = emailEdit.text.toString()
            val bornDate = bornDateEdit.text.toString()
            val careerCod = careerCodEdit.text.toString().toIntOrNull()

            if (name.isBlank() || tel == null || email.isBlank() || bornDate.isBlank() || careerCod == null) {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("id", intent.getIntExtra("id", -1))
                putExtra("name", name)
                putExtra("telNumber", tel)
                putExtra("email", email)
                putExtra("bornDate", bornDate)
                putExtra("careerCod", careerCod)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}