package com.example.banner
import CareerController
import Career_
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class Career : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CareerAdapter
    private lateinit var searchView: SearchView
    private val controller = CareerController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.career)

        recyclerView = findViewById(R.id.recycler_view)
        searchView = findViewById(R.id.search_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()

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

        //Recyclen de carreras
        val careerList = controller.getAllCareers().map {
            Career_(it.first, it.second, it.third)
        }.toMutableList()

        adapter = CareerAdapter(careerList) { career ->
            // Click sobre una carrera → Editar
            showCareerDialog(editMode = true, career = career)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Implementar Swipe
        setupSwipe()

        // Buscar carrera
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        // Botón flotante para agregar carrera (añádelo a tu layout)
        val fab: FloatingActionButton = findViewById(R.id.fab_add)
        fab.setOnClickListener {
            showCareerDialog(editMode = false)
        }
    }

    private fun setupSwipe() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(...) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val career = adapter.getCareerAt(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        controller.deleteCareer(career.cod)
                        adapter.removeItem(position)
                        Toast.makeText(this@Career, "Carrera eliminada", Toast.LENGTH_SHORT).show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        showCareerDialog(editMode = true, career = career)
                        adapter.notifyItemChanged(position) // evitar que se borre visualmente
                    }
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    private fun showCareerDialog(editMode: Boolean, career: Career_? = null) {
        val dialog = CareerFormDialog(this, editMode, career) { result ->
            if (editMode) {
                controller.editCareer(result.cod, result.name, result.title, listOf(), listOf())
                adapter.updateItemByCod(result.cod, result)
            } else {
                controller.insertCareer(result.cod, result.name, result.title)
                adapter.addItem(result)
            }
        }
        dialog.show()
    }
    }

    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}