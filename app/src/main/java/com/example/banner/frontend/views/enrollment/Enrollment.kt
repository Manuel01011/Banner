package com.example.banner.frontend.views.enrollment
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
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
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import android.os.AsyncTask
import org.json.JSONObject

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
        // Inicialización del ActivityResultLauncher para agregar matrícula
        addEnrollmentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadEnrollments() // Recargar datos del backend
                Toast.makeText(this, "Enrollment added successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Agregar esto en onCreate() después de inicializar addEnrollmentLauncher
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
                        // Actualizar la lista local
                        fullList[position] = Enrollment_(studentId, grupoId, grade)
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Enrollment updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // Configurar el FAB para lanzar la actividad de agregar matrícula
        fabAgregarMatricula.setOnClickListener {
            val intent = Intent(this, AddEnrollment::class.java)
            addEnrollmentLauncher.launch(intent)
        }

        setUpRecyclerView()
        loadEnrollments()

    }


    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa con lista vacía
        fullList = mutableListOf()
        mAdapter = RecyclerAdapter7(fullList, this)
        mRecyclerView.adapter = mAdapter

        // Configuración del SearchView
        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter {
                        it.studentId.toString().contains(newText, ignoreCase = true) ||
                                it.grupoId.toString().contains(newText, ignoreCase = true) ||
                                it.grade.toString().contains(newText, ignoreCase = true)
                    }
                } else {
                    fullList
                }
                mAdapter.updateData(filtered)
                return true
            }
        })
    }


    private fun loadEnrollments() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading enrollments...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, MutableList<Enrollment_>>() {
            override fun doInBackground(vararg params: Void?): MutableList<Enrollment_> {
                return try {
                    val response = HttpHelper.getRawResponse("enrollments")
                    Log.d("API_RESPONSE", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Enrollment_>>() {}.type
                        Gson().fromJson<List<Enrollment_>>(dataArray.toString(), type).toMutableList()
                    } else {
                        mutableListOf()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading enrollments", e)
                    mutableListOf()
                }
            }

            override fun onPostExecute(result: MutableList<Enrollment_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList = result
                    mAdapter.updateData(fullList)
                } else {
                    Toast.makeText(
                        this@Enrollment,
                        "Enrollments could not be uploaded",
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

    fun deleteEnrollment(position: Int) {
        val enrollment = mAdapter.getItem(position)

        AlertDialog.Builder(this)
            .setTitle("Confirm deletion")
            .setMessage("Are you sure to remove this enrollment?")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    val success = HttpHelper.deleteRequest("enrollments/${enrollment.studentId}/${enrollment.grupoId}")

                    runOnUiThread {
                        if (success) {
                            mAdapter.removeItem(position)
                            Toast.makeText(this, "Enrollments deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error when deleting", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun editEnrollment(position: Int) {
        val enrollment = mAdapter.getItem(position)
        val intent = Intent(this, EditEnrollmentActivity::class.java).apply {
            putExtra("position", position)
            putExtra("studentId", enrollment.studentId)
            putExtra("grupoId", enrollment.grupoId)
            putExtra("grade", enrollment.grade)
        }
        editEnrollmentLauncher.launch(intent)
    }
}