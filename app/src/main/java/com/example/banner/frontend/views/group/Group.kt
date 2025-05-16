package com.example.banner.frontend.views.group
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
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


class Group : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Grupo_>
    private lateinit var fabAgregarGrupo: FloatingActionButton
    private lateinit var addGroupLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter6
    private lateinit var courseList: MutableList<Course_>
    //edit
    private lateinit var editGroupLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarGrupo = findViewById(R.id.fabAgregarGrupo)
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
        addGroupLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadGroups() // Recargar datos del backend
                Toast.makeText(this, "Group successfully added", Toast.LENGTH_SHORT).show()
            }
        }

        //edit
        editGroupLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val id = data.getIntExtra("id", -1)
                    val number = data.getIntExtra("number", -1)
                    val year = data.getIntExtra("year", -1)
                    val horario = data.getStringExtra("horario") ?: ""
                    val courseCode = data.getIntExtra("courseCode", -1)
                    val teacherId = data.getIntExtra("teacherId", -1)

                    if (position != -1) {
                        fullList[position] = Grupo_(
                            id = id,
                            numberGroup = number,
                            year = year,
                            horario = horario,
                            courseCod = courseCode,
                            teacherId = teacherId
                        )
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Group updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        fabAgregarGrupo.setOnClickListener {
            val intent = Intent(this, AddGroup::class.java)
            addGroupLauncher.launch(intent)
        }

        initViews()
        loadGroups()
        setUpRecyclerView()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        fabAgregarGrupo = findViewById(R.id.fabAgregarGrupo)
        searchView = findViewById(R.id.search_view)

        // Configurar RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar lista vacía
        fullList = mutableListOf()
        mAdapter = RecyclerAdapter6(fullList, this)
        mRecyclerView.adapter = mAdapter
    }

    private fun loadGroups() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading groups...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, List<Grupo_>>() {
            override fun doInBackground(vararg params: Void?): List<Grupo_> {
                return try {
                    val response = HttpHelper.getRawResponse("groups")
                    Log.d("API_GROUPS", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Grupo_>>() {}.type
                        Gson().fromJson(dataArray.toString(), type)
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading groups", e)
                    emptyList()
                }
            }

            override fun onPostExecute(result: List<Grupo_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList.clear()
                    fullList.addAll(result)
                    mAdapter.updateData(fullList)
                    Log.d("LOAD_GROUPS", "Loaded groups: ${fullList.size}")
                } else {
                    Toast.makeText(this@Group, "No groups were found", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    //devuelve la lista de los carreas
    private fun getGrupos(): MutableList<Grupo_> {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading groups...")
            setCancelable(false)
            show()
        }

        val grupos = mutableListOf<Grupo_>()

        try {
            val response = HttpHelper.getRawResponse("groups")
            if (response != null) {
                val json = JSONObject(response)
                val dataArray = json.getJSONArray("data")
                val type = object : TypeToken<List<Grupo_>>() {}.type
                grupos.addAll(Gson().fromJson<List<Grupo_>>(dataArray.toString(), type))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading groups", e)
        } finally {
            progressDialog.dismiss()
        }

        return grupos
    }

    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getGrupos()
        mAdapter = RecyclerAdapter6(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.horario.contains(newText, ignoreCase = true) }
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
    fun editGroup(position: Int) {
        val group = fullList[position]
        val intent = Intent(this, EditGroupActivity::class.java).apply {
            putExtra("position", position)
            putExtra("id", group.id)
            putExtra("number", group.numberGroup)
            putExtra("year", group.year)
            putExtra("horario", group.horario)
            putExtra("courseCode", group.courseCod)
            putExtra("teacherId", group.teacherId)
        }
        editGroupLauncher.launch(intent)
    }

    fun deleteGrupo(position: Int) {
        val grupo = mAdapter.getItem(position)
        Log.d("DELETE_GROUP", "Attempting to delete group with ID: ${grupo.id}")

        AlertDialog.Builder(this)
            .setTitle("Confirm deletion")
            .setMessage("Are you sure to remove this Group?")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    try {
                        val url = "groups/${grupo.id}"
                        Log.d("DELETE_GROUP", "Request URL: $url")

                        val success = HttpHelper.deleteRequest(url)
                        Log.d("DELETE_GROUP", "Delete response: $success")

                        runOnUiThread {
                            if (success) {
                                mAdapter.removeItem(position)
                                Toast.makeText(this, "Group deleted", Toast.LENGTH_SHORT).show()
                                // Recargar los grupos para sincronizar con el servidor
                                loadGroups()
                            } else {
                                Toast.makeText(this, "Error when deleting", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DELETE_GROUP", "Error deleting group", e)
                        runOnUiThread {
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}