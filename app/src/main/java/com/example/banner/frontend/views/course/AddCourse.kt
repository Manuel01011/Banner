package com.example.banner.frontend.views.course
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Career_
import com.example.backend_banner.backend.Models.Ciclo_
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddCourse : AppCompatActivity() {
    private lateinit var courseName: EditText
    private lateinit var credits: EditText
    private lateinit var hours: EditText
    private lateinit var courseCode:EditText
    private lateinit var saveBtn: Button


    private lateinit var cycleSpinner: Spinner
    private lateinit var careerSpinner: Spinner

    private var cyclesList: List<Ciclo_> = emptyList()
    private var careersList: List<Career_> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_course)

        // Initialize views
        courseCode = findViewById(R.id.agregar_code)
        courseName = findViewById(R.id.agregar_nombre)
        credits = findViewById(R.id.agregar_creditos)
        hours = findViewById(R.id.agregar_horas)
        cycleSpinner = findViewById(R.id.cycle_spinner)
        careerSpinner = findViewById(R.id.career_spinner)
        saveBtn = findViewById(R.id.btn_guardar_curso)

        // Load initial data
        loadInitialData()

        saveBtn.setOnClickListener {
            try {
                val CourseCourse = courseCode.text.toString().toInt()
                val courseNameValue = courseName.text.toString().trim()
                val creditsValue = credits.text.toString().toInt()
                val hoursValue = hours.text.toString().toInt()
                val selectedCycle = cyclesList[cycleSpinner.selectedItemPosition]
                val selectedCareer = careersList[careerSpinner.selectedItemPosition]

                if (courseNameValue.isEmpty()) {
                    throw IllegalArgumentException("Course name cannot be empty")
                }

                if (creditsValue <= 0 || hoursValue <= 0) {
                    throw IllegalArgumentException("Credits and hours must be positive")
                }

                showProgressDialog("Adding course...")

                val newCourse = Course_(
                    CourseCourse,
                    courseNameValue,
                    creditsValue,
                    hoursValue,
                    selectedCycle.id,
                    selectedCareer.cod
                )

                lifecycleScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            HttpHelper.sendRequest(
                                "courses",
                                "POST",
                                newCourse,
                                String::class.java
                            )
                        }

                        Log.d("API_DEBUG", "Create course response: $response")

                        if (response != null) {
                            try {
                                val jsonResponse = JSONObject(response)
                                if (jsonResponse.optString("status") != "success") {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                } else {
                                    showError(jsonResponse.optString("message", "Error adding course"))
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
            } catch (e: IllegalArgumentException) {
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


                val cyclesDeferred = async(Dispatchers.IO) {
                    Log.d("API_DEBUG", "Loading cycles...")
                    loadCycles().also {
                        Log.d("API_DEBUG", "Cycles loaded: ${it?.size ?: 0} items")
                    }
                }

                val careersDeferred = async(Dispatchers.IO) {
                    Log.d("API_DEBUG", "Loading careers...")
                    loadCareers().also {
                        Log.d("API_DEBUG", "Careers loaded: ${it?.size ?: 0} items")
                    }
                }

                cyclesList = cyclesDeferred.await() ?: emptyList()
                careersList = careersDeferred.await() ?: emptyList()


                withContext(Dispatchers.Main) {

                    if (cyclesList.isNotEmpty()) {
                        setupCycleSpinner()
                    } else {
                        showError("No cycles available")
                    }

                    if (careersList.isNotEmpty()) {
                        setupCareerSpinner()
                    } else {
                        showError("No careers available")
                    }
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error in loadInitialData", e)
                withContext(Dispatchers.Main) {
                    showError("Error loading data: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    dismissProgressDialog()
                }
            }
        }
    }

    private suspend fun loadCycles(): List<Ciclo_>? {
        return try {
            val response = HttpHelper.getRawResponse("ciclos")
            Log.d("API_DEBUG", "Raw cycles response: $response")
            parseResponse<List<Ciclo_>>(response)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading cycles", e)
            null
        }
    }

    private suspend fun loadCareers(): List<Career_>? {
        return try {
            val response = HttpHelper.getRawResponse("careers")
            Log.d("API_DEBUG", "Raw careers response: $response")
            parseResponse<List<Career_>>(response)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading careers", e)
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

            // Verifica el status primero
            if (json.getString("status") != "success") {
                Log.e("API_ERROR", "API error: ${json.optString("message")}")
                return null
            }

            // Obtiene el array "data" correctamente
            val dataArray = json.getJSONArray("data")
            Log.d("API_DEBUG", "Data array: ${dataArray.toString()}")

            // Usa TypeToken para listas
            val type = object : TypeToken<T>() {}.type
            Gson().fromJson(dataArray.toString(), type)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Parsing failed: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }

    private fun setupCycleSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cyclesList.map { "${it.id} - ${it.year}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cycleSpinner.adapter = adapter
    }

    private fun setupCareerSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            careersList.map { "${it.cod} - ${it.name}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        careerSpinner.adapter = adapter
    }

    private var progressDialog: ProgressDialog? = null

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
}