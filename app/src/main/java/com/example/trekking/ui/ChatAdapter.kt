package com.example.trekking.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trekking.R

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<ChatMessage>()

    fun add(msg: ChatMessage) {
        data.add(msg)
        notifyItemInserted(data.lastIndex)
    }

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is ChatMessage.User -> 1
        is ChatMessage.Bot -> 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val v = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserVH(v)
        } else {
            val v = inflater.inflate(R.layout.item_chat_bot, parent, false)
            BotVH(v)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (holder) {
            is UserVH -> holder.bind((item as ChatMessage.User).text)
            is BotVH -> holder.bind((item as ChatMessage.Bot).text)
        }
    }
}

class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.tvMessage)
    fun bind(text: String) { tv.text = text }
}

class BotVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.tvMessage)
    fun bind(text: String) { tv.text = text }
}
