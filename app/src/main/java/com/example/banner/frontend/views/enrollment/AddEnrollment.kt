package com.example.banner.frontend.views.enrollment
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.backend_banner.backend.Models.Grupo_
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddEnrollment : AppCompatActivity() {
    private lateinit var grade: EditText
    private lateinit var studentSpinner: Spinner
    private lateinit var groupSpinner: Spinner
    private lateinit var saveMatriculaBtn: Button
    private var studentsProgressDialog: ProgressDialog? = null
    private var groupsProgressDialog: ProgressDialog? = null
    private lateinit var progressDialog: ProgressDialog


    private var studentsList: List<Student_> = emptyList()
    private var groupsList: List<Grupo_> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_enrollment)

        // Inicializar las vistas
        studentSpinner = findViewById(R.id.student_spinner)
        groupSpinner = findViewById(R.id.group_spinner)
        grade = findViewById(R.id.agregar_calificacion)
        saveMatriculaBtn = findViewById(R.id.btn_guardar_matricula)

        loadStudents()
        loadGroups()

        // Configurar el botón de guardar matrícula
        saveMatriculaBtn.setOnClickListener {
            try {
                val selectedStudent = studentsList[studentSpinner.selectedItemPosition]
                val selectedGroup = groupsList[groupSpinner.selectedItemPosition]
                val gradeValue = grade.text.toString().toDouble()

                if (gradeValue < 0 || gradeValue > 100) {
                    grade.error = "The rating must be between 0 and 100"
                    return@setOnClickListener
                }

                showProgressDialog("Adding enrollment...")

                val newEnrollment = Enrollment_(selectedStudent.id, selectedGroup.id, gradeValue)

                lifecycleScope.launch {
                    try {
                        val success = withContext(Dispatchers.IO) {
                            val response = HttpHelper.sendRequest(
                                "enrollments",
                                "POST",
                                newEnrollment,
                                String::class.java
                            )

                            response?.let {
                                val jsonResponse = JSONObject(it)
                                jsonResponse.optBoolean("success", false) &&
                                        jsonResponse.optString("message") == "Enrollment successfully created"
                            } ?: false
                        }

                        progressDialog.dismiss()
                        if (success) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            showError("Error adding enrollment. Verify the data.")
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        Log.e("API_ERROR", "Error adding enrollment", e)
                        showError("Error: ${e.message ?: "Unknown error"}")
                    }
                }
            } catch (e: NumberFormatException) {
                showError("Please enter a valid grade")
            }
        }
    }
    private fun loadStudents() {
        studentsProgressDialog = ProgressDialog(this).apply {
            setMessage("Loading students...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("students")
                }

                if (response != null) {
                    val json = JSONObject(response)
                    val dataArray = json.getJSONArray("data")
                    studentsList = Gson().fromJson(dataArray.toString(), Array<Student_>::class.java).toList()

                    runOnUiThread {
                        val studentNames = studentsList.map { "${it.id} - ${it.name}" }
                        val adapter = ArrayAdapter(
                            this@AddEnrollment,
                            android.R.layout.simple_spinner_item,
                            studentNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        studentSpinner.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error loading students", e)
                runOnUiThread {
                    showError("Error loading students")
                }
            } finally {
                studentsProgressDialog?.dismiss()
            }
        }
    }
    private fun loadGroups() {
        groupsProgressDialog = ProgressDialog(this).apply {
            setMessage("Loading groups...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("groups")
                }

                if (response != null) {
                    val json = JSONObject(response)
                    val dataArray = json.getJSONArray("data")
                    groupsList = Gson().fromJson(dataArray.toString(), Array<Grupo_>::class.java).toList()

                    runOnUiThread {
                        val groupNames = groupsList.map { "${it.id} - ${it.horario}" }
                        val adapter = ArrayAdapter(
                            this@AddEnrollment,
                            android.R.layout.simple_spinner_item,
                            groupNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        groupSpinner.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error loading groups", e)
                runOnUiThread {
                    showError("Error loading groups")
                }
            } finally {
                groupsProgressDialog?.dismiss()
            }
        }
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
