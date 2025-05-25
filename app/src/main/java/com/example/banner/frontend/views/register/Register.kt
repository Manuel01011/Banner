package com.example.banner.frontend.views.register
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Usuario_
import com.example.banner.MainActivity
import com.example.banner.R
import com.example.banner.backend.Models.RegistrationRequest_
import com.google.gson.Gson
import org.json.JSONObject

class Register : AppCompatActivity() {
    private lateinit var userIdInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var nameInput: EditText
    private lateinit var telInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var bornDateInput: EditText
    private lateinit var careerSpinner: Spinner
    private lateinit var careerLabel: TextView
    private lateinit var nameLabel: TextView
    private lateinit var telLabel: TextView
    private lateinit var emailLabel: TextView
    private lateinit var bornDateLabel: TextView

    private var selectedRole = "student"
    private var selectedCareerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        initViews()
        setupRoleSpinner()
        setupRegisterButton()
    }

    private fun initViews() {
        userIdInput = findViewById(R.id.user_id_input)
        passwordInput = findViewById(R.id.password_input)
        roleSpinner = findViewById(R.id.role_spinner)
        registerButton = findViewById(R.id.register_btn)
        nameInput = findViewById(R.id.name_input)
        telInput = findViewById(R.id.tel_input)
        emailInput = findViewById(R.id.email_input)
        bornDateInput = findViewById(R.id.born_date_input)
        careerSpinner = findViewById(R.id.career_spinner)

        careerLabel = findViewById(R.id.career_label)
        nameLabel = findViewById(R.id.name_label)
        telLabel = findViewById(R.id.tel_label)
        emailLabel = findViewById(R.id.email_label)
        bornDateLabel = findViewById(R.id.born_date_label)
    }

    private fun setupRoleSpinner() {
        val roles = listOf("student", "teacher", "admin", "matriculador")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRole = roles[position]
                updateFieldsVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateFieldsVisibility() {
        when (selectedRole) {
            "student" -> {
                showStudentFields()
                loadCareers()
            }
            "teacher" -> {
                showTeacherFields()
                hideCareerField()
            }
            else -> {
                hideAdditionalFields()
            }
        }
    }

    private fun showStudentFields() {
        nameLabel.visibility = View.VISIBLE
        nameInput.visibility = View.VISIBLE
        telLabel.visibility = View.VISIBLE
        telInput.visibility = View.VISIBLE
        emailLabel.visibility = View.VISIBLE
        emailInput.visibility = View.VISIBLE
        bornDateLabel.visibility = View.VISIBLE
        bornDateInput.visibility = View.VISIBLE
        careerLabel.visibility = View.VISIBLE
        careerSpinner.visibility = View.VISIBLE
    }

    private fun showTeacherFields() {
        nameLabel.visibility = View.VISIBLE
        nameInput.visibility = View.VISIBLE
        telLabel.visibility = View.VISIBLE
        telInput.visibility = View.VISIBLE
        emailLabel.visibility = View.VISIBLE
        emailInput.visibility = View.VISIBLE
        hideCareerField()
        bornDateLabel.visibility = View.GONE
        bornDateInput.visibility = View.GONE
    }

    private fun hideAdditionalFields() {
        nameLabel.visibility = View.GONE
        nameInput.visibility = View.GONE
        telLabel.visibility = View.GONE
        telInput.visibility = View.GONE
        emailLabel.visibility = View.GONE
        emailInput.visibility = View.GONE
        bornDateLabel.visibility = View.GONE
        bornDateInput.visibility = View.GONE
        hideCareerField()
    }

    private fun hideCareerField() {
        careerLabel.visibility = View.GONE
        careerSpinner.visibility = View.GONE
    }

    private fun loadCareers() {
        val careers = listOf("Ingeniería de Software", "Medicina", "Derecho")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, careers)
        careerSpinner.adapter = adapter

        careerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCareerId = position + 1 // Simulando IDs de carreras
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRegisterButton() {
        registerButton.setOnClickListener {
            val userId = userIdInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (userId.isEmpty() || password.isEmpty()) {
                showToast("Por favor, completa todos los campos")
                return@setOnClickListener
            }

            when (selectedRole) {
                "student" -> registerStudent(userId, password)
                "teacher" -> registerTeacher(userId, password)
                else -> registerAdminOrMatriculador(userId, password)
            }
        }
    }

    private fun registerStudent(userId: String, password: String) {
        val name = nameInput.text.toString()
        val telNumber = telInput.text.toString()
        val email = emailInput.text.toString()
        val bornDate = bornDateInput.text.toString()
        val careerCod = selectedCareerId

        if (name.isEmpty() || telNumber.isEmpty() || email.isEmpty() || bornDate.isEmpty() || careerCod == null) {
            showToast("Por favor, completa todos los campos para estudiante")
            return
        }

        val request = RegistrationRequest_(
            id = 0, // El ID se generará en el servidor
            user_id = userId.toInt(),
            password = password,
            role = selectedRole,
            name = name,
            tel_number = telNumber.toInt(),
            email = email,
            born_date = bornDate,
            career_cod = careerCod,
            status = "pending",
            request_date = ""
        )

        sendRegistrationRequest(request)
    }

    private fun registerTeacher(userId: String, password: String) {
        val name = nameInput.text.toString()
        val telNumber = telInput.text.toString()
        val email = emailInput.text.toString()

        if (name.isEmpty() || telNumber.isEmpty() || email.isEmpty()) {
            showToast("Por favor, completa todos los campos para profesor")
            return
        }

        val request = RegistrationRequest_(
            id = 0,
            user_id = userId.toInt(),
            password = password,
            role = selectedRole,
            name = name,
            tel_number = telNumber.toInt(),
            email = email,
            born_date = null,
            career_cod = null,
            status = "pending",
            request_date = ""
        )

        sendRegistrationRequest(request)
    }

    private fun registerAdminOrMatriculador(userId: String, password: String) {
        val request = RegistrationRequest_(
            id = 0,
            user_id = userId.toInt(),
            password = password,
            role = selectedRole,
            name = null,
            tel_number = null,
            email = null,
            born_date = null,
            career_cod = null,
            status = "pending",
            request_date = ""
        )

        sendRegistrationRequest(request)
    }

    private fun sendRegistrationRequest(request: RegistrationRequest_) {
        val jsonRequest = Gson().toJson(request)

        Thread {
            try {
                val response = HttpHelper.postRequest("registration-requests", jsonRequest)

                runOnUiThread {
                    if (response != null) {
                        val jsonResponse = JSONObject(response)
                        if (jsonResponse.getBoolean("success")) {
                            showToast("Solicitud de registro enviada para aprobación")
                            redirectToMainScreen()
                        } else {
                            showToast("Error en el registro: ${jsonResponse.optString("message")}")
                        }
                    } else {
                        showToast("Error de conexión con el servidor")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error al enviar la solicitud: ${e.message}")
                }
            }
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun redirectToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}