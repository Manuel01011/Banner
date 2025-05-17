package com.example.banner.frontend.views.professor
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backend_banner.backend.Models.Student_
import com.example.backend_banner.backend.Models.Teacher_
import com.example.banner.R
import com.example.banner.frontend.views.teacher.AddTeacher
import com.example.banner.frontend.views.teacher.EditTeacherActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class Teacher : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Teacher_>
    private lateinit var fabAgregarProfesor: FloatingActionButton
    private lateinit var addTeacherLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter3

    //editar
    private lateinit var editProfessorLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teacher)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarProfesor = findViewById(R.id.fabAgregarProfesor)
        // Botón para abrir el menú lateral
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo de las opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->
            Log.d("AdminActivity", "Menu item clicked: ${item.itemId} (${resources.getResourceEntryName(item.itemId)})")

            when (item.itemId) {
                R.id.nav_logout -> {
                    Log.d("ProfessorActivity", "Logout Clicked")
                    finish() // Cierra la actividad actual y vuelve a la anterior
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        addTeacherLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadTeachers() // Recarga los datos del servidor
                Toast.makeText(this, "Teacher agregado", Toast.LENGTH_SHORT).show()
            }
        }
        editProfessorLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("position", -1)
                    val profesorId = data.getIntExtra("id", -1)
                    val profesorName = data.getStringExtra("name") ?: ""
                    val profesorTelNumber = data.getIntExtra("tel",-1)
                    val profesorEmail = data.getStringExtra("email") ?: ""

                    if (position != -1) {
                        fullList[position] = Teacher_(
                            id = profesorId,
                            name = profesorName,
                            telNumber = profesorTelNumber,
                            email = profesorEmail,
                        )
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Techaer updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fabAgregarProfesor.setOnClickListener {
            val intent = Intent(this, AddTeacher::class.java)
            addTeacherLauncher.launch(intent)
        }

        setUpRecyclerView()
        loadTeachers()
        enableSwipeToDeleteAndEdit()
    }

    private fun loadTeachers() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading teachers...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, List<Teacher_>>() {
            override fun doInBackground(vararg params: Void?): List<Teacher_> {
                return try {
                    val response = HttpHelper.getRawResponse("teachers")
                    Log.d("API_TEACHERS", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Teacher_>>() {}.type
                        Gson().fromJson(dataArray.toString(), type)
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading teachers", e)
                    emptyList()
                }
            }

            override fun onPostExecute(result: List<Teacher_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList.clear()
                    fullList.addAll(result)
                    mAdapter.updateData(fullList)
                    Log.d("LOAD_TEACHERS", "Loaded teachers: ${fullList.size}")
                } else {
                    Toast.makeText(this@Teacher, "No teachers found", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    //devuelve la lista de los carreas
    private fun getSuperheros(): MutableList<Teacher_> {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading students...")
            setCancelable(false)
            show()
        }

        val teachers = mutableListOf<Teacher_>()
        try {
            val response = HttpHelper.getRawResponse("teachers")
            if (response != null) {
                val json = JSONObject(response)
                val dataArray = json.getJSONArray("data")
                val type = object : TypeToken<List<Teacher_>>() {}.type
                teachers.addAll(Gson().fromJson<List<Teacher_>>(dataArray.toString(), type))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading teachers", e)
        } finally {
            progressDialog.dismiss()
        }

        return teachers
    }

    //Habilitar Swipe con ItemTouchHelper
    private fun enableSwipeToDeleteAndEdit() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val profesor = mAdapter.getItem(position)

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        editProfessorLauncher.launch(Intent(this@Teacher, EditTeacherActivity::class.java).apply {
                            putExtra("name", profesor.name)
                            putExtra("tel",profesor.telNumber)
                            putExtra("email", profesor.email)
                            putExtra("id", profesor.id)
                        })
                        mAdapter.notifyItemChanged(position)
                    }
                }
            }
            //edicion con estilo bonito
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX > 0) {
                    val itemView = viewHolder.itemView
                    val paint = Paint().apply {
                        color = Color.parseColor("#388E3C") // Verde para editar
                    }

                    val background = RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + dX,
                        itemView.bottom.toFloat()
                    )

                    c.drawRect(background, paint)

                    // Agregar ícono de lápiz (opcional: revisa si tienes este drawable en tu proyecto)
                    val icon = ContextCompat.getDrawable(this@Teacher, R.drawable.ic_edit) // Usa tu ícono aquí
                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + it.intrinsicWidth
                        val iconBottom = iconTop + it.intrinsicHeight

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    //setUpRecyclerView: Inicializa y configura el RecyclerView con un LinearLayoutManager
    private fun setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        fullList = getSuperheros()
        mAdapter = RecyclerAdapter3(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.name.contains(newText, ignoreCase = true) }
                } else {
                    fullList
                }
                mAdapter.updateData(filtered)
                return true
            }
        })
    }

    // Si el usuario presiona atrás y el menú está abierto, se cierra en lugar de salir de la app
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun deleteProfessor(position: Int) {
        val professor = mAdapter.getItem(position)
        Log.d("DELETE_TEACHER", "Attempting to delete teacher with ID: ${professor.id}")

        AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Are you sure you want to delete this teacher?")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    try {
                        val url = "teachers/${professor.id}"
                        Log.d("DELETE_TEACHER", "Request URL: $url")

                        val success = HttpHelper.deleteRequest(url)
                        Log.d("DELETE_TEACHER", "Delete response: $success")

                        runOnUiThread {
                            if (success) {
                                mAdapter.removeItem(position)
                                Toast.makeText(this@Teacher, "Teacher deleted", Toast.LENGTH_SHORT).show()
                                loadTeachers()
                            } else {
                                Toast.makeText(this@Teacher, "Error deleting teacher", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DELETE_TEACHER", "Error deleting teacher", e)
                        runOnUiThread {
                            Toast.makeText(this@Teacher, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun editProfessor(position: Int) {
        val professor = fullList[position]
        val intent = Intent(this, EditTeacherActivity::class.java).apply {
            putExtra("position", position)
            putExtra("id", professor.id)
            putExtra("name", professor.name)
            putExtra("tel", professor.telNumber)
            putExtra("email", professor.email)
        }
        editProfessorLauncher.launch(intent)
    }
}