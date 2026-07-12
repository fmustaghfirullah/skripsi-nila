package com.faisal.bangunruang;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.faisal.bangunruang.adapter.ChatAdapter;
import com.faisal.bangunruang.databinding.ActivityChatbotBinding;
import com.faisal.bangunruang.ml.NlpProcessor;
import com.faisal.bangunruang.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private ActivityChatbotBinding binding;
    private NlpProcessor nlpProcessor;
    private List<ChatMessage> messages;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        nlpProcessor = new NlpProcessor();
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);

        binding.rvChat.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChat.setAdapter(adapter);

        addBotMessage(getString(R.string.chat_welcome));

        String shapeContext = getIntent().getStringExtra("shape_context");
        if (shapeContext != null) {
            String contextMessage = "Kamu sedang mempelajari tentang " + shapeContext +
                    ". Silakan tanyakan apa saja tentang " + shapeContext + "! 😊";
            addBotMessage(contextMessage);
        }

        binding.fabSend.setOnClickListener(v -> sendMessage());

        binding.etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String text = binding.etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        binding.etMessage.setText("");

        binding.rvChat.postDelayed(() -> {
            String response = nlpProcessor.processQuery(text);
            addBotMessage(response);
        }, 500);
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, ChatMessage.TYPE_BOT));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        binding.rvChat.scrollToPosition(messages.size() - 1);
    }
}
