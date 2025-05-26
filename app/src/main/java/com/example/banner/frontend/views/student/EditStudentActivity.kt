package com.example.banner.frontend.views.student

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R
import java.util.Calendar

class EditStudentActivity : AppCompatActivity() {
    private lateinit var nameEdit: EditText
    private lateinit var telEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var bornDateEdit: EditText
    private lateinit var careerCodEdit: EditText
    private lateinit var passwordEdit: EditText
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
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating student...")
                    setCancelable(false)
                    show()
                }

                val updatedStudent = Student_(
                    id = intent.getIntExtra("id", -1),
                    name = nameEdit.text.toString(),
                    telNumber = telEdit.text.toString().toInt(),
                    email = emailEdit.text.toString(),
                    bornDate = bornDateEdit.text.toString(),
                    careerCod = careerCodEdit.text.toString().toInt(),
                    password = passwordEdit.text.toString()

                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "students",
                                "PUT",
                                updatedStudent,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error updating student", e)
                            false
                        }
                    }

                    override fun onPostExecute(success: Boolean) {
                        progressDialog.dismiss()
                        if (success) {
                            val resultIntent = Intent().apply {
                                putExtra("position", position)
                                putExtra("id", updatedStudent.id)
                                putExtra("name", updatedStudent.name)
                                putExtra("telNumber", updatedStudent.telNumber)
                                putExtra("email", updatedStudent.email)
                                putExtra("bornDate", updatedStudent.bornDate)
                                putExtra("careerCod", updatedStudent.careerCod)
                                putExtra("password", updatedStudent.password)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                            Toast.makeText(this@EditStudentActivity, "Student updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this@EditStudentActivity,
                                "Error updating the student",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.execute()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            nameEdit.text.isNullOrEmpty() -> {
                nameEdit.error = "Enter the name"
                false
            }
            telEdit.text.isNullOrEmpty() -> {
                telEdit.error = "Enter the phone number"
                false
            }
            emailEdit.text.isNullOrEmpty() -> {
                emailEdit.error = "Enter the email"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches() -> {
                emailEdit.error = "Invalid email format"
                false
            }
            bornDateEdit.text.isNullOrEmpty() -> {
                bornDateEdit.error = "Enter the date of birth"
                false
            }
            careerCodEdit.text.isNullOrEmpty() -> {
                careerCodEdit.error = "Enter the career code"
                false
            }
            else -> true
        }
    }
}