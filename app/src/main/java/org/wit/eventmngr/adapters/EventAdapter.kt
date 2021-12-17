package org.wit.eventmngr.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import org.wit.eventmngr.databinding.CardEventBinding
import org.wit.eventmngr.models.EventModel

interface EventListener {
    fun onEventClick(event: EventModel)
}

class EventAdapter constructor(
    private var events: List<EventModel>,
    private val listener: EventListener
) :
    RecyclerView.Adapter<EventAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardEventBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val event = events[holder.adapterPosition]
        holder.bind(event, listener)
    }

    override fun getItemCount(): Int = events.size

    class MainHolder(private val binding: CardEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventModel, listener: EventListener) {
            binding.eventTitle.text = event.title
            binding.description.text = event.description
            Picasso.get().load(event.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onEventClick(event) }
        }
    }
}