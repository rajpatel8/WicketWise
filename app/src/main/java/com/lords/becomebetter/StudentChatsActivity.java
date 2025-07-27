// REPLACE the entire StudentChatsActivity.java with this simplified version:

package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class StudentChatsActivity extends AppCompatActivity {

    private RecyclerView chatsRecyclerView;
    private ImageView backBtn;
    private TextView titleText, emptyStateText, addChatBtn;

    private DatabaseHelper databaseHelper;
    private ChatListAdapter chatListAdapter;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_chats);

        initializeViews();
        getIntentData();
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }

    private void initializeViews() {
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.titleText);
        emptyStateText = findViewById(R.id.emptyStateText);
        addChatBtn = findViewById(R.id.addChatBtn);

        databaseHelper = new DatabaseHelper(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        studentEmail = intent.getStringExtra("studentEmail");

        // Get student ID
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        studentId = student != null ? student.getId() : -1;
    }

    private void setupRecyclerView() {
        chatListAdapter = new ChatListAdapter(chatItem -> {
            // Open chat with selected coach
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("currentUserEmail", studentEmail);
            chatIntent.putExtra("currentUserType", "student");
            chatIntent.putExtra("otherUserId", chatItem.getCoachId());
            chatIntent.putExtra("otherUserType", "coach");
            chatIntent.putExtra("otherUserName", chatItem.getCoachName());
            startActivity(chatIntent);
        });

        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.setAdapter(chatListAdapter);
    }

    private void loadChats() {
        // Get all coaches that this student has chatted with
        List<Coach> allCoaches = databaseHelper.getAllCoaches();
        List<ChatListItem> chatItems = new ArrayList<>();

        for (Coach coach : allCoaches) {
            List<Message> messages = databaseHelper.getMessagesBetweenUsers(studentId, coach.getId());
            if (!messages.isEmpty()) {
                // Get last message
                Message lastMessage = messages.get(messages.size() - 1);

                // Get unread count
                int unreadCount = databaseHelper.getUnreadMessageCount(studentId, coach.getId());

                ChatListItem chatItem = new ChatListItem();
                chatItem.setCoachId(coach.getId());
                chatItem.setCoachName(coach.getName());
                chatItem.setCoachSpecialization(coach.getSpecialization());
                chatItem.setLastMessage(lastMessage.getMessageText());
                chatItem.setLastMessageTime(lastMessage.getFormattedTime());
                chatItem.setUnreadCount(unreadCount);

                chatItems.add(chatItem);
            }
        }

        if (chatItems.isEmpty()) {
            findViewById(R.id.emptyStateLayout).setVisibility(View.VISIBLE);
            chatsRecyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyStateLayout).setVisibility(View.GONE);
            chatsRecyclerView.setVisibility(View.VISIBLE);
        }

        chatListAdapter.updateChats(chatItems);
        titleText.setText("My Chats (" + chatItems.size() + ")");
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        addChatBtn.setOnClickListener(v -> {
            // Open coach list to start new chat
            Intent intent = new Intent(this, CoachListActivity.class);
            intent.putExtra("studentEmail", studentEmail);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}