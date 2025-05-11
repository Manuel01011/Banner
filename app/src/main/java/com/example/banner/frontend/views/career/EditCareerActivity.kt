package com.example.banner.frontend.views.career
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Career_
import com.example.banner.R

class EditCareerActivity : AppCompatActivity() {
    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var saveBtn: Button
    private var position: Int = -1
    private var originalCod: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_career)

        cod = findViewById(R.id.edit_cod)
        name = findViewById(R.id.edit_name)
        title = findViewById(R.id.edit_title)
        saveBtn = findViewById(R.id.btn_save)

        // Obtener datos
        position = intent.getIntExtra("position", -1)
        originalCod = intent.getIntExtra("cod", 0)
        cod.setText(originalCod.toString())
        name.setText(intent.getStringExtra("name"))
        title.setText(intent.getStringExtra("title"))

        saveBtn.setOnClickListener {
            if (validateFields()) {
                val updatedCareer = Career_(
                    cod.text.toString().toInt(),
                    name.text.toString(),
                    title.text.toString()
                )
                UpdateCareerTask().execute(updatedCareer)
            }
        }
    }

    private fun validateFields(): Boolean {
        if (name.text.isBlank() || title.text.isBlank()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private inner class UpdateCareerTask : AsyncTask<Career_, Void, Boolean>() {
        override fun doInBackground(vararg params: Career_): Boolean {
            return try {
                val response = HttpHelper.sendRequest(
                    "careers",
                    "PUT",
                    params[0],
                    String::class.java
                )
                response?.contains("\"success\":true") ?: false
            } catch (e: Exception) {
                false
            }
        }

        override fun onPostExecute(success: Boolean) {
            if (success) {
                setResultAndFinish()
            } else {
                Toast.makeText(
                    this@EditCareerActivity,
                    "Error al actualizar. Verifique los datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private fun setResultAndFinish() {
            val resultIntent = Intent().apply {
                putExtra("position", position)
                putExtra("cod", cod.text.toString().toInt())
                putExtra("name", name.text.toString())
                putExtra("title", title.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}