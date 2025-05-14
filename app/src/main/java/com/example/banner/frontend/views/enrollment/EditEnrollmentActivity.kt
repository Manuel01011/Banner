package com.example.banner.frontend.views.enrollment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R
import android.os.AsyncTask


class EditEnrollmentActivity : AppCompatActivity() {
    private lateinit var etStudentId: EditText
    private lateinit var etGrupoId: EditText
    private lateinit var etGrade: EditText
    private lateinit var btnGuardar: Button


    private var originalStudentId: Int = -1
    private var originalGrupoId: Int = -1
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_enrollment)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        etStudentId = findViewById(R.id.etStudentId)
        etGrupoId = findViewById(R.id.etGrupoId)
        etGrade = findViewById(R.id.etGrade)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        originalStudentId = intent.getIntExtra("studentId", -1)
        originalGrupoId = intent.getIntExtra("grupoId", -1)

        etStudentId.setText(originalStudentId.toString())
        etGrupoId.setText(originalGrupoId.toString())
        etGrade.setText(intent.getDoubleExtra("grade", 0.0).toString())

        // Deshabilitar edición de studentId y grupoId ya que son parte de la clave primaria
        etStudentId.isEnabled = false
        etGrupoId.isEnabled = false
    }

    private fun setupSaveButton() {
        btnGuardar.setOnClickListener {
            if (validateFields()) {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating enrollment...")
                    setCancelable(false)
                    show()
                }

                val updatedEnrollment = Enrollment_(
                    originalStudentId,
                    originalGrupoId,
                    etGrade.text.toString().toDouble()
                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "enrollments",
                                "PUT",
                                updatedEnrollment,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error updating enrollment", e)
                            false
                        }
                    }

                    override fun onPostExecute(success: Boolean) {
                        progressDialog.dismiss()
                        if (success) {
                            val resultIntent = Intent().apply {
                                putExtra("position", position)
                                putExtra("studentId", updatedEnrollment.studentId)
                                putExtra("grupoId", updatedEnrollment.grupoId)
                                putExtra("grade", updatedEnrollment.grade)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@EditEnrollmentActivity,
                                "Error updating enrollment",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.execute()
            }
        }
    }

    private fun validateFields(): Boolean {
        val grade = etGrade.text.toString().toDoubleOrNull()

        return when {
            grade == null || grade < 0 || grade > 100 -> {
                etGrade.error = "The grade must be between 0 and 100"
                false
            }
            else -> true
        }
    }
}