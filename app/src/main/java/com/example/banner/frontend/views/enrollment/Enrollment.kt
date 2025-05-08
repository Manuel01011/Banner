package com.example.banner.frontend.views.enrollment
import Enrollment_
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
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class Enrollment : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Enrollment_>
    private lateinit var fabAgregarMatricula: FloatingActionButton
    private lateinit var addEnrollmentLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de carreras
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter7
    //edit
    private lateinit var editEnrollmentLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enrrollment)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarMatricula = findViewById(R.id.fabAgregarMatricula)
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
                    finish()
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        // Registra el ActivityResultLauncher para recibir el resultado de la actividad
        addEnrollmentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val studentId = data.getIntExtra("studentId", -1)
                    val grupoId = data.getIntExtra("grupoId", -1)
                    val grade = data.getDoubleExtra("grade", 0.0)
                    // Crear un nuevo objeto Matrícula con los datos recibidos
                    val newEnrollment = Enrollment_(studentId, grupoId, grade)
                    // Agregar la nueva matrícula a la lista
                    fullList.add(newEnrollment)
                    // Actualizar el adaptador con los nuevos datos
                    mAdapter.updateData(fullList)
                    // Mostrar mensaje confirmando la acción
                    Toast.makeText(this, "Matrícula agregada correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editEnrollmentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val studentId = data.getIntExtra("studentId", -1)
                    val grupoId = data.getIntExtra("grupoId", -1)
                    val grade = data.getDoubleExtra("grade", 0.0)

                    if (position != -1) {
                        fullList[position] = Enrollment_(studentId, grupoId, grade)
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Matrícula actualizada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fabAgregarMatricula.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddEnrollment::class.java)
            addEnrollmentLauncher.launch(intent)
        }

        Log.d("CareerActivity", "Antes de setUpRecyclerView")
        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")

    }

    //devuelve la lista de los carreas
    private fun getMatriculas(): MutableList<Enrollment_> {
        return mutableListOf(
           Enrollment_(1,2,8.5),
            Enrollment_(2,2,7.5),
            Enrollment_(3,2,7.6),
        )
    }

    //


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getMatriculas()
        mAdapter = RecyclerAdapter7(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.studentId.toString().contains(newText, ignoreCase = true) }
                } else {
                    fullList
                }
                mAdapter.updateData(filtered)
                return true
            }
        })
    }

    fun editEnrollment(position: Int) {
        val enrollment = fullList[position]
        val intent = Intent(this, EditEnrollmentActivity::class.java).apply {
            putExtra("position", position)
            putExtra("studentId", enrollment.studentId)
            putExtra("grupoId", enrollment.grupoId)
            putExtra("grade", enrollment.grade)
        }
        editEnrollmentLauncher.launch(intent)
    }
    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun deleteEnrollment(position: Int) {
        val matriclula = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Matricula eliminada: ${matriclula.studentId}", Toast.LENGTH_SHORT).show()
    }
}