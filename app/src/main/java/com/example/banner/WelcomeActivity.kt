package com.example.banner
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome) // Asegúrate de crear `activity_welcome.xml`
    }
}