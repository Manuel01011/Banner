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
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R

class EditGroupActivity : AppCompatActivity() {
    private lateinit var etId: EditText
    private lateinit var etNumber: EditText
    private lateinit var etYear: EditText
    private lateinit var etHorario: EditText
    private lateinit var etCourseCode: EditText
    private lateinit var etTeacherId: EditText
    private lateinit var btnGuardar: Button

    private var position: Int = -1
    private var originalId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_group)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        etId = findViewById(R.id.etId)
        etNumber = findViewById(R.id.etNumber)
        etYear = findViewById(R.id.etYear)
        etHorario = findViewById(R.id.etHorario)
        etCourseCode = findViewById(R.id.etCourseCode)
        etTeacherId = findViewById(R.id.etTeacherId)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        originalId = intent.getIntExtra("id", -1)

        etId.setText(originalId.toString())
        etNumber.setText(intent.getIntExtra("number", -1).toString())
        etYear.setText(intent.getIntExtra("year", -1).toString())
        etHorario.setText(intent.getStringExtra("horario"))
        etCourseCode.setText(intent.getIntExtra("courseCode", -1).toString())
        etTeacherId.setText(intent.getIntExtra("teacherId", -1).toString())

        // ID no se puede editar ya que es la clave primaria
        etId.isEnabled = false
    }

    private fun setupSaveButton() {
        btnGuardar.setOnClickListener {
            if (validateFields()) {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating group...")
                    setCancelable(false)
                    show()
                }

                val updatedGroup = Grupo_(
                    id = originalId,
                    numberGroup = etNumber.text.toString().toInt(),
                    year = etYear.text.toString().toInt(),
                    horario = etHorario.text.toString(),
                    courseCod = etCourseCode.text.toString().toInt(),
                    teacherId = etTeacherId.text.toString().toInt()
                )

                Thread {
                    try {
                        // Envía el objeto directamente en lugar de convertirlo a String primero
                        val success = HttpHelper.sendRequest(
                            "groups",
                            "PUT",
                            updatedGroup,  // Envía el objeto directamente
                            String::class.java
                        )?.contains("\"success\":true") == true

                        runOnUiThread {
                            progressDialog.dismiss()
                            if (success) {
                                val resultIntent = Intent().apply {
                                    putExtra("position", position)
                                    putExtra("id", updatedGroup.id)
                                    putExtra("number", updatedGroup.numberGroup)
                                    putExtra("year", updatedGroup.year)
                                    putExtra("horario", updatedGroup.horario)
                                    putExtra("courseCode", updatedGroup.courseCod)
                                    putExtra("teacherId", updatedGroup.teacherId)
                                }
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@EditGroupActivity,
                                    "Error updating the group",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this@EditGroupActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("EDIT_GROUP", "Error updating group", e)
                        }
                    }
                }.start()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            etId.text.isNullOrEmpty() -> {
                etId.error = "Enter Group ID"
                false
            }
            etNumber.text.isNullOrEmpty() -> {
                etNumber.error = "Enter group number"
                false
            }
            etYear.text.isNullOrEmpty() -> {
                etYear.error = "Enter the year"
                false
            }
            etHorario.text.isNullOrEmpty() -> {
                etHorario.error = "Enter the schedule"
                false
            }
            etCourseCode.text.isNullOrEmpty() -> {
                etCourseCode.error = "Enter course code"
                false
            }
            etTeacherId.text.isNullOrEmpty() -> {
                etTeacherId.error = "Enter teacher ID"
                false
            }
            else -> true
        }
    }
}