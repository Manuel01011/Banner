package com.example.banner.frontend.views.career
import android.app.Activity
import android.app.AlertDialog
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
import com.example.backend_banner.backend.Models.Career_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

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
                        Toast.makeText(this, "Career updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // Lanzador para agregar carrera
        addCareerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadCareers()
                Toast.makeText(this, "Career successfully added", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón flotante para agregar nueva carrera
        fabAgregarCarrera.setOnClickListener {
            val intent = Intent(this, AddCareer::class.java)
            addCareerLauncher.launch(intent)
        }

        // Configurar RecyclerView y adaptador
        setUpRecyclerView()
        loadCareers() // Cargar datos del backend al iniciar
    }


    private fun getCareersFromBackend(): MutableList<Career_> {
        val response = HttpHelper.getRawResponse("careers")
        return try {
            val json = JSONObject(response)
            val dataArray = json.getJSONArray("data")
            val listType = object : TypeToken<List<Career_>>() {}.type
            Gson().fromJson<List<Career_>>(dataArray.toString(), listType).toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    private fun loadCareers() {
        object : AsyncTask<Void, Void, MutableList<Career_>>() {
            override fun doInBackground(vararg params: Void?): MutableList<Career_> {
                return getCareersFromBackend()
            }

            override fun onPostExecute(result: MutableList<Career_>) {
                super.onPostExecute(result)
                fullList = result
                mAdapter.updateData(fullList)
            }
        }.execute()
    }

    private fun getSuperheros(): MutableList<Career_> {
        return getCareersFromBackend()
    }

    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getSuperheros()
        mAdapter = RecyclerAdapter(fullList.toMutableList(), this) { superhero ->
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

    // editCareer para usar el backend
    fun editCareer(position: Int) {
        val carrera = mAdapter.getItem(position)
        val intent = Intent(this, EditCareerActivity::class.java).apply {
            putExtra("position", position)
            putExtra("cod", carrera.cod)
            putExtra("name", carrera.name)
            putExtra("title", carrera.title)
        }
        editCareerLauncher.launch(intent)
    }

    // deleteCareer para usar el backend
    fun deleteCareer(position: Int) {
        val carrera = mAdapter.getItem(position)

        // Mostrar diálogo de confirmación
        AlertDialog.Builder(this)
            .setTitle("Confirm deletion")
            .setMessage("¿Are you sure to eliminate the career ${carrera.name}?")
            .setPositiveButton("Delete") { dialog, _ ->
                dialog.dismiss()
                executeDelete(position, carrera)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun executeDelete(position: Int, carrera: Career_) {
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return HttpHelper.deleteRequest("careers/${carrera.cod}")
            }

            override fun onPostExecute(success: Boolean) {
                if (success) {
                    mAdapter.removeItem(position)
                    Toast.makeText(this@Career, "Career eliminated: ${carrera.name}", Toast.LENGTH_SHORT).show()

                    loadCareers()
                } else {
                    Toast.makeText(this@Career, "Error when deleting the Career", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }
}


