package com.example.banner.frontend.views.user
import Usuario_
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

class User : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var fullList: MutableList<Usuario_>
    private lateinit var fabAgregarUsuario: FloatingActionButton
    private lateinit var addUserLauncher: ActivityResultLauncher<Intent>
    // Recyclerview de Teachers
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerAdapter8

    //edit
    private lateinit var editUserLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront()
        navigationView.requestFocus()
        fabAgregarUsuario = findViewById(R.id.fabAgregarUser)
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
        addUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Obtener los valores de los campos de la nueva actividad
                    val usuarioId = data.getIntExtra("usuarioId", -1)
                    val usuarioPassword = data.getStringExtra("usuarioPassword") ?: ""
                    val usuarioRole = data.getStringExtra("usuarioRole") ?: ""
                    // Crear un nuevo objeto Estudiante con los datos recibidos
                    val newUser = Usuario_(usuarioId, usuarioPassword, usuarioRole)

                    // Agregar el nuevo estudiante a la lista
                    fullList.add(newUser)
                    mAdapter.updateData(fullList)
                    Toast.makeText(this, "Usuario agregado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //edit
        editUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val index = data.getIntExtra("userIndex", -1)
                    val usuarioId = data.getIntExtra("usuarioId", -1)
                    val usuarioPassword = data.getStringExtra("usuarioPassword") ?: ""
                    val usuarioRole = data.getStringExtra("usuarioRole") ?: ""

                    if (index != -1) {
                        val editedUser = Usuario_(usuarioId, usuarioPassword, usuarioRole)
                        fullList[index] = editedUser
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "Usuario editado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        fabAgregarUsuario.setOnClickListener {
            // Iniciar la actividad para agregar un curso
            val intent = Intent(this, AddUser::class.java)
            addUserLauncher.launch(intent)
        }

        setUpRecyclerView()
        Log.d("CareerActivity", "Después de setUpRecyclerView")
        enableSwipeToDeleteAndEdit()
    }

    //devuelve la lista de los carreas
    private fun getUsuers(): MutableList<Usuario_> {
        return mutableListOf(
            Usuario_(1,"pass","Student"),
            Usuario_(2,"pass","Teacher"),
            Usuario_(3,"pass","Admi"),

        )
    }
    //Habilitar Swipe con ItemTouchHelper
    private fun enableSwipeToDeleteAndEdit() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
                        // Editar usuario
                        val intent = Intent(this@User, EditUser::class.java).apply {
                            putExtra("usuarioId", student.id)
                            putExtra("usuarioPassword", student.password)
                            putExtra("usuarioRole", student.role)
                            putExtra("userIndex", position)
                        }
                        editUserLauncher.launch(intent)
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
                    val icon = ContextCompat.getDrawable(this@User, R.drawable.ic_edit) // Usa tu ícono aquí
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

        fullList = getUsuers()
        mAdapter = RecyclerAdapter8(fullList.toMutableList(), this)
        mRecyclerView.adapter = mAdapter

        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter { it.role.contains(newText, ignoreCase = true) }
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

    fun deleteUser(position: Int) {
        val user = mAdapter.getItem(position)
        mAdapter.removeItem(position)
        Toast.makeText(this, "Usuario eliminada: ${user.id}", Toast.LENGTH_SHORT).show()
    }
}