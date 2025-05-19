package com.example.banner.frontend.views.student
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class Student : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Student_>
    private lateinit var fabAgregarEstudiante: FloatingActionButton
    private lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter5

    //editar
    private lateinit var editStudentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarEstudiante = findViewById(R.id.fabAgregarEstudiante)

        // Botón para abrir el menú lateral
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo de las opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->
            Log.d(
                "AdminActivity",
                "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})"
            )

            when (item.itemId) {
                R.id.nav_logout -> {
                    Log.d("ProfessorActivity", "Logout Clicked")
                    finish()
                }

                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //editar
        editStudentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val studentId = data.getIntExtra("id", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val telNumber = data.getIntExtra("telNumber", 0)
                    val email = data.getStringExtra("email") ?: ""
                    val bornDate = data.getStringExtra("bornDate") ?: ""
                    val careerCod = data.getIntExtra("careerCod", 0)
                    val password = data.getStringExtra("password") ?: ""

                    if (position != -1) {
                        fullList[position] = Student_(
                            id = studentId,
                            name = name,
                            telNumber = telNumber,
                            email = email,
                            bornDate = bornDate,
                            careerCod = careerCod,
                            password = password
                        )
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        addStudentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadStudents() // Recarga los datos del servidor
                Toast.makeText(this, "Estudiante agregado", Toast.LENGTH_SHORT).show()
            }
        }

        fabAgregarEstudiante.setOnClickListener {
            val intent = Intent(this, AddStudent::class.java)
            addStudentLauncher.launch(intent)
        }
        loadStudents()
        setUpRecyclerView()
    }


    //devuelve la lista de los carreas
    private fun getStudents(): MutableList<Student_> {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading students...")
            setCancelable(false)
            show()
        }

        val students = mutableListOf<Student_>()
        try {
            val response = HttpHelper.getRawResponse("students")
            if (response != null) {
                val json = JSONObject(response)
                val dataArray = json.getJSONArray("data")
                val type = object : TypeToken<List<Student_>>() {}.type
                students.addAll(Gson().fromJson<List<Student_>>(dataArray.toString(), type))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading students", e)
        } finally {
            progressDialog.dismiss()
        }

        return students
    }

    private fun loadStudents() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading students...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, List<Student_>>() {
            override fun doInBackground(vararg params: Void?): List<Student_> {
                return try {
                    val response = HttpHelper.getRawResponse("students")
                    Log.d("API_STUDENTS", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Student_>>() {}.type
                        Gson().fromJson(dataArray.toString(), type)
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading students", e)
                    emptyList()
                }
            }

            override fun onPostExecute(result: List<Student_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList.clear()
                    fullList.addAll(result)
                    mAdapter.updateData(fullList)
                    Log.d("LOAD_STUDENTS", "Loaded students: ${fullList.size}")
                } else {
                    Toast.makeText(this@Student, "No se encontraron estudiantes", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getStudents()
        mAdapter = RecyclerAdapter5(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.name.contains(newText, ignoreCase = true) }
                } else {
                    fullList
                }
                mAdapter.updateData(filtered)
                return true
            }
        })
    }

    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    // En tu clase Student
    fun editStudent(position: Int) {
        val student = fullList[position]
        val intent = Intent(this, EditStudentActivity::class.java).apply {
            putExtra("position", position)
            putExtra("id", student.id)
            putExtra("name", student.name)
            putExtra("telNumber", student.telNumber)
            putExtra("email", student.email)
            putExtra("bornDate", student.bornDate)
            putExtra("careerCod", student.careerCod)
        }
        editStudentLauncher.launch(intent)
    }

    fun deleteStudent(position: Int) {
        val student = mAdapter.getItem(position)
        Log.d("DELETE_STUDENT", "Attempting to delete student with ID: ${student.id}")

        AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("ArAre you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    try {
                        val url = "students/${student.id}"
                        Log.d("DELETE_STUDENT", "Request URL: $url")

                        val success = HttpHelper.deleteRequest(url)
                        Log.d("DELETE_STUDENT", "Delete response: $success")

                        runOnUiThread {
                            if (success) {
                                mAdapter.removeItem(position)
                                Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
                                loadStudents()
                            } else {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DELETE_STUDENT", "Error deleting student", e)
                        runOnUiThread {
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}