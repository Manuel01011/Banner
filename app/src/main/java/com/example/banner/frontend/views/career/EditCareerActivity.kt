package com.example.banner.frontend.views.career
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Career_
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R
import org.json.JSONArray
import org.json.JSONObject

class EditCareerActivity : AppCompatActivity() {

    private lateinit var cod: EditText
    private lateinit var name: EditText
    private lateinit var title: EditText
    private lateinit var saveBtn: Button

    private lateinit var assignedCoursesListView: ListView
    private lateinit var unassignedCoursesListView: ListView

    private var assignedCourses = mutableListOf<Course_>()
    private var unassignedCourses = mutableListOf<Course_>()

    private lateinit var assignedAdapter: AssignedCoursesAdapter
    private lateinit var unassignedAdapter: ArrayAdapter<String>
    private var originalAssignedCourses = mutableListOf<Course_>()

    private var position: Int = -1
    private var originalCod: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_career)

        cod = findViewById(R.id.edit_cod)
        name = findViewById(R.id.edit_name)
        title = findViewById(R.id.edit_title)
        saveBtn = findViewById(R.id.btn_save)

        assignedCoursesListView = findViewById(R.id.assignedCoursesListView)
        unassignedCoursesListView = findViewById(R.id.unassignedCoursesListView)

        position = intent.getIntExtra("position", -1)
        originalCod = intent.getIntExtra("cod", 0)
        cod.setText(originalCod.toString())
        name.setText(intent.getStringExtra("name"))
        title.setText(intent.getStringExtra("title"))

        // Cargar listas de cursos asignados y no asignados
        LoadAssignedCoursesTask().execute(originalCod)
        LoadUnassignedCoursesTask().execute(originalCod)

        assignedAdapter = AssignedCoursesAdapter()
        assignedCoursesListView.adapter = assignedAdapter

        unassignedAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice)
        unassignedCoursesListView.adapter = unassignedAdapter
        unassignedCoursesListView.choiceMode = ListView.CHOICE_MODE_SINGLE

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

        // Cuando seleccionas curso no asignado para añadir
        unassignedCoursesListView.setOnItemClickListener { _, _, position, _ ->
            val courseToAdd = unassignedCourses[position]
            assignedCourses.add(courseToAdd)
            assignedAdapter.notifyDataSetChanged()

            // Lo quito de no asignados
            unassignedCourses.removeAt(position)
            unassignedAdapter.clear()
            unassignedAdapter.addAll(unassignedCourses.map { it.name })
            unassignedAdapter.notifyDataSetChanged()

            // Deseleccionar para que puedas añadir más sin problema
            unassignedCoursesListView.clearChoices()
        }
    }

    private fun validateFields(): Boolean {
        if (name.text.isBlank() || title.text.isBlank()) {
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Adaptador personalizado para mostrar lista con botón "X" para eliminar cursos asignados
    inner class AssignedCoursesAdapter : BaseAdapter() {
        override fun getCount() = assignedCourses.size
        override fun getItem(position: Int) = assignedCourses[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@EditCareerActivity)
                .inflate(R.layout.item_course_with_remove, parent, false)

            val course = assignedCourses[position]
            val tvCourseName = view.findViewById<TextView>(R.id.tvCourseName)
            val btnRemove = view.findViewById<ImageButton>(R.id.btnRemoveCourse)

            tvCourseName.text = course.name
            btnRemove.setOnClickListener {
                // Quitar curso de asignados y agregar a no asignados
                assignedCourses.removeAt(position)
                assignedAdapter.notifyDataSetChanged()

                unassignedCourses.add(course)
                unassignedAdapter.clear()
                unassignedAdapter.addAll(unassignedCourses.map { it.name })
                unassignedAdapter.notifyDataSetChanged()
            }

            return view
        }
    }

    private inner class LoadAssignedCoursesTask : AsyncTask<Int, Void, List<Course_>?>() {
        override fun doInBackground(vararg params: Int?): List<Course_>? {
            val careerId = params[0] ?: return null
            try {
                val response = HttpHelper.getRawResponse("careers/$careerId/courses")
                if (response.isNullOrEmpty()) return null

                val coursesArray = org.json.JSONArray(response)
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
                return courses
            } catch (e: Exception) {
                Log.e("LoadAssignedCourses", "Error: ${e.message}", e)
                return null
            }
        }

        override fun onPostExecute(result: List<Course_>?) {
            if (result == null) {
                Toast.makeText(this@EditCareerActivity, "Error loading assigned courses.", Toast.LENGTH_LONG).show()
                return
            }
            assignedCourses.clear()
            assignedCourses.addAll(result)
            originalAssignedCourses.clear()
            originalAssignedCourses.addAll(result.map { it.copy() })
            assignedAdapter.notifyDataSetChanged()
            assignedCourses.clear()
            assignedCourses.addAll(result)
            assignedAdapter.notifyDataSetChanged()
        }
    }

    private inner class LoadUnassignedCoursesTask : AsyncTask<Int, Void, List<Course_>?>() {
        override fun doInBackground(vararg params: Int?): List<Course_>? {
            val careerId = params[0] ?: return null
            try {
                val response = HttpHelper.getRawResponse("courses/not_assigned/$careerId")
                if (response.isNullOrEmpty()) return null

                val jsonObject = org.json.JSONObject(response)
                val status = jsonObject.getString("status")
                if (status != "success") return null

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
                return courses
            } catch (e: Exception) {
                Log.e("LoadUnassignedCourses", "Error: ${e.message}", e)
                return null
            }
        }

        override fun onPostExecute(result: List<Course_>?) {
            if (result == null) {
                Toast.makeText(this@EditCareerActivity, "Error loading unassigned courses.", Toast.LENGTH_LONG).show()
                return
            }
            unassignedCourses.clear()
            unassignedCourses.addAll(result)
            unassignedAdapter.clear()
            unassignedAdapter.addAll(unassignedCourses.map { it.name })
            unassignedAdapter.notifyDataSetChanged()
        }
    }

    private inner class UpdateCareerTask : AsyncTask<Career_, Void, Pair<Boolean, String?>>() {
        override fun doInBackground(vararg params: Career_): Pair<Boolean, String?> {
            return try {
                val career = params[0]
                val originalCourses = originalAssignedCourses.map { it.cod }.toSet()
                val currentCourses = assignedCourses.map { it.cod }.toSet()

                // Construir el payload como objeto JSON directamente
                val payloadObject = JSONObject().apply {
                    put("career", JSONObject().apply {
                        put("cod", career.cod)
                        put("name", career.name)
                        put("title", career.title)
                    })
                    put("coursesToAdd", JSONArray(currentCourses.minus(originalCourses).toList()))
                    put("coursesToRemove", JSONArray(originalCourses.minus(currentCourses).toList()))
                }

                val payload = payloadObject.toString()
                Log.d("UpdateCareer", "Sending payload: $payload")

                // Usar sendRequest con método PUT
                val response = HttpHelper.sendRequest(
                    "careers",
                    "PUT",
                    payload,
                    String::class.java
                ) ?: return Pair(false, "No response was received from the server")

                Log.d("UpdateCareer", "Server response: $response")

                // Parsear respuesta manualmente
                val jsonResponse = JSONObject(response)
                Pair(
                    jsonResponse.optBoolean("success", false),
                    jsonResponse.optString("message", null)
                )
            } catch (e: Exception) {
                Log.e("UpdateCareer", "Error: ${e.message}", e)
                Pair(false, "Error al conectar con el servidor: ${e.message}")
            }
        }

        override fun onPostExecute(result: Pair<Boolean, String?>) {
            if (result.first) {
                Toast.makeText(this@EditCareerActivity, "Successfully updated career", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(
                    this@EditCareerActivity,
                    result.second ?: "Unknown error when updating",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}