package com.example.banner.frontend.views.user

import Usuario_
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.R

class RecyclerAdapter8(
    private val usuarios: MutableList<Usuario_>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter8.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(usuarios[position], context)
    }

    override fun getItemCount(): Int = usuarios.size

    fun getItem(position: Int): Usuario_ = usuarios[position]

    fun removeItem(position: Int) {
        usuarios.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Usuario_, position: Int) {
        usuarios.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Usuario_>) {
        usuarios.clear()
        usuarios.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textUserId: TextView = view.findViewById(R.id.text_user_id)
        private val textUserPassword: TextView = view.findViewById(R.id.text_user_password)
        private val textUserRole: TextView = view.findViewById(R.id.text_user_role)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)

        fun bind(usuario: Usuario_, context: Context) {
            textUserId.text = "ID Usuario: ${usuario.id}"
            textUserPassword.text = "Contraseña: ${usuario.password}"
            textUserRole.text = "Rol: ${usuario.role}"

            itemView.setOnClickListener {
                Toast.makeText(context, "Usuario ID: ${usuario.id}", Toast.LENGTH_SHORT).show()
            }


            btnDelete.setOnClickListener {
                if (context is User) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteUser(position)
                    }
                }
            }
        }
    }
}