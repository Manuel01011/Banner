package com.example.banner.frontend.views.career
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Career_
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class EditCareerActivity : AppCompatActivity() {
    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var saveBtn: Button
    private lateinit var coursesListView: ListView
    private var position: Int = -1
    private var originalCod: Int = -1
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_career)

        cod = findViewById(R.id.edit_cod)
        name = findViewById(R.id.edit_name)
        title = findViewById(R.id.edit_title)
        saveBtn = findViewById(R.id.btn_save)
        coursesListView = findViewById(R.id.coursesListView)

        // Configurar adapter para la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        coursesListView.adapter = adapter

        // Obtener datos
        position = intent.getIntExtra("position", -1)
        originalCod = intent.getIntExtra("cod", 0)
        cod.setText(originalCod.toString())
        name.setText(intent.getStringExtra("name"))
        title.setText(intent.getStringExtra("title"))

        // Cargar cursos de la carrera
        LoadCareerCoursesTask().execute(originalCod)

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

    private inner class LoadCareerCoursesTask : AsyncTask<Int, Void, List<Course_>?>() {
        override fun doInBackground(vararg params: Int?): List<Course_>? {
            val careerId = params[0] ?: return null
            Log.d("LoadCourses", "Cargando cursos para carrera ID: $careerId")

            try {
                // Usamos getRawResponse para obtener el JSON como String
                val response = HttpHelper.getRawResponse("careers/$careerId/courses")

                if (response.isNullOrEmpty()) {
                    Log.e("LoadCourses", "Respuesta vacía o nula")
                    return null
                }

                Log.d("LoadCourses", "Respuesta recibida: $response")

                // Parseamos la respuesta manualmente para mejor control
                val jsonObject = JSONObject(response)

                if (!jsonObject.getBoolean("success")) {
                    Log.e("LoadCourses", "API respondió con success=false")
                    return null
                }

                val coursesArray = jsonObject.getJSONArray("data")
                val courses = mutableListOf<Course_>()

                for (i in 0 until coursesArray.length()) {
                    val courseObj = coursesArray.getJSONObject(i)
                    courses.add(
                        Course_(
                            courseObj.getInt("cod"),
                            courseObj.getString("name"),
                            courseObj.getInt("credits"),
                            courseObj.getInt("hours"),
                            courseObj.getInt("cicloId"),
                            courseObj.getInt("careerCod")
                        )
                    )
                }

                Log.d("LoadCourses", "Cursos parseados: ${courses.size}")
                return courses

            } catch (e: Exception) {
                Log.e("LoadCourses", "Error al cargar cursos: ${e.message}", e)
                return null
            }
        }

        override fun onPostExecute(result: List<Course_>?) {
            if (result == null) {
                Toast.makeText(
                    this@EditCareerActivity,
                    "Error al cargar cursos. Verifica la conexión.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (result.isEmpty()) {
                Toast.makeText(
                    this@EditCareerActivity,
                    "Esta carrera no tiene cursos asignados",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val courseNames = result.map { it.name }
            adapter.clear()
            adapter.addAll(courseNames)
            adapter.notifyDataSetChanged()
        }
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