package com.example.banner.frontend.views.group
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
import com.example.backend_banner.backend.Models.Grupo_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class Group : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Grupo_>
    private lateinit var fabAgregarGrupo: FloatingActionButton
    private lateinit var addGroupLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter6

    //edit
    private lateinit var editGroupLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarGrupo = findViewById(R.id.fabAgregarGrupo)
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
        addGroupLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val groupId = data.getIntExtra("groupId", -1)
                    val groupNumber = data.getIntExtra("groupNumber", -1)
                    val groupYear = data.getIntExtra("groupYear", -1)
                    val groupHorario = data.getStringExtra("groupHorario") ?: ""
                    val groupCourseCode = data.getIntExtra("groupCourseCode", -1)
                    val groupTeacherId = data.getIntExtra("groupTeacherId", -1)

                    // Crear un nuevo objeto Grupo con los datos recibidos
                    val newGroup = Grupo_(groupId, groupNumber, groupYear, groupHorario, groupCourseCode, groupTeacherId)

                    // Agregar el nuevo estudiante a la lista
                    fullList.add(newGroup)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Grupo agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //edit
        editGroupLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val id = data.getIntExtra("id", -1)
                    val number = data.getIntExtra("number", -1)
                    val year = data.getIntExtra("year", -1)
                    val horario = data.getStringExtra("horario") ?: ""
                    val courseCode = data.getIntExtra("courseCode", -1)
                    val teacherId = data.getIntExtra("teacherId", -1)
                    val position = data.getIntExtra("position", -1)

                    val updatedGroup = Grupo_(id, number, year, horario, courseCode, teacherId)
                    if (position >= 0) {
                        fullList[position] = updatedGroup
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Grupo actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fabAgregarGrupo.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddGroup::class.java)
            addGroupLauncher.launch(intent)
        }

        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")
        enableSwipeToDeleteAndEdit()
    }

    //devuelve la lista de los carreas
    private fun getGrupos(): MutableList<Grupo_> {
        return mutableListOf(
            Grupo_(1,10,2025,"Lun-Jue 10:00-11:40",1,1),
            Grupo_(2,20,2025,"Mierc 8:00-11:20",2,3),
            Grupo_(3,12,2025,"Mar-Vie 1:00-2:40",1,4),

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
                val curso = mAdapter.getItem(position)

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        val intent = Intent(this@Group, EditGroupActivity::class.java).apply {
                            putExtra("id", curso.id)
                            putExtra("number", curso.numberGroup)
                            putExtra("year", curso.year)
                            putExtra("horario", curso.horario)
                            putExtra("courseCode", curso.courseCod)
                            putExtra("teacherId", curso.teacherId)
                            putExtra("position", position)
                        }
                        editGroupLauncher.launch(intent)
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
                    val icon = ContextCompat.getDrawable(this@Group, R.drawable.ic_edit) // Usa tu ícono aquí
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

        fullList = getGrupos()
        mAdapter = RecyclerAdapter6(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.horario.contains(newText, ignoreCase = true) }
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

    fun deleteGrupo(position: Int) {
        val grupo = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Grupo eliminada: ${grupo.id}", Toast.LENGTH_SHORT).show()
    }
}