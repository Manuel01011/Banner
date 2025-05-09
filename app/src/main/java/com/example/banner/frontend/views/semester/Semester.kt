package com.example.banner.frontend.views.semester
import Ciclo_
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

class Semester : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Ciclo_>
    private lateinit var fabAgregarCiclo: FloatingActionButton
    private lateinit var addCycleLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de ciclos
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter2

    //editar
    private lateinit var editCicloLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.semester)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarCiclo = findViewById(R.id.fabAgregarCiclo)

        // Botón para abrir el menú lateral
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo de las opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->
            Log.d("AdminActivity", "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})")

            when (item.itemId) {
                R.id.nav_logout -> {
                    Log.d("CicloActivity", "Logout Clicked")
                    finish()
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

// En tu clase Semester, dentro de onCreate()
        editCicloLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val id = data.getIntExtra("id", -1)
                    val year = data.getIntExtra("year", 0)
                    val number = data.getIntExtra("number", 0)
                    val dateStart = data.getStringExtra("dateStart") ?: ""
                    val dateFinish = data.getStringExtra("dateFinish") ?: ""
                    val isActive = data.getBooleanExtra("is_active", false)

                    if (position != -1) {
                        val updatedCiclo = Ciclo_(id, year, number, dateStart, dateFinish, isActive)
                        fullList[position] = updatedCiclo
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Semestre actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        addCycleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val cycleId = data.getIntExtra("id", -1)
                    val cycleYear = data.getIntExtra("year", -1)
                    val cycleNumber = data.getIntExtra("number", -1)
                    val startDate = data.getStringExtra("startDate") ?: ""
                    val endDate = data.getStringExtra("endDate") ?: ""
                    val isActive = data.getBooleanExtra("isActive", false)

                    // Crear un nuevo objeto Cycle con los datos recibidos
                    val newCycle = Ciclo_(cycleId, cycleYear, cycleNumber, startDate, endDate, isActive)

                    // Agregar el nuevo semester a la lista
                    fullList.add(newCycle)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Semester agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }

            fabAgregarCiclo.setOnClickListener {
                // Iniciar la actividad para agregar un semester
                val intent = Intent(this, AddSemester::class.java)
                addCycleLauncher.launch(intent)
            }

        Log.d("CareerActivity", "Antes de setUpRecyclerView")
        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")

    }

    //devuelve la lista de los carreas
    private fun getSuperheros(): MutableList<Ciclo_> {
        return mutableListOf(
            Ciclo_(1,2025,1,"17 febrero","22 julio",true),
            Ciclo_(2,2025,2,"1 agosto","30 noviembre",false),
            Ciclo_(3,2026,1,"15 febrero","25 julio",false),
        )
    }

    //Habilitar Swipe con ItemTouchHelper


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getSuperheros()
        mAdapter = RecyclerAdapter2(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    // Filtra por el título del semester, que es la concatenación de "Semester number - year"
                    fullList.filter {
                        "Semester ${it.number} - ${it.year}".contains(newText, ignoreCase = true)
                    }
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
    fun editCiclo(position: Int) {
        val ciclo = fullList[position]
        val intent = Intent(this, EditSemesterActivity::class.java).apply {
            putExtra("position", position)
            putExtra("id", ciclo.id)
            putExtra("year", ciclo.year)
            putExtra("number", ciclo.number)
            putExtra("dateStart", ciclo.dateStart)
            putExtra("dateFinish", ciclo.dateFinish)
            putExtra("is_active", ciclo.is_active)
        }
        editCicloLauncher.launch(intent)
    }

    fun deleteCiclo(position: Int) {
        val ciclo = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Semester eliminada: ${ciclo.id}", Toast.LENGTH_SHORT).show()
    }
}