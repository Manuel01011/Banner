package com.example.banner
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val usuario = intent.getStringExtra("USERNAME")


        val mensajeBienvenida = "Welcome $usuario"
        findViewById<TextView>(R.id.textViewWelcome).text = mensajeBienvenida
    }

}
