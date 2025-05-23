package com.example.banner.frontend.views.login
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.banner.R
import com.example.banner.backend.Controllers.UserController
import com.example.banner.frontend.views.admin.Admin
import com.example.banner.frontend.views.professor.Teacher
import com.example.banner.frontend.views.register.Register
import com.example.banner.frontend.views.rol_student.StudentHistory
import com.example.banner.frontend.views.rol_teacher.TeacherGradesActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class login : ComponentActivity(){
    lateinit var usernameInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        val userController = UserController()

        // Obtener las vistas de los componentes UI del XML
        usernameInput = findViewById(R.id.user_name_input)
        passwordInput = findViewById(R.id.password_input)
        loginbtn = findViewById(R.id.login_btn)
        val registerText: TextView = findViewById(R.id.register_text)

        loginbtn.setOnClickListener {
            // Intentar convertir el username a Int
            val username = usernameInput.text.toString().toIntOrNull()
            val password = passwordInput.text.toString()

            // Verificar si el username es válido (no nulo)
            if (username == null) {
                showToast("El id de usuario debe ser un número entero")
                // Mostrar un mensaje de error o alerta aquí si lo deseas
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                showToast("Por favor ingrese su contraseña")
                return@setOnClickListener
            }
            attemptLogin(username, password)
        }
        registerText.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
    private fun attemptLogin(username: Int, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("http://10.0.2.2:8080/api/login")
                connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                // Crear el objeto JSON
                val requestBody = JSONObject().apply {
                    put("id", username)
                    put("password", password)
                }.toString()

                Log.d("LoginRequest", "Enviando: $requestBody")

                // Enviar la solicitud
                connection.outputStream.use { os ->
                    os.write(requestBody.toByteArray(Charsets.UTF_8))
                }

                // Leer la respuesta
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream.bufferedReader().use { it.readText() }
                }

                Log.d("LoginResponse", "Código: $responseCode, Respuesta: $response")

                // Manejar la respuesta en el hilo principal
                withContext(Dispatchers.Main) {
                    handleLoginResponse(response, responseCode)
                }

            } catch (e: Exception) {
                Log.e("LoginError", "Error en login", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de conexión: ${e.message ?: "Error desconocido"}")
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun handleLoginResponse(response: String, responseCode: Int) {
        try {
            val jsonResponse = JSONObject(response)

            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val success = jsonResponse.optBoolean("success", false)
                    if (success) {
                        val userObject = jsonResponse.getJSONObject("user")
                        val userId = userObject.getInt("id")
                        val role = userObject.getString("role")

                        redirectUserBasedOnRole(userId, role)
                    } else {
                        val message = jsonResponse.optString("message", "Credenciales incorrectas")
                        showToast(message)
                    }
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    val message = jsonResponse.optString("message", "Credenciales incorrectas")
                    showToast(message)
                }
                else -> {
                    val message = jsonResponse.optString("message", "Error desconocido")
                    showToast("Error $responseCode: $message")
                }
            }
        } catch (e: Exception) {
            Log.e("LoginParseError", "Error parsing response", e)
            showToast("Error procesando la respuesta del servidor")
        }
    }

    private fun redirectUserBasedOnRole(userId: Int, role: String) {
        val intent = when (role.toLowerCase()) {
            "admin" -> Intent(this, Admin::class.java)
            "teacher" -> Intent(this, TeacherGradesActivity::class.java)
            "student" -> Intent(this, StudentHistory::class.java)
            else -> {
                showToast("Rol de usuario no reconocido: $role")
                return
            }
        }

        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}