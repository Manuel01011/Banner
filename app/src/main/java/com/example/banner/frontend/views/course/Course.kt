package com.example.banner.frontend.views.course
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

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
                        Toast.makeText(this, "Curso actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Inicialización del ActivityResultLauncher para agregar curso
       addCourseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val courseCode = data.getIntExtra("courseCode", -1)
                    val courseName = data.getStringExtra("courseName") ?: ""
                    val credits = data.getIntExtra("credits", -1)
                    val hours = data.getIntExtra("hours", -1)
                    val cycleId = data.getIntExtra("cycleId", -1)
                    val careerCode = data.getIntExtra("careerCode", -1)

                    // Crear un nuevo objeto Course con los datos recibidos
                    val newCourse = Course_(courseCode, courseName, credits, hours, cycleId, careerCode)

                    // Agregar el nuevo curso a la lista
                    fullList.add(newCourse)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Curso agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar el FAB para lanzar la actividad de agregar curso
        fabAgregarCurso.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddCourse::class.java)
            addCourseLauncher.launch(intent)
        }

        Log.d("CareerActivity", "Antes de setUpRecyclerView")
        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")

    }

    //devuelve la lista de los carreas
    private fun getCursos(): MutableList<Course_> {
        return mutableListOf(
            Course_(1,"Programacion 1",4,4,1,101),
            Course_(2,"Bases de datos 1",3,3,2,101),
            Course_(3,"Redes",4,4,2,101),
        )
    }

    //Habilitar Swipe con ItemTouchHelper


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getCursos()
        mAdapter = RecyclerAdapter4(fullList.toMutableList(), this)
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
        val curso = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Carrera eliminada: ${curso.name}", Toast.LENGTH_SHORT).show()
    }
}