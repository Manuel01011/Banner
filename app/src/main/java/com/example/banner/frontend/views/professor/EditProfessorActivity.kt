package com.example.banner.frontend.views.professor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditProfessorActivity : AppCompatActivity(){
    private lateinit var editName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnSave: Button

    private var teacherId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editteacher)

        editName = findViewById(R.id.edit_name)
        editPhone = findViewById(R.id.edit_tel)
        editEmail = findViewById(R.id.edit_email)
        btnSave = findViewById(R.id.btn_save)

        // Obtener datos del intent
        teacherId = intent.getIntExtra("id", -1)
        editName.setText(intent.getStringExtra("name"))
        editPhone.setText(intent.getIntExtra("tel", 0).toString())
        editEmail.setText(intent.getStringExtra("email"))

        btnSave.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("id", teacherId)
                putExtra("name", editName.text.toString())
                putExtra("tel", editPhone.text.toString().toIntOrNull() ?: 0)
                putExtra("email", editEmail.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}