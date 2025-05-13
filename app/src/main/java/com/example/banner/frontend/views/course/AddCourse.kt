package com.example.banner.frontend.views.course

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
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R

class AddCourse : AppCompatActivity() {
    private lateinit var courseCode: EditText
    private lateinit var courseName: EditText
    private lateinit var credits: EditText
    private lateinit var hours: EditText
    private lateinit var cycleId: EditText
    private lateinit var careerCode: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_course)

        // Asocia las vistas de la interfaz con las variables de Kotlin
        courseCode = findViewById(R.id.agregar_cod_curso)
        courseName = findViewById(R.id.agregar_nombre)
        credits = findViewById(R.id.agregar_creditos)
        hours = findViewById(R.id.agregar_horas)
        cycleId = findViewById(R.id.agregar_id_ciclo)
        careerCode = findViewById(R.id.agregar_cod_carrera)
        saveBtn = findViewById(R.id.btn_guardar_curso)

        saveBtn.setOnClickListener {
            try {
                val courseCodeValue = courseCode.text.toString().toInt()
                val courseNameValue = courseName.text.toString().trim()
                val creditsValue = credits.text.toString().toInt()
                val hoursValue = hours.text.toString().toInt()
                val cycleIdValue = cycleId.text.toString().toInt()
                val careerCodeValue = careerCode.text.toString().toInt()

                if (courseNameValue.isEmpty()) {
                    throw IllegalArgumentException("Course name cannot be empty")
                }

                if (creditsValue <= 0 || hoursValue <= 0) {
                    throw IllegalArgumentException("Credits and hours must be positive")
                }

                // Mostrar progreso
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Adding course...")
                    setCancelable(false)
                    show()
                }

                // Crear objeto Course
                val newCourse = Course_(
                    courseCodeValue,
                    courseNameValue,
                    creditsValue,
                    hoursValue,
                    cycleIdValue,
                    careerCodeValue
                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "courses",
                                "POST",
                                newCourse,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error adding course", e)
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
                                this@AddCourse,
                                "Error adding course",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.execute()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
