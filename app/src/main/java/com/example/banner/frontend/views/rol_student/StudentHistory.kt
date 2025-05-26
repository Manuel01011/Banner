package com.example.banner.frontend.views.rol_student
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.R
import android.util.Log
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.banner.MainActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class StudentHistory : AppCompatActivity() {
    private lateinit var studentName: TextView
    private lateinit var studentIdView: TextView
    private lateinit var studentCareer: TextView
    private lateinit var menuButton: ImageButton
    private lateinit var studentAverage: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentHistoryAdapter
    private lateinit var progressDialog: ProgressDialog
    private var studentId: Int = -1
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_history)

        // Initialize views
        studentName = findViewById(R.id.student_name)
        studentIdView = findViewById(R.id.student_id)
        studentCareer = findViewById(R.id.student_career)
        studentAverage = findViewById(R.id.student_average)
        recyclerView = findViewById(R.id.history_recycler_view)
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configure RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        // Get student ID from Intent
        studentId = intent.getIntExtra("USER_ID", -1)
        if (studentId == -1) {
            Toast.makeText(this, "Error: Student ID not provided", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Set up progress dialog
        progressDialog = ProgressDialog(this).apply {
            setTitle("Loading")
            setMessage("Obtaining academic history...")
            setCancelable(false)
        }
        progressDialog.show()
        // Cargar el historial académico
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:8080/api/students/academic-history?studentId=$studentId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val json = JSONObject(response)
                    val dataArray = json.getJSONArray("data")

                    val items = mutableListOf<StudentHistoryItem>()

                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)

                        val course = item.getJSONObject("course")
                        val courseName = course.getString("name")
                        val credits = course.getInt("credits")

                        val grade = item.getDouble("grade")

                        val cycle = item.getJSONObject("cycle")
                        val cycleStr = "${cycle.getInt("year")}-${cycle.getInt("number")}"

                        val teacher = item.getString("teacher")

                        val career = item.getJSONObject("career")
                        val careerName = career.getString("name")

                        items.add(
                            StudentHistoryItem(
                                courseName = courseName,
                                grade = grade,
                                credits = credits,
                                cycle = cycleStr,
                                teacher = teacher,
                                careerName = careerName
                            )
                        )
                    }

                    // Actualizar UI en el hilo principal
                    withContext(Dispatchers.Main) {
                        adapter.updateData(items)
                        progressDialog.dismiss()

                        // Mostrar info adicional si quieres (por ejemplo nombre/carrera del primer curso)
                        studentIdView.text = "ID: $studentId"
                        studentCareer.text = "Carrera: ${items.firstOrNull()?.careerName ?: "Unknown"}"
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@StudentHistory,
                            "Error loading history. Code: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@StudentHistory, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            Log.d("AdminActivity", "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})")

            when (item.itemId) {
                R.id.nav_logout -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }
}