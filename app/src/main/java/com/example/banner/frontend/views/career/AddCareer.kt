package com.example.banner.frontend.views.career
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Career_
import com.example.banner.R
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class AddCareer : AppCompatActivity() {
    internal val gson = Gson()
    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var addBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_career) // Asegúrate de que este XML esté correctamente configurado

        cod = findViewById(R.id.agregar_cod)
        name = findViewById(R.id.agregar_name)
        title = findViewById(R.id.agregar_title)
        addBtn = findViewById(R.id.btn_agregar)

        // La edición del código es deshabilitada, pero si lo deseas, puedes habilitarla aquí.

        // Modificar el onClickListener del botón
        addBtn.setOnClickListener {
            val careerCod = cod.text.toString().toIntOrNull()
            val careerName = name.text.toString()
            val careerTitle = title.text.toString()

            if (careerCod != null && careerName.isNotBlank() && careerTitle.isNotBlank()) {
                val newCareer = Career_(careerCod, careerName, careerTitle)
                AddCareerTask().execute(newCareer)
            } else {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class AddCareerTask : AsyncTask<Career_, Void, Pair<Boolean, String>>() {
        override fun doInBackground(vararg params: Career_): Pair<Boolean, String> {
            return try {
                val response = HttpHelper.sendRequest(
                    "careers",
                    "POST",
                    params[0],
                    String::class.java
                ) ?: return Pair(false, "No response from server")

                try {
                    val json = JSONObject(response)
                    Pair(
                        json.optBoolean("success", false),
                        json.optString("message", if (json.optBoolean("success")) "Success" else "Unknown error")
                    )
                } catch (e: JSONException) {
                    Pair(false, "Invalid server response format")
                }
            } catch (e: Exception) {
                Pair(false, "Network error: ${e.localizedMessage}")
            }
        }

        override fun onPostExecute(result: Pair<Boolean, String>) {
            if (result.first) {
                Toast.makeText(this@AddCareer, result.second, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                AlertDialog.Builder(this@AddCareer)
                    .setTitle("Error")
                    .setMessage(result.second)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

}
