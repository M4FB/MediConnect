package com.mediconnect.app.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.UserDto
import com.mediconnect.app.databinding.ItemUserBinding

class UsersAdapter(
    private val onToggleActiveClick: (UserDto) -> Unit
) : ListAdapter<UserDto, UsersAdapter.UserViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto) {
            binding.tvUserName.text = "${user.nombre} ${user.apellido}"
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = "Rol: ${user.role}"
            binding.tvUserStatus.text = "Estado: ${if (user.activo) "Activo" else "Inactivo"}"

            binding.btnToggleActive.text = if (user.activo) "Desactivar" else "Activar"
            binding.btnToggleActive.setOnClickListener {
                onToggleActiveClick(user)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserDto>() {
        override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem == newItem
        }
    }
}
