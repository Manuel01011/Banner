package com.example.banner.frontend.views.student

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R

class AddStudent : AppCompatActivity() {

    private lateinit var studentId: EditText
    private lateinit var studentName: EditText
    private lateinit var studentTelNumber: EditText
    private lateinit var studentEmail: EditText
    private lateinit var studentBornDate: EditText
    private lateinit var studentCareerCode: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_student)

        // Asociamos las vistas con las variables
        studentId = findViewById(R.id.agregar_estudiante_id)
        studentName = findViewById(R.id.agregar_estudiante_name)
        studentTelNumber = findViewById(R.id.agregar_estudiante_telNumber)
        studentEmail = findViewById(R.id.agregar_estudiante_email)
        studentBornDate = findViewById(R.id.agregar_estudiante_bornDate)
        studentCareerCode = findViewById(R.id.agregar_estudiante_careerCod)
        saveBtn = findViewById(R.id.btn_guardar_estudiante)

        // Configuramos el botón para guardar el estudiante
        saveBtn.setOnClickListener {
            val studentId = studentId.text.toString().toIntOrNull()
            val studentName = studentName.text.toString()
            val studentTelNumber = studentTelNumber.text.toString().toIntOrNull()
            val studentEmail = studentEmail.text.toString()
            val studentBornDate = studentBornDate.text.toString()
            val studentCareerCod = studentCareerCode.text.toString().toIntOrNull()

            if (studentId != null && studentName.isNotBlank() &&
                studentTelNumber != null && studentEmail.isNotBlank() &&
                studentBornDate.isNotBlank() && studentCareerCod != null) {

                val newStudent = Student_(
                    id = studentId,
                    name = studentName,
                    telNumber = studentTelNumber,
                    email = studentEmail,
                    bornDate = studentBornDate,
                    careerCod = studentCareerCod
                )

                InsertStudentTask().execute(newStudent)
            } else {
                Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class InsertStudentTask : AsyncTask<Student_, Void, Boolean>() {
        private val progressDialog = ProgressDialog(this@AddStudent).apply {
            setMessage("Guardando estudiante...")
            setCancelable(false)
        }

        override fun onPreExecute() {
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Student_): Boolean {
            return try {
                val response = HttpHelper.sendRequest(
                    "students",
                    "POST",
                    params[0],
                    String::class.java
                )
                response?.contains("\"success\":true") ?: false
            } catch (e: Exception) {
                false
            }
        }

        override fun onPostExecute(success: Boolean) {
            progressDialog.dismiss()

            if (success) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(
                    this@AddStudent,
                    "Error al guardar el estudiante",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}