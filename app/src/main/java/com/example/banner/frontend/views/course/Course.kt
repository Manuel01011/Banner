package com.example.banner.frontend.views.course
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class Course : AppCompatActivity(){
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Course_>
    private lateinit var fabAgregarCurso: FloatingActionButton
    // Recyclerview de carreras
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter4
    private lateinit var addCourseLauncher: ActivityResultLauncher<Intent>
    //editar
    private lateinit var editCourseLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarCurso = findViewById(R.id.fabAgregarCurso)

        // Botón para abrir el menú lateral
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo de las opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->
            Log.d("AdminActivity", "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})")

            when (item.itemId) {
                R.id.nav_logout -> {
                    Log.d("ProfessorActivity", "Logout Clicked")
                    finish() // Cierra la actividad actual y vuelve a la anterior
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //editar
        editCourseLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val cod = data.getIntExtra("cod", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val credits = data.getIntExtra("credits", 0)
                    val hours = data.getIntExtra("hours", 0)
                    val cicloId = data.getIntExtra("cicloId", 0)
                    val careerCod = data.getIntExtra("careerCod", 0)

                    if (position != -1) {
                        fullList[position] = Course_(cod, name, credits, hours, cicloId, careerCod)
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Updated course", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Inicialización del ActivityResultLauncher para agregar curso
        addCourseLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadCourses() // Recargar datos del backend
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el FAB para lanzar la actividad de agregar curso
        fabAgregarCurso.setOnClickListener {
            val intent = Intent(this, AddCourse::class.java)
            addCourseLauncher.launch(intent)
        }

        setUpRecyclerView()
        loadCourses()
    }

    //devuelve la lista de los carreas
    private fun getCoursesFromBackend(): MutableList<Course_> {
        val response = HttpHelper.getRawResponse("courses")
        return try {
            val json = JSONObject(response)
            val dataArray = json.getJSONArray("data")
            val listType = object : TypeToken<List<Course_>>() {}.type
            Gson().fromJson<List<Course_>>(dataArray.toString(), listType).toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa con lista vacía
        fullList = mutableListOf()
        mAdapter = RecyclerAdapter4(fullList, this)
        mRecyclerView.adapter = mAdapter

        // Cargar datos del backend
        loadCourses()

        // Configuración del SearchView
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

    private fun loadCourses() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading courses...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, MutableList<Course_>>() {
            override fun doInBackground(vararg params: Void?): MutableList<Course_> {
                return try {
                    val response = HttpHelper.getRawResponse("courses")
                    Log.d("API_RESPONSE", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Course_>>() {}.type
                        Gson().fromJson<List<Course_>>(dataArray.toString(), type).toMutableList()
                    } else {
                        mutableListOf()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading courses", e)
                    mutableListOf()
                }
            }

            override fun onPostExecute(result: MutableList<Course_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList = result
                    mAdapter.updateData(fullList)
                } else {
                    Toast.makeText(
                        this@Course,
                        "Courses could not be uploaded",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.execute()
    }

    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun editCourse(position: Int) {
        val course = fullList[position]
        val intent = Intent(this, EditCourseActivity::class.java).apply {
            putExtra("position", position)
            putExtra("cod", course.cod)
            putExtra("name", course.name)
            putExtra("credits", course.credits)
            putExtra("hours", course.hours)
            putExtra("cicloId", course.cicloId)
            putExtra("careerCod", course.careerCod)
        }
        editCourseLauncher.launch(intent)
    }

    fun deleteCourse(position: Int) {
        val course = mAdapter.getItem(position)

        AlertDialog.Builder(this)
            .setTitle("Confirm deletion")
            .setMessage("¿Delete the course ${course.name}?")
            .setPositiveButton("Delete") { dialog, _ ->
                executeDelete(position, course)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun executeDelete(position: Int, course: Course_) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Eliminating course...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return try {
                    HttpHelper.deleteRequest("courses/${course.cod}")
                } catch (e: Exception) {
                    Log.e("DELETE_ERROR", "Error when deleting course", e)
                    false
                }
            }

            override fun onPostExecute(success: Boolean) {
                progressDialog.dismiss()
                if (success) {
                    fullList.removeAt(position)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this@Course, "Course eliminated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@Course,
                        "Error when deleting the course",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.execute()
    }
}