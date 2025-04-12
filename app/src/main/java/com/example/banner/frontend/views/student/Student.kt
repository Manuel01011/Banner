package com.example.banner.frontend.views.student
import Student_
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class Student : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Student_>
    private lateinit var fabAgregarEstudiante: FloatingActionButton
    private lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter5

    //editar
    private lateinit var editStudentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarEstudiante = findViewById(R.id.fabAgregarEstudiante)

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
                    finish()
                }
                else -> Log.d("AdminActivity", "Unknown menu item: ${item.itemId}")
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //editar
        editStudentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val studentId = data.getIntExtra("id", -1)
                    val name = data.getStringExtra("name") ?: ""
                    val telNumber = data.getIntExtra("telNumber", 0)
                    val email = data.getStringExtra("email") ?: ""
                    val bornDate = data.getStringExtra("bornDate") ?: ""
                    val careerCod = data.getIntExtra("careerCod", 0)

                    val index = fullList.indexOfFirst { it.id == studentId }
                    if (index != -1) {
                        fullList[index].apply {
                            this.name = name
                            this.telNumber = telNumber
                            this.email = email
                            this.bornDate = bornDate
                            this.careerCod = careerCod
                        }
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Estudiante actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        addStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val studentId = data.getIntExtra("studentId", -1)
                    val studentName = data.getStringExtra("studentName") ?: ""
                    val studentTelNumber = data.getIntExtra("studentTelNumber",-1)
                    val studentEmail = data.getStringExtra("studentEmail") ?: ""
                    val studentBornDate = data.getStringExtra("studentBornDate") ?: ""
                    val studentCareerCode = data.getIntExtra("studentCareerCode", -1)

                    // Crear un nuevo objeto Estudiante con los datos recibidos
                    val newStudent = Student_(studentId, studentName, studentTelNumber, studentEmail, studentBornDate, studentCareerCode)

                    // Agregar el nuevo estudiante a la lista
                    fullList.add(newStudent)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Estudiante agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        fabAgregarEstudiante.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AgregarEstudiante::class.java)
            addStudentLauncher.launch(intent)
        }

        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")
        enableSwipeToDeleteAndEdit()
    }

    //devuelve la lista de los carreas
    private fun getStudents(): MutableList<Student_> {
        return mutableListOf(
          Student_(1,"Manuel Mora",62567524,"Manuel@gmail.com","09/13/2004",1),
            Student_(2,"Victor Quesada",82738459,"Victor@gmail.com","02/3/2000",1),
            Student_(3,"Pablo Alvarez",384912303,"Pablo@gmail.com","04/1/1999",1)

        )
    }

    //Habilitar Swipe con ItemTouchHelper
    private fun enableSwipeToDeleteAndEdit() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val student = mAdapter.getItem(position)

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        // Lanzar la actividad de edición
                        editStudentLauncher.launch(Intent(this@Student, EditStudentActivity::class.java).apply  {
                            putExtra("id", student.id)
                            putExtra("name", student.name)
                            putExtra("telNumber", student.telNumber)
                            putExtra("email", student.email)
                            putExtra("bornDate", student.bornDate)
                            putExtra("careerCod", student.careerCod)
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
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

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
                    val icon = ContextCompat.getDrawable(
                        this@Student,
                        R.drawable.ic_edit
                    ) // Usa tu ícono aquí
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

        fullList = getStudents()
        mAdapter = RecyclerAdapter5(fullList.toMutableList(), this)
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

    fun deleteStudent(position: Int) {
        val student = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Estudiante eliminada: ${student.id}", Toast.LENGTH_SHORT).show()
    }
}