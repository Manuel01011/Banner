package com.example.banner.frontend.views.student
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Career_
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddStudent : AppCompatActivity() {

    private lateinit var studentId: EditText
    private lateinit var studentName: EditText
    private lateinit var studentTelNumber: EditText
    private lateinit var studentEmail: EditText
    private lateinit var studentBornDate: EditText
    private lateinit var studentPassword: EditText
    private lateinit var saveBtn: Button
    private lateinit var careerSpinner: Spinner

    private var careersList: List<Career_> = emptyList()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_student)

        // Asociamos las vistas con las variables
        studentId = findViewById(R.id.agregar_estudiante_id)
        studentName = findViewById(R.id.agregar_estudiante_name)
        studentTelNumber = findViewById(R.id.agregar_estudiante_telNumber)
        studentEmail = findViewById(R.id.agregar_estudiante_email)
        studentBornDate = findViewById(R.id.agregar_estudiante_bornDate)
        studentPassword = findViewById(R.id.agregar_estudiante_pass)
        saveBtn = findViewById(R.id.btn_guardar_estudiante)
        careerSpinner = findViewById(R.id.career_spinner)

        // Cargar las carreras disponibles
        loadCareers()

        // Configuramos el botón para guardar el estudiante
        saveBtn.setOnClickListener {
            try {
                val studentId = studentId.text.toString().toIntOrNull()
                val studentName = studentName.text.toString()
                val studentTelNumber = studentTelNumber.text.toString().toIntOrNull()
                val studentEmail = studentEmail.text.toString()
                val studentBornDate = studentBornDate.text.toString()
                val studentPassword = studentPassword.text.toString()

                // Obtener la carrera seleccionada
                val selectedCareer = if (careerSpinner.selectedItemPosition != Spinner.INVALID_POSITION) {
                    careersList[careerSpinner.selectedItemPosition]
                } else {
                    null
                }

                if (studentId != null && studentName.isNotBlank() &&
                    studentTelNumber != null && studentEmail.isNotBlank() &&
                    studentBornDate.isNotBlank() && selectedCareer != null) {

                    val newStudent = Student_(
                        id = studentId,
                        name = studentName,
                        telNumber = studentTelNumber,
                        email = studentEmail,
                        bornDate = studentBornDate,
                        careerCod = selectedCareer.cod,
                        password = studentPassword
                    )

                    saveStudent(newStudent)
                } else {
                    Toast.makeText(this, "Complete all fields correctly", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCareers() {
        showProgressDialog("Loading careers...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("careers")
                }

                if (response != null) {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.optString("status") == "success") {
                        val dataArray = jsonResponse.getJSONArray("data")
                        val type = object : TypeToken<List<Career_>>() {}.type
                        careersList = Gson().fromJson(dataArray.toString(), type)

                        withContext(Dispatchers.Main) {
                            if (careersList.isNotEmpty()) {
                                setupCareerSpinner()
                            } else {
                                showError("No careers available")
                            }
                        }
                    } else {
                        showError(jsonResponse.optString("message", "Error loading careers"))
                    }
                } else {
                    showError("No response from server")
                }
            } catch (e: Exception) {
                showError("Error loading careers: ${e.message}")
            } finally {
                dismissProgressDialog()
            }
        }
    }

    private fun setupCareerSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            careersList.map { "${it.name} (${it.cod})" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        careerSpinner.adapter = adapter
    }

    private fun saveStudent(student: Student_) {
        showProgressDialog("Saving student...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.sendRequest(
                        "students",
                        "POST",
                        student,
                        String::class.java
                    )
                }

                if (response != null) {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.optString("status") != "success") {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        showError(jsonResponse.optString("message", "Error saving student"))
                    }
                } else {
                    showError("No response from server")
                }
            } catch (e: Exception) {
                showError("Error saving student: ${e.message}")
            } finally {
                dismissProgressDialog()
            }
        }
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
}