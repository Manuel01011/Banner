package com.example.banner.frontend.views.career
import Career_
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

class Career : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Career_>
    private lateinit var fabAgregarCarrera: FloatingActionButton
    // Recyclerview de carreras
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter

    //editar
    // Elimina esto:
    private lateinit var editCareerLauncher: ActivityResultLauncher<Intent>

    private lateinit var addCareerLauncher: ActivityResultLauncher<Intent>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CareerActivity", "onCreate llamado")
        setContentView(R.layout.career)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()

        fabAgregarCarrera = findViewById(R.id.fabAgregarCarrera)

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


        editCareerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val cod = data.getIntExtra("cod", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val title = data.getStringExtra("title") ?: ""

                    if (position != -1) {
                        val updatedCareer = Career_(cod, name, title)
                        mAdapter.editItem(position, updatedCareer)
                        Toast.makeText(this, "Carrera actualizada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // Lanzador para agregar carrera
        addCareerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val cod = data.getIntExtra("cod", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val title = data.getStringExtra("title") ?: ""

                    val newCareer = Career_(cod, name, title)
                    fullList.add(newCareer)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Carrera agregada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón flotante para agregar nueva carrera
        fabAgregarCarrera.setOnClickListener {
            val intent = Intent(this, AddCareer::class.java)
            addCareerLauncher.launch(intent)
        }

        // Configurar RecyclerView y adaptador
        Log.d("CareerActivity", "Antes de setUpRecyclerView")
        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")

    }


    //devuelve la lista de los carreas
    private fun getSuperheros(): MutableList<Career_> {
        return mutableListOf(
            Career_(1, "Ingeniería en Sistemas", "Ingeniero en Sistemas"),
            Career_(2, "Ingeniería Civil", "Ingeniero Civil"),
            Career_(3, "Medicina", "Médico Cirujano")
        )
    }

    //Habilitar Swipe con ItemTouchHelper


    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getSuperheros()
        mAdapter = RecyclerAdapter(fullList.toMutableList(), this) { superhero ->
            // Handle edit action here (e.g., open a new activity or show a dialog)
            Toast.makeText(this, "Edit ${superhero.name}", Toast.LENGTH_SHORT).show()
        }

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

    fun editCareer(position: Int) {
        val carrera = mAdapter.getItem(position)
        val intent = Intent(this, EditCareerActivity::class.java).apply {
            putExtra("position", position)  // Añadimos la posición para saber qué item actualizar
            putExtra("cod", carrera.cod)
            putExtra("name", carrera.name)
            putExtra("title", carrera.title)
        }
        editCareerLauncher.launch(intent)
    }

    fun deleteCareer(position: Int) {
        val carrera = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Carrera eliminada: ${carrera.name}", Toast.LENGTH_SHORT).show()
    }
}


