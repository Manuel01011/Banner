package com.example.banner.frontend.views.cicle
import java.util.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import com.example.backend_banner.backend.Models.Ciclo_
import com.example.banner.R
import android.os.AsyncTask

class EditSemesterActivity : AppCompatActivity() {
    private lateinit var editYear: EditText
    private lateinit var editNumber: EditText
    private lateinit var editStartDate: EditText
    private lateinit var editFinishDate: EditText
    private lateinit var checkIsActive: CheckBox
    private lateinit var btnSave: Button

    private var id: Int = -1
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_semester)

        initViews()
        loadIntentData()
        setupDatePickers()
        setupSaveButton()
        setupActiveCheckbox()
    }

    private fun initViews() {
        editYear = findViewById(R.id.edit_year)
        editNumber = findViewById(R.id.edit_number)
        editStartDate = findViewById(R.id.edit_start_date)
        editFinishDate = findViewById(R.id.edit_finish_date)
        checkIsActive = findViewById(R.id.checkbox_active)
        btnSave = findViewById(R.id.btn_save)
    }

    private fun loadIntentData() {
        id = intent.getIntExtra("id", -1)
        position = intent.getIntExtra("position", -1)
        editYear.setText(intent.getIntExtra("year", 0).toString())
        editNumber.setText(intent.getIntExtra("number", 0).toString())
        editStartDate.setText(intent.getStringExtra("dateStart"))
        editFinishDate.setText(intent.getStringExtra("dateFinish"))
        checkIsActive.isChecked = intent.getBooleanExtra("is_active", false)
    }

    private fun setupDatePickers() {
        editStartDate.setOnClickListener { showDatePickerDialog(editStartDate) }
        editFinishDate.setOnClickListener { showDatePickerDialog(editFinishDate) }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            if (validateFields()) {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating semester...")
                    setCancelable(false)
                    show()
                }

                val updatedCiclo = Ciclo_(
                    id = id,
                    year = editYear.text.toString().toInt(),
                    number = editNumber.text.toString().toInt(),
                    dateStart = editStartDate.text.toString(),
                    dateFinish = editFinishDate.text.toString(),
                    is_active = checkIsActive.isChecked
                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "ciclos",
                                "PUT",
                                updatedCiclo,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error updating ciclo", e)
                            false
                        }
                    }

                    override fun onPostExecute(success: Boolean) {
                        progressDialog.dismiss()
                        if (success) {
                            val resultIntent = Intent().apply {
                                putExtra("position", position)
                                putExtra("id", id)
                                putExtra("year", updatedCiclo.year)
                                putExtra("number", updatedCiclo.number)
                                putExtra("dateStart", updatedCiclo.dateStart)
                                putExtra("dateFinish", updatedCiclo.dateFinish)
                                putExtra("is_active", updatedCiclo.is_active)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                            Toast.makeText(this@EditSemesterActivity, "Semester updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this@EditSemesterActivity,
                                "Error updating the semester",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.execute()
            }
        }
    }

    private fun setupActiveCheckbox() {
        checkIsActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Semester activated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            editYear.text.isNullOrEmpty() -> {
                editYear.error = "Enter the year"
                false
            }
            editYear.text.toString().toIntOrNull() == null -> {
                editYear.error = "Year invalid"
                false
            }
            editNumber.text.isNullOrEmpty() -> {
                editNumber.error = "Enter the semester number"
                false
            }
            editNumber.text.toString().toIntOrNull() == null -> {
                editNumber.error = "Semester number invalid"
                false
            }
            editStartDate.text.isNullOrEmpty() -> {
                editStartDate.error = "Select start date"
                false
            }
            editFinishDate.text.isNullOrEmpty() -> {
                editFinishDate.error = "Select end date"
                false
            }
            else -> true
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(formattedDate)
                editText.error = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
