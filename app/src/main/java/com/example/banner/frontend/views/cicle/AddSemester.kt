package com.example.banner.frontend.views.cicle

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Ciclo_
import com.example.banner.R

class AddSemester : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var year: EditText
    private lateinit var number: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var isActive: CheckBox
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_semester)

        id = findViewById(R.id.agregar_id)
        year = findViewById(R.id.agregar_year)
        number = findViewById(R.id.agregar_number)
        startDate = findViewById(R.id.agregar_fecha_inicio)
        endDate = findViewById(R.id.agregar_fecha_fin)
        isActive = findViewById(R.id.agregar_activo)
        saveBtn = findViewById(R.id.btn_guardar_ciclo)

        saveBtn.setOnClickListener {
            val cycleId = id.text.toString().toIntOrNull()
            val cycleYear = year.text.toString().toIntOrNull()
            val cycleNumber = number.text.toString().toIntOrNull()
            val cycleStartDate = startDate.text.toString()
            val cycleEndDate = endDate.text.toString()

            if (cycleId != null && cycleYear != null && cycleNumber != null &&
                cycleStartDate.isNotBlank() && cycleEndDate.isNotBlank()) {

                val newCiclo = Ciclo_(
                    id = cycleId,
                    year = cycleYear,
                    number = cycleNumber,
                    dateStart = cycleStartDate,
                    dateFinish = cycleEndDate,
                    is_active = isActive.isChecked
                )
                InsertCicloTask().execute(newCiclo)
            } else {
                Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private inner class InsertCicloTask : AsyncTask<Ciclo_, Void, Boolean>() {
        private val progressDialog = ProgressDialog(this@AddSemester).apply {
            setMessage("Saving cycle...")
            setCancelable(false)
        }

        override fun onPreExecute() {
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Ciclo_): Boolean {
            return try {
                val response = HttpHelper.sendRequest(
                    "ciclos",
                    "POST",
                    params[0],
                    String::class.java
                )
                response?.contains("\"success\":true") ?: false
            } catch (e: Exception) {
                false
            }
        }

        override fun onPostExecute(success: Boolean) {
            progressDialog.dismiss()

            if (success) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(
                    this@AddSemester,
                    "Error saving the cycle",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
