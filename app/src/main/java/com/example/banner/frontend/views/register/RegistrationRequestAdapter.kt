package com.example.banner.frontend.views.register
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.R
import com.example.banner.backend.Models.RegistrationRequest_

class RegistrationRequestAdapter(
    private val onApprove: (Int) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<RegistrationRequestAdapter.RequestViewHolder>() {

    private var requests: List<RegistrationRequest_> = emptyList()

    fun submitList(newRequests: List<RegistrationRequest_>?) {
        requests = newRequests ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registration_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount() = requests.size

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(request: RegistrationRequest_) {
            itemView.apply {
                findViewById<TextView>(R.id.request_id).text = "ID: ${request.id}"
                findViewById<TextView>(R.id.user_id).text = "User ID: ${request.user_id}"
                findViewById<TextView>(R.id.role).text = "Role: ${request.role}"

                findViewById<Button>(R.id.approve_btn).setOnClickListener {
                    onApprove(request.id)
                }

                findViewById<Button>(R.id.reject_btn).setOnClickListener {
                    onReject(request.id)
                }
            }
        }
    }
}