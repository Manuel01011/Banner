package com.example.banner.frontend.views.group

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddGroup : AppCompatActivity() {
    private lateinit var grupoId: EditText
    private lateinit var grupoNumber: EditText
    private lateinit var grupoYear: EditText
    private lateinit var grupoHorario: EditText
    private lateinit var grupoCourseCod: EditText
    private lateinit var grupoTeacherId: EditText
    private lateinit var saveGrupoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_group)

        //se inicializan los campos de texto
        grupoId = findViewById(R.id.agregar_grupo_id)
        grupoNumber = findViewById(R.id.agregar_grupo_number)
        grupoYear = findViewById(R.id.agregar_grupo_year)
        grupoHorario = findViewById(R.id.agregar_grupo_horario)
        grupoCourseCod = findViewById(R.id.agregar_grupo_courseCod)
        grupoTeacherId = findViewById(R.id.agregar_grupo_teacherId)
        saveGrupoBtn = findViewById(R.id.btn_guardar_grupo)

        saveGrupoBtn.setOnClickListener {
            try {
                val id = grupoId.text.toString().toInt()
                val number = grupoNumber.text.toString().toInt()
                val year = grupoYear.text.toString().toInt()
                val horario = grupoHorario.text.toString()
                val courseCod = grupoCourseCod.text.toString().toInt()
                val teacherId = grupoTeacherId.text.toString().toInt()

                if (horario.isEmpty()) {
                    grupoHorario.error = "Please enter a valid schedule"
                    return@setOnClickListener
                }

                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Creating group...")
                    setCancelable(false)
                    show()
                }

                val newGroup = Grupo_(
                    id = id,
                    numberGroup = number,
                    year = year,
                    horario = horario,
                    courseCod = courseCod,
                    teacherId = teacherId
                )

                lifecycleScope.launch {
                    try {
                        val success = withContext(Dispatchers.IO) {
                            val response = HttpHelper.sendRequest(
                                "groups",
                                "POST",
                                newGroup,
                                String::class.java
                            )

                            response?.let {
                                val jsonResponse = JSONObject(it)
                                jsonResponse.optBoolean("success", false) &&
                                        jsonResponse.optString("message") == "Group successfully created"
                            } ?: false
                        }

                        progressDialog.dismiss()
                        if (success) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddGroup,
                                "Error creating group. Verify the data.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        Log.e("API_ERROR", "Error creating group", e)
                        Toast.makeText(
                            this@AddGroup,
                            "Error: ${e.message ?: "Error desconocido"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun returnResult(group: Grupo_) {
        val resultIntent = Intent().apply {
            putExtra("groupId", group.id)
            putExtra("groupNumber", group.numberGroup)
            putExtra("groupYear", group.year)
            putExtra("groupHorario", group.horario)
            putExtra("groupCourseCode", group.courseCod)
            putExtra("groupTeacherId", group.teacherId)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}
