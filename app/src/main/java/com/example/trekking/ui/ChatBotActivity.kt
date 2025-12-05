package com.example.trekking.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trekking.R
import com.example.trekking.ui.ChatGeminiHelper
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var input: EditText
    private lateinit var send: Button
    private lateinit var geminiHelper: ChatGeminiHelper

    private var greeted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_ai_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler = findViewById(R.id.recyclerChat)
        input = findViewById(R.id.etMessage)
        send = findViewById(R.id.btnSend)
        geminiHelper = ChatGeminiHelper()

        adapter = ChatAdapter()
        recycler.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        recycler.adapter = adapter

        // Initial welcome message
        if (!greeted) {
            adapter.add(ChatMessage.Bot(getString(R.string.chat_welcome)))
            adapter.add(ChatMessage.Bot(getString(R.string.chat_prompt_plan)))
            greeted = true
        }

        send.setOnClickListener {
            val text = input.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                adapter.add(ChatMessage.User(text))
                input.setText("")
                recycler.scrollToPosition(adapter.itemCount - 1)
                sendToGemini(text)
            }
        }
    }

    private fun sendToGemini(userText: String) {
        lifecycleScope.launch {
            val reply = geminiHelper.getResponse(userText)
            adapter.add(ChatMessage.Bot(reply))
            recycler.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
