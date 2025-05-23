package com.example.banner.frontend.views.teacher

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Teacher_
import com.example.banner.R

class AddTeacher : AppCompatActivity() {
    private lateinit var profesorId: EditText
    private lateinit var profesorName: EditText
    private lateinit var profesorTel: EditText
    private lateinit var profesorEmail: EditText
    private lateinit var saveProfesorBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_teacher)

        // Inicializar las vistas
        profesorId = findViewById(R.id.agregar_profesor_id)
        profesorName = findViewById(R.id.agregar_profesor_name)
        profesorTel = findViewById(R.id.agregar_profesor_telNumber)
        profesorEmail = findViewById(R.id.agregar_profesor_email)
        saveProfesorBtn = findViewById(R.id.btn_guardar_profesor)

        // Configurar el botón de guardar teacher
        saveProfesorBtn.setOnClickListener {
            if (validateFields()) {
                saveTeacherToBackend()
            }
        }
    }
    private fun validateFields(): Boolean {
        return when {
            profesorName.text.isNullOrEmpty() -> {
                profesorName.error = "Enter teacher name"
                false
            }
            profesorTel.text.isNullOrEmpty() -> {
                profesorTel.error = "Enter phone number"
                false
            }
            profesorTel.text.toString().toIntOrNull() == null -> {
                profesorTel.error = "Invalid phone number"
                false
            }
            profesorEmail.text.isNullOrEmpty() -> {
                profesorEmail.error = "Enter email"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(profesorEmail.text.toString()).matches() -> {
                profesorEmail.error = "Invalid email format"
                false
            }
            else -> true
        }
    }
    private fun saveTeacherToBackend() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Saving teacher...")
            setCancelable(false)
            show()
        }

        val teacher = Teacher_(
            id = profesorId.text.toString().toInt(),
            name = profesorName.text.toString(),
            telNumber = profesorTel.text.toString().toInt(),
            email = profesorEmail.text.toString()
        )

        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return try {
                    val response = HttpHelper.sendRequest(
                        "teachers",
                        "POST",
                        teacher,
                        String::class.java
                    )
                    Log.d("API_RESPONSE", "Response: $response")
                    response != null && response.contains("\"success\":true")
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error saving teacher", e)
                    false
                }
            }

            override fun onPostExecute(success: Boolean) {
                progressDialog.dismiss()
                if (success) {
                    Toast.makeText(this@AddTeacher, "Teacher saved successfully", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@AddTeacher,
                        "Error saving teacher",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.execute()
    }
}
