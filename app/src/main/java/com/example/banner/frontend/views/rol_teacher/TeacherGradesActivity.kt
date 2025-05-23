package com.example.banner.frontend.views.rol_teacher
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Enrollment_
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R
import com.example.banner.frontend.views.teacher.StudentGradeAdapter
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
    private lateinit var adapter: StudentGradeAdapter
    private lateinit var progressDialog: ProgressDialog

    private var currentGroupId: Int = -1
    private var teacherId: Int = 0
    private var currentGroups: List<Grupo_> = emptyList()
    private var currentEnrollments: MutableList<Enrollment_> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher_grades)

        teacherId = intent.getIntExtra("USER_ID", 0)
        if (teacherId == 0) {
            Toast.makeText(this, "Error: No se identificó al profesor", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        groupsSpinner = findViewById(R.id.groups_spinner)
        studentsRecyclerView = findViewById(R.id.students_recycler_view)
        saveButton = findViewById(R.id.save_grades_button)

        studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentGradeAdapter(emptyList(), currentGroupId) { studentId, groupId, newGrade ->
            // callback por cada cambio
        }
        studentsRecyclerView.adapter = adapter

        groupsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGroup = currentGroups[position]
                currentGroupId = selectedGroup.id
                loadStudentsForGroup(currentGroupId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        saveButton.setOnClickListener { saveGrades() }
        loadTeacherGroups()
    }

    private fun loadTeacherGroups() {
        showProgressDialog("Cargando grupos...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("teachers/$teacherId/groups")
                }

                if (response.isNullOrEmpty()) {
                    showError("No se recibió respuesta del servidor")
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
                            showError("No tiene grupos asignados")
                        }
                    }
                } else {
                    showError(jsonResponse.optString("message", "Error al cargar grupos"))
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
            currentGroups.map { "Grupo ${it.id} : ${it.horario}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupsSpinner.adapter = adapter
    }

    private fun loadStudentsForGroup(groupId: Int) {
        showProgressDialog("Cargando estudiantes...")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpHelper.getRawResponse("groups/$groupId/enrollments")
                }

                if (response.isNullOrEmpty()) {
                    showError("El servidor no devolvió datos")
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
                    showError(jsonResponse.optString("message", "Error desconocido"))
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
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        showProgressDialog("Guardando notas...")

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
                        Toast.makeText(this@TeacherGradesActivity, "Notas guardadas con éxito", Toast.LENGTH_SHORT).show()
                        adapter.clearChanges(currentGroupId)
                        loadStudentsForGroup(currentGroupId)
                    } else {
                        val successCount = results.count { it }
                        showError("Se guardaron $successCount de ${modifiedGrades.size} notas")
                    }
                }
            } catch (e: Exception) {
                dismissProgressDialog()
                showError("Error al guardar notas: ${e.message}")
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