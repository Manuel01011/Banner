package com.example.banner.frontend.views.career
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.backend_banner.backend.Models.Career_
import com.example.banner.R

class RecyclerAdapter(
    private val carreras: MutableList<Career_>,
    private val context: Context,
    private val onItemEdit: (Career_) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //onCreateViewHolder: Infla el diseño de cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.career_item, parent, false))
    }

    //onBindViewHolder: Asigna los datos de un SuperHeroe a la vista correspondiente.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("RecyclerAdapter", "Binding item en posición $position")
        holder.bind(carreras[position], context, onItemEdit)
    }

    override fun getItemCount(): Int = carreras.size

    fun getItem(position: Int): Career_ = carreras[position]

    fun removeItem(position: Int) {
        carreras.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int,item: Career_) {
        carreras.set(position,item)
        notifyItemChanged(position)
    }

    fun restoreItem(item: Career_, position: Int) {
        carreras.add(position, item)
        notifyItemInserted(position)
    }

    fun updateData(newData: List<Career_>) {
        val oldSize = carreras.size
        carreras.clear()
        carreras.addAll(newData)

        // Notifica cambios de manera más eficiente:
        if (oldSize == newData.size) {
            notifyItemRangeChanged(0, oldSize)
        } else {
            notifyDataSetChanged()  // Si el tamaño cambió, actualiza todo
        }
    }


    //ViewHolder: Clase interna que define las referencias a los elementos de la vista y maneja eventos de clic.
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val codCarrera: TextView = view.findViewById(R.id.text_cod)
        private val nameCarrea: TextView = view.findViewById(R.id.text_name)
        private val titleCarrera: TextView = view.findViewById(R.id.text_title)
        private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        private val editButton: ImageButton = view.findViewById(R.id.btn_edit)

        fun bind(carrera: Career_, context: Context, onEdit: (Career_) -> Unit) {
            Log.d("ViewHolder", "Binding carrera: ${carrera.name}")
            codCarrera.text = carrera.cod.toString()
            nameCarrea.text = carrera.name
            titleCarrera.text = carrera.title

            // Solo para mostrar un Toast si se hace clic en todo el ítem
            itemView.setOnClickListener {
                Log.d("ViewHolder", "Click on carrera: ${carrera.cod}")
                Toast.makeText(context, carrera.name, Toast.LENGTH_SHORT).show()
            }

            // Ahora sí: botón de edición llama al callback
            editButton.setOnClickListener {

                if (context is Career) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.editCareer(position)
                    }
                }
            }

            btnDelete.setOnClickListener {
                if (context is Career) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        context.deleteCareer(position)
                    }
                }
            }
        }
    }
}