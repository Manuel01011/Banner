package com.example.banner.frontend.views.rol_matriculador
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Student_
import com.example.banner.MainActivity
import com.example.banner.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class StudentListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private val fullList = mutableListOf<Student_>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)

        // 1. Configuración inicial del Adapter
        studentAdapter = StudentAdapter(
            emptyList(),
            onEnrollClick = { student ->
                // Lógica para matricular cursos
                openEnrollmentActivity(student)
            },
            onClick = { selectedStudent ->
                // Lógica para ver detalles (existente)
                val intent = Intent(this, StudentEnrollmentsActivity::class.java).apply {
                    putExtra("studentId", selectedStudent.id)
                    putExtra("student_name", selectedStudent.name)
                }
                startActivity(intent)
            }
        )


        // 2. Configuración completa del RecyclerView
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewStudents).apply {
            this.layoutManager = LinearLayoutManager(this@StudentListActivity)
            this.adapter = studentAdapter
            this.setHasFixedSize(true)
            this.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // 3. Configuración del SearchView
        searchView = findViewById<SearchView>(R.id.searchViewStudents).apply {
            this.setIconifiedByDefault(false)
            this.queryHint = "Search student"

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    studentAdapter.filter(newText ?: "")
                    return true
                }
            })
            requestFocus()
        }

        menuButton.setOnClickListener {
            try {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Verifica que el navigationView esté correctamente configurado
                    if (::navigationView.isInitialized) {
                        drawerLayout.openDrawer(GravityCompat.START)
                    } else {
                        Log.e("NAV_ERROR", "NavigationView not initialized")
                    }
                }
            } catch (e: Exception) {
                Log.e("DRAWER_ERROR", "Error handling drawer", e)
                Toast.makeText(this, "Error opening the menu", Toast.LENGTH_SHORT).show()
            }
        }

        navigationView = findViewById<NavigationView>(R.id.navigation_view).apply {
            setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_logout -> {
                        val intent = Intent(this@StudentListActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

        // 4. Carga inicial de datos
        loadStudents()
    }

    private fun openEnrollmentActivity(student: Student_) {
        val intent = Intent(this, StudentEnrollmentsActivity::class.java).apply {
            putExtra("student_id", student.id)
            putExtra("student_name", student.name)
        }
        startActivity(intent)
    }

    private fun loadStudents() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading students...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, List<Student_>>() {
            override fun doInBackground(vararg params: Void?): List<Student_> {
                return try {
                    val response = HttpHelper.get("students") ?: return emptyList()
                    Log.d("API_RESPONSE", response)

                    val json = JSONObject(response)
                    if (json.getString("status") != "success") {
                        Log.e("API_ERROR", "Estado no exitoso")
                        return emptyList()
                    }

                    val dataArray = json.getJSONArray("data")
                    Gson().fromJson<List<Student_>>(
                        dataArray.toString(),
                        object : TypeToken<List<Student_>>() {}.type
                    ) ?: emptyList()

                } catch (e: Exception) {
                    Log.e("LOAD_ERROR", "Error al cargar", e)
                    emptyList()
                }
            }

            override fun onPostExecute(result: List<Student_>) {
                progressDialog.dismiss()
                Log.d("LOAD_RESULT", "Estudiantes cargados: ${result.size}")

                fullList.apply {
                    clear()
                    addAll(result)
                }
                studentAdapter.updateData(fullList)

                if (result.isEmpty()) {
                    Toast.makeText(
                        this@StudentListActivity,
                        "No students found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.execute()
    }
}