package com.example.banner
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

class EditCicloActivity : AppCompatActivity() {
    private lateinit var editYear: EditText
    private lateinit var editNumber: EditText
    private lateinit var editStartDate: EditText
    private lateinit var editFinishDate: EditText
    private lateinit var checkIsActive: CheckBox
    private lateinit var btnSave: Button

    private var id: Int = -1

    private val cicloList: MutableList<Ciclo_> = mutableListOf() // Asegúrate de inicializar esta lista correctamente


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editciclo)

        // Vinculamos las vistas
        editYear = findViewById(R.id.edit_year)
        editNumber = findViewById(R.id.edit_number)
        editStartDate = findViewById(R.id.edit_start_date)
        editFinishDate = findViewById(R.id.edit_finish_date)
        checkIsActive = findViewById(R.id.checkbox_active)
        btnSave = findViewById(R.id.btn_save)

        // Obtener datos del intent
        id = intent.getIntExtra("id", -1)
        editYear.setText(intent.getIntExtra("year", 0).toString())
        editNumber.setText(intent.getIntExtra("number", 0).toString())
        editStartDate.setText(intent.getStringExtra("dateStart"))
        editFinishDate.setText(intent.getStringExtra("dateFinish"))
        checkIsActive.isChecked = intent.getBooleanExtra("is_active", false)

        // Listener para el selector de fecha de inicio
        editStartDate.setOnClickListener {
            showDatePickerDialog(editStartDate)
        }

        // Listener para el selector de fecha de finalización
        editFinishDate.setOnClickListener {
            showDatePickerDialog(editFinishDate)
        }

        btnSave.setOnClickListener {
            val resultIntent = Intent().apply {
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

        // Listener para el CheckBox de estado activo
        checkIsActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Desactivar otros ciclos
                // para asegurarte de que solo un ciclo esté marcado como activo
                Toast.makeText(this, "Ciclo activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Muestra el DatePickerDialog
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formattedDate = "$year-${month + 1}-$dayOfMonth"
                editText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
