package com.example.banner.frontend.views.rol_teacher
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.MainActivity
import com.example.banner.R
import com.example.banner.frontend.views.teacher.StudentGradeAdapter
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TeacherGradesActivity : AppCompatActivity() {

    private lateinit var groupsSpinner: Spinner
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var menuButton: ImageButton
    private lateinit var adapter: StudentGradeAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private var currentGroupId: Int = -1
    private var teacherId: Int = 0
    private var currentGroups: List<Grupo_> = emptyList()
    private var currentEnrollments: MutableList<Enrollment_> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_grades)

        // Inicializar vistas
        groupsSpinner = findViewById(R.id.groups_spinner)
        studentsRecyclerView = findViewById(R.id.students_recycler_view)
        saveButton = findViewById(R.id.save_grades_button)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)

        teacherId = intent.getIntExtra("USER_ID", 0)
        if (teacherId == 0) {
            Toast.makeText(this, "Error: Teacher was not identified", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar RecyclerView
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentGradeAdapter(emptyList(), currentGroupId) { studentId, groupId, newGrade ->
            // callback por cada cambio
        }
        studentsRecyclerView.adapter = adapter

        // Configurar Spinner
        groupsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (currentGroups.isNotEmpty()) {
                    val selectedGroup = currentGroups[position]
                    currentGroupId = selectedGroup.id
                    loadStudentsForGroup(currentGroupId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Configurar Navigation Drawer
        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        saveButton.setOnClickListener { saveGrades() }
        loadTeacherGroups()
    }

    private fun loadTeacherGroups() {
        showProgressDialog("Loading groups...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("teachers/$teacherId/groups")
                }

                if (response.isNullOrEmpty()) {
                    showError("No response was received from the server")
                    return@launch
                }

                val jsonResponse = JSONObject(response)
                if (jsonResponse.optString("status") == "success") {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val type = object : TypeToken<List<Grupo_>>() {}.type
                    currentGroups = Gson().fromJson(dataArray.toString(), type)

                    withContext(Dispatchers.Main) {
                        if (currentGroups.isNotEmpty()) {
                            setupGroupsSpinner()
                        } else {
                            showError("No groups assigned")
                        }
                    }
                } else {
                    showError(jsonResponse.optString("message", "Error loading groups"))
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                dismissProgressDialog()
            }
        }
    }

    private fun setupGroupsSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currentGroups.map { "Group ${it.id} : ${it.horario}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupsSpinner.adapter = adapter
    }

    private fun loadStudentsForGroup(groupId: Int) {
        showProgressDialog("Loading students...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("groups/$groupId/enrollments")
                }

                if (response.isNullOrEmpty()) {
                    showError("Server did not return data")
                    return@launch
                }

                val jsonResponse = JSONObject(response)
                if (jsonResponse.optString("status") == "success") {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val type = object : TypeToken<List<Enrollment_>>() {}.type
                    currentEnrollments = Gson().fromJson<List<Enrollment_>>(dataArray.toString(), type).toMutableList()

                    withContext(Dispatchers.Main) {
                        val filtered = currentEnrollments.filter { it.grupoId == groupId }
                        adapter.updateData(filtered, groupId)
                    }
                } else {
                    showError(jsonResponse.optString("message", "Unkown Error"))
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                dismissProgressDialog()
            }
        }
    }

    private fun saveGrades() {
        val modifiedGrades = adapter.getModifiedGrades()
            .filterKeys { it.second == currentGroupId }

        if (modifiedGrades.isEmpty()) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
            return
        }

        showProgressDialog("Saving grades...")

        lifecycleScope.launch {
            try {
                val results = modifiedGrades.map { (key, grade) ->
                    val (studentId, groupId) = key
                    val enrollment = Enrollment_(studentId = studentId, grupoId = groupId, grade = grade)
                    try {
                        val response = withContext(Dispatchers.IO) {
                            HttpHelper.sendRequest("enrollments", "PUT", enrollment, String::class.java)
                        }
                        response != null && JSONObject(response).optBoolean("success", false)
                    } catch (e: Exception) {
                        false
                    }
                }

                val allSuccess = results.all { it }

                withContext(Dispatchers.Main) {
                    dismissProgressDialog()
                    if (allSuccess) {
                        Toast.makeText(this@TeacherGradesActivity, "Successfully grades notes", Toast.LENGTH_SHORT).show()
                        adapter.clearChanges(currentGroupId)
                        loadStudentsForGroup(currentGroupId)
                    } else {
                        val successCount = results.count { it }
                        showError("They were kept $successCount de ${modifiedGrades.size} grades")
                    }
                }
            } catch (e: Exception) {
                dismissProgressDialog()
                showError("Error saving grades: ${e.message}")
            }
        }
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun dismissProgressDialog() {
        if (!isFinishing && !isDestroyed) {
            runOnUiThread {
                if (::progressDialog.isInitialized && progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}