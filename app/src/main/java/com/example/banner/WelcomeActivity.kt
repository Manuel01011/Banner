package com.example.banner
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    lateinit var logoutbtn : Button
    lateinit var matriculabtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        logoutbtn = findViewById(R.id.logout_btn)
        val usuario = intent.getStringExtra("USERNAME")
        matriculabtn = findViewById(R.id.matricula_btn)


        val mensajeBienvenida = "Welcome $usuario"
        findViewById<TextView>(R.id.textViewWelcome).text = mensajeBienvenida


        logoutbtn.setOnClickListener {

            try {
                Log.d("WelcomeActivity","Logout")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                Log.d("MainActivity", "Error en el proceso de login: ${e.message}")
                e.printStackTrace()
            }

        }
        matriculabtn.setOnClickListener {

            try {
                Log.d("WelcomeActivity","Matricula")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                Log.d("MenuActivity", "Error en el proceso de menu")
                e.printStackTrace()
            }

        }


    }
}
