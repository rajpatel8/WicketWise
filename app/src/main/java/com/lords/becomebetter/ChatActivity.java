package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText messageInputEdit;
    private ImageView sendMessageBtn, backBtn;
    private TextView chatUserNameText, chatUserRoleText;

    private MessagesAdapter messagesAdapter;
    private DatabaseHelper databaseHelper;
    private List<Message> messagesList;

    // Current user info
    private int currentUserId;
    private String currentUserType; // "student" or "coach"
    private String currentUserEmail;

    // Other user info
    private int otherUserId;
    private String otherUserType;
    private String otherUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();
        getIntentData();
        setupRecyclerView();
        loadMessages();
        setupClickListeners();
        updateToolbar();
    }

    private void initializeViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInputEdit = findViewById(R.id.messageInputEdit);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        backBtn = findViewById(R.id.backBtn);
        chatUserNameText = findViewById(R.id.chatUserNameText);
        chatUserRoleText = findViewById(R.id.chatUserRoleText);

        databaseHelper = new DatabaseHelper(this);
        messagesList = new ArrayList<>();
    }

    private void getIntentData() {
        Intent intent = getIntent();

        // Current user info (who opened the chat)
        currentUserEmail = intent.getStringExtra("currentUserEmail");
        currentUserType = intent.getStringExtra("currentUserType"); // "student" or "coach"

        // Other user info (who they're chatting with)
        otherUserId = intent.getIntExtra("otherUserId", -1);
        otherUserType = intent.getStringExtra("otherUserType"); // "student" or "coach"
        otherUserName = intent.getStringExtra("otherUserName");

        // Get current user ID based on email and type
        if ("student".equals(currentUserType)) {
            Student student = databaseHelper.getStudentByEmail(currentUserEmail);
            currentUserId = student != null ? student.getId() : -1;
        } else {
            Coach coach = databaseHelper.getCoachByEmail(currentUserEmail);
            currentUserId = coach != null ? coach.getId() : -1;
        }

        // Validate data
        if (currentUserId == -1 || otherUserId == -1 || TextUtils.isEmpty(otherUserName)) {
            Toast.makeText(this, "Error loading chat", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        messagesAdapter = new MessagesAdapter(messagesList, currentUserId, currentUserType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom (most recent messages)

        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }

    private void loadMessages() {
        // Load messages between student and coach
        int studentId = "student".equals(currentUserType) ? currentUserId : otherUserId;
        int coachId = "coach".equals(currentUserType) ? currentUserId : otherUserId;

        messagesList.clear();
        messagesList.addAll(databaseHelper.getMessagesBetweenUsers(studentId, coachId));
        messagesAdapter.notifyDataSetChanged();

        // Scroll to bottom to show latest messages
        if (!messagesList.isEmpty()) {
            messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
        }

        // Mark messages as read
        databaseHelper.markMessagesAsRead(otherUserId, currentUserId);
    }

    private void updateToolbar() {
        chatUserNameText.setText(otherUserName);
        chatUserRoleText.setText(otherUserType.substring(0, 1).toUpperCase() + otherUserType.substring(1));
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        sendMessageBtn.setOnClickListener(v -> sendMessage());

        // Send message on Enter key (optional)
        messageInputEdit.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String messageText = messageInputEdit.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save message
        Message message = new Message(currentUserId, otherUserId, currentUserType, messageText);
        long messageId = databaseHelper.sendMessage(currentUserId, otherUserId, currentUserType, messageText);

        if (messageId > 0) {
            message.setMessageId((int) messageId);

            // Add to list and update UI
            messagesList.add(message);
            messagesAdapter.addMessage(message);

            // Clear input and scroll to bottom
            messageInputEdit.setText("");
            messagesRecyclerView.scrollToPosition(messagesList.size() - 1);

        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh messages when returning to chat
        loadMessages();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}