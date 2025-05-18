package com.example.banner
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.banner.frontend.views.login.login


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirigir inmediatamente al login
        val intent = Intent(this, login::class.java)
        startActivity(intent)

        // Finaliza MainActivity para que el usuario no pueda volver a ella con "Atrás"
        finish()
    }
}





