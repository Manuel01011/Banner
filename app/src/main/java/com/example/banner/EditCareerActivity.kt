package com.example.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditCareerActivity : AppCompatActivity() {
    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editcareer)

        cod = findViewById(R.id.edit_cod)
        name = findViewById(R.id.edit_name)
        title = findViewById(R.id.edit_title)
        saveBtn = findViewById(R.id.btn_save)

        // Obtener los datos actuales
        cod.setText(intent.getIntExtra("cod", 0).toString())
        name.setText(intent.getStringExtra("name"))
        title.setText(intent.getStringExtra("title"))

        saveBtn.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("cod", cod.text.toString().toInt())
                putExtra("name", name.text.toString())
                putExtra("title", title.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}