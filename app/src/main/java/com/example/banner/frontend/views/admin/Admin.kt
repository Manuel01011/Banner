package com.example.banner.frontend.views.admin
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.banner.MainActivity
import com.example.banner.R
import com.example.banner.frontend.views.career.Career
import com.example.banner.frontend.views.cicle.Ciclo
import com.example.banner.frontend.views.course.Course
import com.example.banner.frontend.views.enrollment.Enrollment
import com.example.banner.frontend.views.group.Group
import com.example.banner.frontend.views.professor.Professor
import com.example.banner.frontend.views.student.Student
import com.example.banner.frontend.views.user.User
import com.google.android.material.navigation.NavigationView

class Admin : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var textViewWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin)


        // Referencias a los elementos del layout
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        menuButton = findViewById(R.id.menu_button)
        textViewWelcome = findViewById(R.id.textViewWelcome)
        navigationView.bringToFront()
        navigationView.requestFocus()

        // Mostrar nombre del usuario
        val usuario = intent.getStringExtra("USERNAME")
        textViewWelcome.text = "Welcome $usuario"

        // Botón para abrir el menú lateral
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo de las opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->
            Log.d("AdminActivity", "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})")

            when (item.itemId) {
                R.id.nav_students -> {
                    Log.d("AdminActivity", "Students Clicked")
                    startActivity(Intent(this, Student::class.java))
                }
                R.id.nav_professors -> {
                    Log.d("AdminActivity", "Professors Clicked")
                    startActivity(Intent(this, Professor::class.java))
                }
                R.id.nav_courses -> {
                    Log.d("AdminActivity", "Courses Clicked")
                    startActivity(Intent(this, Course::class.java))
                }
                R.id.nav_carrer -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, Career::class.java))
                }
                R.id.nav_Ciclo -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, Ciclo::class.java))
                }
                R.id.nav_enrrollment -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, Enrollment::class.java))
                }
                R.id.nav_group -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, Group::class.java))
                }
                R.id.nav_user -> {
                    Log.d("AdminActivity", "Logout Clicked")
                    startActivity(Intent(this, User::class.java))
                }
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

    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}