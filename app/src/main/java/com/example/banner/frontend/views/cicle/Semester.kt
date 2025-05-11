package com.example.banner.frontend.views.cicle
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
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Ciclo_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

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

        setUpRecyclerView()
        loadCiclos()

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


        addCycleLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadCiclos() // Recarga los datos del servidor
                Toast.makeText(this, "Ciclo agregado", Toast.LENGTH_SHORT).show()
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

    //devuelve la lista de los carreas del backend
    private fun getSuperheros(): MutableList<Ciclo_> {
        return getCiclosFromBackend()
    }

    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa con lista vacía
        fullList = mutableListOf()
        mAdapter = RecyclerAdapter2(fullList, this)
        mRecyclerView.adapter = mAdapter

        // Configuración del SearchView
        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter {
                        "Semester ${it.number} - ${it.year}".contains(newText, ignoreCase = true) ||
                                it.dateStart.contains(newText, ignoreCase = true) ||
                                it.dateFinish.contains(newText, ignoreCase = true)
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


    // Método para obtener la lista de ciclos desde el backend
    private fun getCiclosFromBackend(): MutableList<Ciclo_> {
        val response = HttpHelper.getRawResponse("ciclos")
        return try {
            val json = JSONObject(response)
            val dataArray = json.getJSONArray("data")
            val listType = object : TypeToken<List<Ciclo_>>() {}.type
            Gson().fromJson<List<Ciclo_>>(dataArray.toString(), listType).toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    // Método para cargar los ciclos desde el backend
    private fun loadCiclos() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading cycles...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, MutableList<Ciclo_>>() {
            override fun doInBackground(vararg params: Void?): MutableList<Ciclo_> {
                return try {
                    val response = HttpHelper.getRawResponse("ciclos")
                    Log.d("API_RESPONSE", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")

                        // Solución: Especifica explícitamente el tipo
                        val type = object : TypeToken<List<Ciclo_>>() {}.type
                        val lista: List<Ciclo_> = Gson().fromJson(dataArray.toString(), type)

                        lista.toMutableList()
                    } else {
                        mutableListOf()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading ciclos", e)
                    mutableListOf()
                }
            }

            override fun onPostExecute(result: MutableList<Ciclo_>) {
                progressDialog.dismiss()

                if (result.isNotEmpty()) {
                    fullList = result
                    mAdapter.updateData(fullList)
                    Log.d("LOAD_CICLOS", "Datos cargados: ${fullList.size} ciclos")
                } else {
                    Toast.makeText(
                        this@Semester,
                        "No se pudieron cargar los ciclos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.execute()
    }

    // Método para eliminar un ciclo
    fun deleteCiclo(position: Int) {
        val ciclo = mAdapter.getItem(position)

        AlertDialog.Builder(this)
            .setTitle("Confirm deletion")
            .setMessage("¿Eliminate the semester ${ciclo.number} of the year ${ciclo.year}?")
            .setPositiveButton("Delete") { dialog, _ ->
                executeDelete(position, ciclo)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun executeDelete(position: Int, ciclo: Ciclo_) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Eliminating cicle...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                return try {
                    HttpHelper.deleteRequest("ciclos/${ciclo.id}")
                } catch (e: Exception) {
                    Log.e("DELETE_ERROR", "Error when deleting cicle", e)
                    false
                }
            }

            override fun onPostExecute(success: Boolean) {
                progressDialog.dismiss()

                if (success) {
                    fullList.removeAt(position)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this@Semester, "Cicle eliminated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@Semester,
                        "Error when deleting the cicle",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.execute()
    }

}