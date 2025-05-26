package com.example.banner.frontend.views.group
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Course_
import com.example.backend_banner.backend.Models.Grupo_
import com.example.backend_banner.backend.Models.Teacher_
import com.example.banner.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddGroup : AppCompatActivity() {
    private lateinit var grupoId: EditText
    private lateinit var grupoNumber: EditText
    private lateinit var grupoYear: EditText
    private lateinit var grupoHorario: EditText
    private lateinit var saveGrupoBtn: Button
    private lateinit var courseSpinner: Spinner
    private lateinit var teacherSpinner: Spinner

    private var coursesList: List<Course_> = emptyList()
    private var teachersList: List<Teacher_> = emptyList()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_group)

        // Initialize views
        grupoId = findViewById(R.id.agregar_grupo_id)
        grupoNumber = findViewById(R.id.agregar_grupo_number)
        grupoYear = findViewById(R.id.agregar_grupo_year)
        grupoHorario = findViewById(R.id.agregar_grupo_horario)
        saveGrupoBtn = findViewById(R.id.btn_guardar_grupo)
        courseSpinner = findViewById(R.id.course_spinner)
        teacherSpinner = findViewById(R.id.teacher_spinner)

        // Load initial data
        loadInitialData()

        saveGrupoBtn.setOnClickListener {
            try {
                val id = grupoId.text.toString().toInt()
                val number = grupoNumber.text.toString().toInt()
                val year = grupoYear.text.toString().toInt()
                val horario = grupoHorario.text.toString()
                val selectedCourse = coursesList[courseSpinner.selectedItemPosition]
                val selectedTeacher = teachersList[teacherSpinner.selectedItemPosition]

                if (horario.isEmpty()) {
                    grupoHorario.error = "Please enter a valid schedule"
                    return@setOnClickListener
                }

                showProgressDialog("Creating group...")

                val newGroup = Grupo_(
                    id = id,
                    numberGroup = number,
                    year = year,
                    horario = horario,
                    courseCod = selectedCourse.cod,
                    teacherId = selectedTeacher.id
                )

                lifecycleScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            HttpHelper.sendRequest(
                                "groups",
                                "POST",
                                newGroup,
                                String::class.java
                            )
                        }

                        Log.d("API_DEBUG", "Create group response: $response")

                        if (response != null) {
                            try {
                                val jsonResponse = JSONObject(response)
                                if (jsonResponse.optString("status") != "success") {
                                    setResult(Activity.RESULT_OK)
                                    finish()

                                    returnResult(newGroup)
                                } else {
                                    showError(jsonResponse.optString("message", "Error creating group"))
                                }
                            } catch (e: Exception) {
                                Log.e("API_ERROR", "Response parsing error", e)
                                showError("Invalid server response")
                            }
                        } else {
                            showError("No response from server")
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Network error", e)
                        showError("Connection error: ${e.localizedMessage}")
                    } finally {
                        dismissProgressDialog()
                    }
                }
            } catch (e: NumberFormatException) {
                showError("Please enter valid numbers")
            } catch (e: Exception) {
                showError(e.message ?: "Invalid input")
            }
        }
    }

    private fun loadInitialData() {
        showProgressDialog("Loading data...")
        Log.d("API_DEBUG", "Starting data loading process")

        lifecycleScope.launch {
            try {
                Log.d("API_DEBUG", "Launching parallel requests")

                val coursesDeferred = async(Dispatchers.IO) {
                    Log.d("API_DEBUG", "Loading courses...")
                    loadCourses().also {
                        Log.d("API_DEBUG", "Courses loaded: ${it?.size ?: 0} items")
                    }
                }

                val teachersDeferred = async(Dispatchers.IO) {
                    Log.d("API_DEBUG", "Loading teachers...")
                    loadTeachers().also {
                        Log.d("API_DEBUG", "Teachers loaded: ${it?.size ?: 0} items")
                    }
                }

                coursesList = coursesDeferred.await() ?: emptyList()
                teachersList = teachersDeferred.await() ?: emptyList()

                withContext(Dispatchers.Main) {
                    if (coursesList.isNotEmpty()) {
                        setupCourseSpinner()
                    } else {
                        showError("No courses available")
                    }

                    if (teachersList.isNotEmpty()) {
                        setupTeacherSpinner()
                    } else {
                        showError("No teachers available")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error in loadInitialData", e)
                showError("Error loading data: ${e.message}")
            } finally {
                dismissProgressDialog()
            }
        }
    }

    private suspend fun loadCourses(): List<Course_>? {
        return try {
            val response = HttpHelper.getRawResponse("courses")
            Log.d("API_DEBUG", "Raw courses response: $response")
            parseResponse<List<Course_>>(response)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading courses", e)
            null
        }
    }

    private suspend fun loadTeachers(): List<Teacher_>? {
        return try {
            val response = HttpHelper.getRawResponse("teachers")
            Log.d("API_DEBUG", "Raw teachers response: $response")
            parseResponse<List<Teacher_>>(response)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading teachers", e)
            null
        }
    }

    private inline fun <reified T> parseResponse(response: String?): T? {
        if (response == null) {
            Log.e("API_ERROR", "Null response received")
            return null
        }

        return try {
            Log.d("API_DEBUG", "Raw JSON: $response")
            val json = JSONObject(response)

            if (json.getString("status") != "success") {
                Log.e("API_ERROR", "API error: ${json.optString("message")}")
                return null
            }

            val dataArray = json.getJSONArray("data")
            Log.d("API_DEBUG", "Data array: ${dataArray.toString()}")

            val type = object : TypeToken<T>() {}.type
            Gson().fromJson(dataArray.toString(), type)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Parsing failed: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }

    private fun setupCourseSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            coursesList.map { "${it.cod} - ${it.name}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter
    }

    private fun setupTeacherSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            teachersList.map { "${it.id} - ${it.name}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        teacherSpinner.adapter = adapter
    }

    private fun showProgressDialog(message: String) {
        progressDialog?.dismiss()
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun returnResult(group: Grupo_) {
        val resultIntent = Intent().apply {
            putExtra("groupId", group.id)
            putExtra("groupNumber", group.numberGroup)
            putExtra("groupYear", group.year)
            putExtra("groupHorario", group.horario)
            putExtra("groupCourseCode", group.courseCod)
            putExtra("groupTeacherId", group.teacherId)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}