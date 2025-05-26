package com.example.banner.frontend.views.admin
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.MainActivity
import com.example.banner.R
import com.example.banner.backend.Models.RegistrationRequest_
import com.example.banner.frontend.views.career.Career
import com.example.banner.frontend.views.cicle.Semester
import com.example.banner.frontend.views.course.Course
import com.example.banner.frontend.views.enrollment.Enrollment
import com.example.banner.frontend.views.group.Group
import com.example.banner.frontend.views.professor.Teacher
import com.example.banner.frontend.views.register.RegistrationRequestAdapter
import com.example.banner.frontend.views.student.Student
import com.example.banner.frontend.views.user.User
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Admin : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var textViewWelcome: TextView
    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var requestsAdapter: RegistrationRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin)

        initViews()
        setupNavigation()
        setupRequestsRecyclerView()
        loadPendingRequests()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        menuButton = findViewById(R.id.menu_button)
        textViewWelcome = findViewById(R.id.textViewWelcome)
        requestsRecyclerView = findViewById(R.id.requests_recycler_view)

        navigationView.bringToFront()
        navigationView.requestFocus()
    }

    private fun setupNavigation() {
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            handleNavigationItemSelected(item)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun handleNavigationItemSelected(item: MenuItem) {
        Log.d("AdminActivity", "Menu item clicked: ${item.itemId}")

        when (item.itemId) {
            R.id.nav_students -> startActivity(Intent(this, Student::class.java))
            R.id.nav_professors -> startActivity(Intent(this, Teacher::class.java))
            R.id.nav_courses -> startActivity(Intent(this, Course::class.java))
            R.id.nav_carrer -> startActivity(Intent(this, Career::class.java))
            R.id.nav_Ciclo -> startActivity(Intent(this, Semester::class.java))
            R.id.nav_enrrollment -> startActivity(Intent(this, Enrollment::class.java))
            R.id.nav_group -> startActivity(Intent(this, Group::class.java))
            R.id.nav_user -> startActivity(Intent(this, User::class.java))
            R.id.nav_registration_requests -> loadPendingRequests()
            R.id.nav_logout -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
        }
    }

    private fun setupRequestsRecyclerView() {
        requestsRecyclerView.layoutManager = LinearLayoutManager(this)
        requestsAdapter = RegistrationRequestAdapter(
            onApprove = { requestId -> approveRequest(requestId) },
            onReject = { requestId -> rejectRequest(requestId) }
        )
        requestsRecyclerView.adapter = requestsAdapter
    }

    private fun loadPendingRequests() {
        Thread {
            try {
                val response = HttpHelper.get("registration-requests/pending")

                runOnUiThread {
                    if (!response.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.getBoolean("success")) {
                                val data = jsonObject.getString("data")
                                val type = object : TypeToken<List<RegistrationRequest_>>() {}.type
                                val requests = Gson().fromJson<List<RegistrationRequest_>>(data, type)
                                requestsAdapter.submitList(requests)
                            } else {
                                showToast("Error: ${jsonObject.optString("error")}")
                            }
                        } catch (e: Exception) {
                            showToast("Error al parsear respuesta")
                            Log.e("AdminActivity", "Error parsing response", e)
                        }
                    } else {
                        showToast("No se pudo obtener las solicitudes")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error de conexión: ${e.message}")
                    Log.e("AdminActivity", "Error loading requests", e)
                }
            }
        }.start()
    }
    private fun approveRequest(requestId: Int) {
        Thread {
            try {
                val response = HttpHelper.postRequest(
                    "registration-requests/$requestId/approve",
                    "{}"
                )

                runOnUiThread {
                    try {
                        // 1. Forzar actualización de la lista independientemente de la respuesta
                        loadPendingRequests()

                        // 2. Manejar respuesta solo si existe
                        if (!response.isNullOrEmpty()) {
                            val json = JSONObject(response)
                            if (json.getBoolean("success")) {
                                showToast("Solicitud aprobada con éxito")
                            } else {
                                showToast("Error: ${json.optString("message", "Error en el servidor")}")
                            }
                        } else {
                            // 3. Mostrar éxito genérico si la respuesta está vacía pero la operación fue exitosa
                            showToast("Solicitud aprobada (actualiza la lista)")
                        }
                    } catch (e: Exception) {
                        // 4. Mostrar éxito aunque falle el parseo
                        showToast("Solicitud procesada - Actualiza la lista")
                        Log.w("APPROVE_WARNING", "Error parseando respuesta: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error de conexión: ${e.message}")
                    Log.e("APPROVE_ERROR", "Error de red", e)
                }
            }
        }.start()
    }

    private fun rejectRequest(requestId: Int) {
        Thread {
            try {
                // Usamos postRequest en lugar de putRequest
                val response = HttpHelper.postRequest(
                    "registration-requests/$requestId/reject",
                    "{}" // Envía un objeto JSON vacío
                )

                runOnUiThread {
                    if (response != null) {
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                showToast("Solicitud rechazada")
                                loadPendingRequests()
                            } else {
                                showToast("Error: ${jsonResponse.optString("message")}")
                            }
                        } catch (e: Exception) {
                            showToast("Error al procesar respuesta")
                            Log.e("AdminActivity", "JSON Error", e)
                        }
                    } else {
                        showToast("Error de conexión (respuesta nula)")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error de red: ${e.message}")
                    Log.e("AdminActivity", "Network Error", e)
                }
            }
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}