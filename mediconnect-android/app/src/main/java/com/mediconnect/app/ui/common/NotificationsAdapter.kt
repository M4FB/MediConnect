package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.NotificacionDto
import com.mediconnect.app.databinding.ItemNotificationBinding

class NotificationsAdapter(
    private val onMarkReadClick: (NotificacionDto) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    private var items: List<NotificacionDto> = emptyList()

    fun submitList(list: List<NotificacionDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: NotificacionDto) {
            binding.tvNotifTitle.text = notif.titulo
            binding.tvNotifDate.text = notif.fecha
            binding.tvNotifMessage.text = notif.mensaje

            if (notif.leido) {
                binding.btnMarkRead.visibility = View.GONE
                binding.root.alpha = 0.6f
            } else {
                binding.btnMarkRead.visibility = View.VISIBLE
                binding.root.alpha = 1.0f
                binding.btnMarkRead.setOnClickListener { onMarkReadClick(notif) }
            }
        }
    }
}
