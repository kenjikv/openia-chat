package com.kawaida.openia_chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var questionEditText: EditText
    private lateinit var sendButton: Button
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionEditText = findViewById(R.id.questionEditText)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        val service = retrofit.create(OpenAIService::class.java)

        sendButton.setOnClickListener {
            val prompt = questionEditText.text.toString()
            val request = ChatRequest(
                model = "gpt-3.5-turbo",
                temperature = 1,
                messages = listOf(Message(role = "user", content = prompt))
            )

            val call = service.sendMessage(request)
            call.enqueue(object : retrofit2.Callback<ChatResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: retrofit2.Call<ChatResponse>,
                    response: retrofit2.Response<ChatResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val responseMessage =
                            responseBody?.choices?.firstOrNull()?.message?.content ?: "No response"
                        messages.add(ChatMessage(prompt, true))
                        messages.add(ChatMessage(responseMessage, false))
                        chatAdapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(messages.size - 1)
                    } else {
                        messages.add(ChatMessage(prompt, true))
                        messages.add(ChatMessage(response.errorBody()?.string().toString(), false))
                        chatAdapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(messages.size - 1)
                    }

                    questionEditText.setText("")
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: retrofit2.Call<ChatResponse>, t: Throwable) {
                    messages.add(ChatMessage(prompt, true))
                    messages.add(ChatMessage(t.message.toString(), false))
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                }
            })
        }
    }
}
