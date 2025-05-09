package com.example.banner.frontend.views.teacher
import Teacher_
import android.app.Activity
import android.content.Intent
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
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class Teacher : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Teacher_>
    private lateinit var fabAgregarProfesor: FloatingActionButton
    private lateinit var addTeacherLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter3

    //editar
    private lateinit var editTeacherLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarProfesor = findViewById(R.id.fabAgregarProfesor)
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

        editTeacherLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val id = data.getIntExtra("id", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val tel = data.getIntExtra("tel", 0)
                    val email = data.getStringExtra("email") ?: ""

                    if (position != -1) {
                        val updatedTeacher = Teacher_(id, name, tel, email)
                        mAdapter.editItem(position, updatedTeacher)
                        Toast.makeText(this, "Profesor actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



        addTeacherLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val profesorId = data.getIntExtra("profesorId", -1)
                    val profesorName = data.getStringExtra("profesorName") ?: ""
                    val profesorTelNumber = data.getIntExtra("profesorTel",-1)
                    val profesorEmail = data.getStringExtra("profesorEmail") ?: ""

                    // Crear un nuevo objeto Profesor con los datos recibidos
                    val newProfesor = Teacher_(profesorId, profesorName, profesorTelNumber, profesorEmail)

                    // Agregar el nuevo teacher a la lista
                    fullList.add(newProfesor)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Profesor agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        fabAgregarProfesor.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddTeacher::class.java)
            addTeacherLauncher.launch(intent)
        }

        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")

    }

    //devuelve la lista de los carreas
    private fun getSuperheros(): MutableList<Teacher_> {
        return mutableListOf(
            Teacher_(1,"Oscar",61823972,"Oscar@gamil.com"),
            Teacher_(2,"Tigre",62567524,"Tigre@gamil.com"),
            Teacher_(3,"George",62669395,"George@gamil.com"),
        )
    }



    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getSuperheros()
        mAdapter = RecyclerAdapter3(fullList.toMutableList(), this)
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

    fun editTeacher(position: Int) {
        val teacher = fullList[position]
        val intent = Intent(this, EditTeacherActivity::class.java).apply {
            putExtra("position", position)
            putExtra("id", teacher.id)
            putExtra("name", teacher.name)
            putExtra("tel", teacher.telNumber)
            putExtra("email", teacher.email)
        }
        editTeacherLauncher.launch(intent)
    }

    fun deleteProfessor(position: Int) {
        val professor = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Grupo eliminada: ${professor.id}", Toast.LENGTH_SHORT).show()
    }
}