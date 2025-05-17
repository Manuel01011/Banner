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
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating teacher...")
                    setCancelable(false)
                    show()
                }

                val updatedTeacher = Teacher_(
                    id = intent.getIntExtra("id", -1),
                    name = editName.text.toString(),
                    telNumber = editPhone.text.toString().toInt(),
                    email = editEmail.text.toString()
                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "teachers",
                                "PUT",
                                updatedTeacher,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error updating teacher", e)
                            false
                        }
                    }

                    override fun onPostExecute(success: Boolean) {
                        progressDialog.dismiss()
                        if (success) {
                            val resultIntent = Intent().apply {
                                putExtra("position", position)
                                putExtra("id", updatedTeacher.id)
                                putExtra("name", updatedTeacher.name)
                                putExtra("tel", updatedTeacher.telNumber)
                                putExtra("email", updatedTeacher.email)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                            Toast.makeText(this@EditTeacherActivity, "Teacher updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this@EditTeacherActivity,
                                "Error updating the teacher",
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