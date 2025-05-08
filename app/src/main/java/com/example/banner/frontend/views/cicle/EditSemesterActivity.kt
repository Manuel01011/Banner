package com.example.banner.frontend.views.cicle
import Ciclo_
import java.util.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.widget.Toast
import com.example.banner.R

class EditSemesterActivity : AppCompatActivity() {
    private lateinit var editYear: EditText
    private lateinit var editNumber: EditText
    private lateinit var editStartDate: EditText
    private lateinit var editFinishDate: EditText
    private lateinit var checkIsActive: CheckBox
    private lateinit var btnSave: Button

    private var id: Int = -1
    private var position: Int = -1  // Added to track position in the list

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
        position = intent.getIntExtra("position", -1)  // Get position from intent
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
                val resultIntent = Intent().apply {
                    putExtra("position", position)  // Include position in result
                    putExtra("id", id)
                    putExtra("year", editYear.text.toString().toInt())
                    putExtra("number", editNumber.text.toString().toInt())
                    putExtra("dateStart", editStartDate.text.toString())
                    putExtra("dateFinish", editFinishDate.text.toString())
                    putExtra("is_active", checkIsActive.isChecked)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun setupActiveCheckbox() {
        checkIsActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Semester activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            editYear.text.isNullOrEmpty() -> {
                editYear.error = "Ingrese el año"
                false
            }
            editYear.text.toString().toIntOrNull() == null -> {
                editYear.error = "Año inválido"
                false
            }
            editNumber.text.isNullOrEmpty() -> {
                editNumber.error = "Ingrese el número de semestre"
                false
            }
            editNumber.text.toString().toIntOrNull() == null -> {
                editNumber.error = "Número de semestre inválido"
                false
            }
            editStartDate.text.isNullOrEmpty() -> {
                editStartDate.error = "Seleccione fecha de inicio"
                false
            }
            editFinishDate.text.isNullOrEmpty() -> {
                editFinishDate.error = "Seleccione fecha de fin"
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
