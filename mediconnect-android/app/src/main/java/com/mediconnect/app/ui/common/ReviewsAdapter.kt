package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.ValoracionDto
import com.mediconnect.app.databinding.ItemReviewBinding

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    private var items: List<ValoracionDto> = emptyList()

    fun submitList(list: List<ValoracionDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ValoracionDto) {
            binding.tvReviewRating.text = "Calificación: ${review.calificacion}/5 | Fecha: ${review.createdAt ?: ""}"
            binding.tvReviewComment.text = review.comentario ?: "(Sin comentario)"
        }
    }
}
