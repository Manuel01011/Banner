package com.example.banner.frontend.views.enrollment
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddEnrollment : AppCompatActivity() {
    private lateinit var studentId: EditText
    private lateinit var grupoId: EditText
    private lateinit var grade: EditText
    private lateinit var saveMatriculaBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_enrollment)

        // Inicializar las vistas
        studentId = findViewById(R.id.agregar_id_estudiante)
        grupoId = findViewById(R.id.agregar_id_grupo)
        grade = findViewById(R.id.agregar_calificacion)
        saveMatriculaBtn = findViewById(R.id.btn_guardar_matricula)

        // Configurar el botón de guardar matrícula
        saveMatriculaBtn.setOnClickListener {
            try {
                val studentIdValue = studentId.text.toString().toInt()
                val grupoIdValue = grupoId.text.toString().toInt()
                val gradeValue = grade.text.toString().toDouble()

                if (gradeValue < 0 || gradeValue > 100) {
                    grade.error = "The rating must be between 0 and 100"
                    return@setOnClickListener
                }

                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Adding enrrolment...")
                    setCancelable(false)
                    show()
                }

                val newEnrollment = Enrollment_(studentIdValue, grupoIdValue, gradeValue)

                lifecycleScope.launch {
                    try {
                        val success = withContext(Dispatchers.IO) {
                            // Cambio clave: Enviamos el objeto directamente, no como JSON string
                            val response = HttpHelper.sendRequest(
                                "enrollments",
                                "POST",
                                newEnrollment,  // Envía el objeto directamente
                                String::class.java
                            )

                            // Verificación más robusta de la respuesta
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
                            Toast.makeText(
                                this@AddEnrollment,
                                "Error adding enrollment. Verify the data.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        Log.e("API_ERROR", "Error adding enrollment", e)
                        Toast.makeText(
                            this@AddEnrollment,
                            "Error: ${e.message ?: "Error desconocido"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_LONG).show()
            }
        }
    }
}
