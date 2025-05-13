package com.example.banner.frontend.views.course
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backend_banner.backend.Models.Course_
import com.example.banner.R

class EditCourseActivity : AppCompatActivity() {
    private lateinit var edtCod: EditText
    private lateinit var edtName: EditText
    private lateinit var edtCredits: EditText
    private lateinit var edtHours: EditText
    private lateinit var edtCicloId: EditText
    private lateinit var edtCareerCod: EditText
    private lateinit var saveButton: Button

    private var position: Int = -1  // Added to track position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_course)

        initViews()
        loadIntentData()
        setupSaveButton()
    }

    private fun initViews() {
        edtCod = findViewById(R.id.edtCod)
        edtName = findViewById(R.id.edtName)
        edtCredits = findViewById(R.id.edtCredits)
        edtHours = findViewById(R.id.edtHours)
        edtCicloId = findViewById(R.id.edtCicloId)
        edtCareerCod = findViewById(R.id.edtCareerCod)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun loadIntentData() {
        position = intent.getIntExtra("position", -1)
        edtCod.setText(intent.getIntExtra("cod", 0).toString())
        edtName.setText(intent.getStringExtra("name"))
        edtCredits.setText(intent.getIntExtra("credits", 0).toString())
        edtHours.setText(intent.getIntExtra("hours", 0).toString())
        edtCicloId.setText(intent.getIntExtra("cicloId", 0).toString())
        edtCareerCod.setText(intent.getIntExtra("careerCod", 0).toString())
    }


    private fun validateFields(): Boolean {
        return when {
            edtCod.text.isNullOrEmpty() -> {
                edtCod.error = "Enter a code"
                false
            }
            edtName.text.isNullOrEmpty() -> {
                edtName.error = "Enter a name"
                false
            }
            edtCredits.text.isNullOrEmpty() -> {
                edtCredits.error = "Enter credits"
                false
            }
            edtHours.text.isNullOrEmpty() -> {
                edtHours.error = "Enter the hours"
                false
            }
            edtCicloId.text.isNullOrEmpty() -> {
                edtCicloId.error = "Enter the cycle ID"
                false
            }
            edtCareerCod.text.isNullOrEmpty() -> {
                edtCareerCod.error = "Enter the career code"
                false
            }
            else -> true
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateFields()) {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Updating course...")
                    setCancelable(false)
                    show()
                }

                val updatedCourse = Course_(
                    edtCod.text.toString().toInt(),
                    edtName.text.toString(),
                    edtCredits.text.toString().toInt(),
                    edtHours.text.toString().toInt(),
                    edtCicloId.text.toString().toInt(),
                    edtCareerCod.text.toString().toInt()
                )

                object : AsyncTask<Void, Void, Boolean>() {
                    override fun doInBackground(vararg params: Void?): Boolean {
                        return try {
                            val response = HttpHelper.sendRequest(
                                "courses",
                                "PUT",
                                updatedCourse,
                                String::class.java
                            )
                            response != null && response.contains("\"success\":true")
                        } catch (e: Exception) {
                            Log.e("API_ERROR", "Error updating course", e)
                            false
                        }
                    }

                    override fun onPostExecute(success: Boolean) {
                        progressDialog.dismiss()
                        if (success) {
                            val resultIntent = Intent().apply {
                                putExtra("position", position)
                                putExtra("cod", updatedCourse.cod)
                                putExtra("name", updatedCourse.name)
                                putExtra("credits", updatedCourse.credits)
                                putExtra("hours", updatedCourse.hours)
                                putExtra("cicloId", updatedCourse.cicloId)
                                putExtra("careerCod", updatedCourse.careerCod)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@EditCourseActivity,
                                "Error updating course",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.execute()
            }
        }
    }
}