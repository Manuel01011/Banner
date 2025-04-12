package com.example.banner.frontend.views.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.banner.R
import com.example.banner.backend.Controllers.UserController
import com.example.banner.frontend.views.admin.Admin
import com.example.banner.frontend.views.register.Register

class login : ComponentActivity(){
    lateinit var usernameInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity)
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
                println("El id de usuario debe ser un número entero")
                // Mostrar un mensaje de error o alerta aquí si lo deseas
                return@setOnClickListener
            }

            try {
                // Llamar al método de login
                //val isLoggedIn = userController.loginUser(username, password)
                if (true) {
                    Log.d("MainActivity","Login exitoso")

                    // Iniciar la nueva actividad con el Intent
                    val intent = Intent(this, Admin::class.java)
                    intent.putExtra("USERNAME", username.toString()) // Aquí pasas el nombre de usuario
                    intent.putExtra("PASSWORD", password) // Opcional, si necesitas la contraseña
                    startActivity(intent)
                    //finish() // Cierra la actividad de login para que el usuario no regrese con el botón "Atrás"
                } else {
                    Log.d("MainActivity","Credenciales incorrectas")
                    // Mostrar un mensaje de error de credenciales incorrectas
                }
            } catch (e: Exception) {
                Log.d("MainActivity", "Error en el proceso de login: ${e.message}")
                e.printStackTrace()
            }

            Log.d("MainActivity", "Username: $username and Password: $password")
        }

        registerText.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}