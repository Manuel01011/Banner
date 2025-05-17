package com.example.banner.frontend.views.user
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
import com.example.backend_banner.backend.Models.Teacher_
import com.example.backend_banner.backend.Models.Usuario_
import com.example.banner.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

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

        addUserLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadUsers() // Recarga los datos del servidor
                Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show()
            }
        }

        //edit
        editUserLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val position = data.getIntExtra("userIndex", -1)
                    val userId = data.getIntExtra("usuarioId", -1)
                    val password = data.getStringExtra("usuarioPassword") ?: ""
                    val role = data.getStringExtra("usuarioRole") ?: ""

                    if (position != -1) {
                        fullList[position] = Usuario_(userId, password, role)
                        mAdapter.updateData(fullList)
                        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show()

                        loadUsers()
                    }
                }
            }
        }
        fabAgregarUsuario.setOnClickListener {
            val intent = Intent(this, AddUser::class.java)
            addUserLauncher.launch(intent)
        }

        setUpRecyclerView()
        loadUsers()
        enableSwipeToDeleteAndEdit()
    }

    private fun loadUsers() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading users...")
            setCancelable(false)
            show()
        }

        object : AsyncTask<Void, Void, List<Usuario_>>() {
            override fun doInBackground(vararg params: Void?): List<Usuario_> {
                return try {
                    val response = HttpHelper.getRawResponse("users")
                    Log.d("API_USERS", "Response: $response")

                    if (response != null) {
                        val json = JSONObject(response)
                        val dataArray = json.getJSONArray("data")
                        val type = object : TypeToken<List<Usuario_>>() {}.type
                        Gson().fromJson(dataArray.toString(), type)
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error loading users", e)
                    emptyList()
                }
            }

            override fun onPostExecute(result: List<Usuario_>) {
                progressDialog.dismiss()
                if (result.isNotEmpty()) {
                    fullList.clear()
                    fullList.addAll(result)
                    mAdapter.updateData(fullList)
                    Log.d("LOAD_USERS", "Loaded users: ${fullList.size}")
                } else {
                    Toast.makeText(this@User, "No users found", Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    private fun getUsuers(): MutableList<Usuario_> {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Loading users...")
            setCancelable(false)
            show()
        }

        val usuarios = mutableListOf<Usuario_>()
        try {
            val response = HttpHelper.getRawResponse("users")
            if (response != null) {
                val json = JSONObject(response)
                val dataArray = json.getJSONArray("data")
                val type = object : TypeToken<List<Usuario_>>() {}.type
                usuarios.addAll(Gson().fromJson<List<Usuario_>>(dataArray.toString(), type))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading teachers", e)
        } finally {
            progressDialog.dismiss()
        }

        return usuarios
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
                        val intent = Intent(this@User, EditUser::class.java).apply {
                            putExtra("usuarioId", student.id)
                            putExtra("usuarioPassword", student.password)
                            putExtra("usuarioRole", student.role)
                            putExtra("userIndex", position)
                        }
                        editUserLauncher.launch(intent)
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
        Log.d("DELETE_USER", "Attempting to delete user with ID: ${user.id}")

        AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    try {
                        val url = "users/${user.id}"
                        Log.d("DELETE_USER", "Request URL: $url")

                        val success = HttpHelper.deleteRequest(url)
                        Log.d("DELETE_USER", "Delete response: $success")

                        runOnUiThread {
                            if (success) {
                                mAdapter.removeItem(position)
                                Toast.makeText(this@User, "User deleted", Toast.LENGTH_SHORT).show()
                                loadUsers()
                            } else {
                                Toast.makeText(this@User, "Error deleting user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DELETE_USER", "Error deleting user", e)
                        runOnUiThread {
                            Toast.makeText(this@User, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}