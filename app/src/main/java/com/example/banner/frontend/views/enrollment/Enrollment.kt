package com.example.banner.frontend.views.enrollment
import Enrollment_
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

class Enrollment : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Enrollment_>
    private lateinit var fabAgregarMatricula: FloatingActionButton
    private lateinit var addEnrollmentLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de carreras
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter7
    //edit
    private lateinit var editEnrollmentLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enrrollment)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarMatricula = findViewById(R.id.fabAgregarMatricula)
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
        // Registra el ActivityResultLauncher para recibir el resultado de la actividad
        addEnrollmentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val studentId = data.getIntExtra("studentId", -1)
                    val grupoId = data.getIntExtra("grupoId", -1)
                    val grade = data.getDoubleExtra("grade", 0.0)
                    // Crear un nuevo objeto Matrícula con los datos recibidos
                    val newEnrollment = Enrollment_(studentId, grupoId, grade)
                    // Agregar la nueva matrícula a la lista
                    fullList.add(newEnrollment)
                    // Actualizar el adaptador con los nuevos datos
                    mAdapter.updateData(fullList)
                    // Mostrar mensaje confirmando la acción
                    Toast.makeText(this, "Matrícula agregada correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //edit matricula
        editEnrollmentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val studentId = data.getIntExtra("studentId", -1)
                    val grupoId = data.getIntExtra("grupoId", -1)
                    val grade = data.getDoubleExtra("grade", 0.0)
                    val position = data.getIntExtra("position", -1)

                    if (position != -1) {
                        fullList[position] = Enrollment_(studentId, grupoId, grade)
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Matrícula actualizada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fabAgregarMatricula.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddEnrollment::class.java)
            addEnrollmentLauncher.launch(intent)
        }

        Log.d("CareerActivity", "Antes de setUpRecyclerView")
        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")
        enableSwipeToDeleteAndEdit()
    }

    //devuelve la lista de los carreas
    private fun getMatriculas(): MutableList<Enrollment_> {
        return mutableListOf(
           Enrollment_(1,2,8.5),
            Enrollment_(2,2,7.5),
            Enrollment_(3,2,7.6),
        )
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
                val matricula = mAdapter.getItem(position)

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        val intent = Intent(this@Enrollment, EditEnrollmentActivity::class.java)
                        intent.putExtra("studentId", matricula.studentId)
                        intent.putExtra("grupoId", matricula.grupoId)
                        intent.putExtra("grade", matricula.grade)
                        intent.putExtra("position", position)
                        editEnrollmentLauncher.launch(intent)
                        mAdapter.restoreItem(matricula, position)
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
                    val icon = ContextCompat.getDrawable(this@Enrollment, R.drawable.ic_edit) // Usa tu ícono aquí
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

        fullList = getMatriculas()
        mAdapter = RecyclerAdapter7(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.studentId.toString().contains(newText, ignoreCase = true) }
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

    fun deleteEnrollment(position: Int) {
        val matriclula = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Matricula eliminada: ${matriclula.studentId}", Toast.LENGTH_SHORT).show()
    }
}