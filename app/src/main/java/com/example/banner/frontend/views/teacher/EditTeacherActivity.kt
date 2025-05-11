package com.example.banner.frontend.views.teacher
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.banner.R

class EditTeacherActivity : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnSave: Button

    private var teacherId: Int = -1
    private var position: Int = -1  // Añadido para manejar la posición

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_teacher)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        editName = findViewById(R.id.edit_name)
        editPhone = findViewById(R.id.edit_tel)
        editEmail = findViewById(R.id.edit_email)
        btnSave = findViewById(R.id.btn_save)
    }

    private fun loadIntentData() {
        teacherId = intent.getIntExtra("id", -1)
        position = intent.getIntExtra("position", -1)  // Obtener la posición
        editName.setText(intent.getStringExtra("name"))
        editPhone.setText(intent.getIntExtra("tel", 0).toString())
        editEmail.setText(intent.getStringExtra("email"))
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            if (validateFields()) {
                val resultIntent = Intent().apply {
                    putExtra("position", position)  // Incluir posición en el resultado
                    putExtra("id", teacherId)
                    putExtra("name", editName.text.toString())
                    putExtra("tel", editPhone.text.toString().toInt())
                    putExtra("email", editEmail.text.toString())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            editName.text.isNullOrEmpty() -> {
                editName.error = "Ingrese el nombre"
                false
            }
            editPhone.text.isNullOrEmpty() -> {
                editPhone.error = "Ingrese el teléfono"
                false
            }
            editEmail.text.isNullOrEmpty() -> {
                editEmail.error = "Ingrese el email"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.text.toString()).matches() -> {
                editEmail.error = "Email inválido"
                false
            }
            else -> true
        }
    }
}