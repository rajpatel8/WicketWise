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

public class CoachChatsActivity extends AppCompatActivity {

    private RecyclerView chatsRecyclerView;
    private ImageView backBtn;
    private TextView titleText, emptyStateText;

    private DatabaseHelper databaseHelper;
    private CoachChatListAdapter chatListAdapter;
    private String coachEmail;
    private int coachId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_chats);

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

        databaseHelper = new DatabaseHelper(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        coachEmail = intent.getStringExtra("coachEmail");
        coachId = intent.getIntExtra("coachId", -1);
    }

    private void setupRecyclerView() {
        chatListAdapter = new CoachChatListAdapter(chatItem -> {
            // Open chat with selected student
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("currentUserEmail", coachEmail);
            chatIntent.putExtra("currentUserType", "coach");
            chatIntent.putExtra("otherUserId", chatItem.getStudentId());
            chatIntent.putExtra("otherUserType", "student");
            chatIntent.putExtra("otherUserName", chatItem.getStudentName());
            startActivity(chatIntent);
        });

        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.setAdapter(chatListAdapter);
    }

    private void loadChats() {
        // Get all students that this coach has chatted with
        List<Student> allStudents = databaseHelper.getAllStudents();
        List<CoachChatListItem> chatItems = new ArrayList<>();

        for (Student student : allStudents) {
            List<Message> messages = databaseHelper.getMessagesBetweenUsers(student.getId(), coachId);
            if (!messages.isEmpty()) {
                // Get last message
                Message lastMessage = messages.get(messages.size() - 1);

                // Get unread count
                int unreadCount = databaseHelper.getUnreadMessageCount(coachId, student.getId());

                CoachChatListItem chatItem = new CoachChatListItem();
                chatItem.setStudentId(student.getId());
                chatItem.setStudentName(student.getName());
                chatItem.setStudentSkillLevel(student.getSkillLevel() != null ? student.getSkillLevel() : "Beginner");
                chatItem.setLastMessage(lastMessage.getMessageText());
                chatItem.setLastMessageTime(lastMessage.getFormattedTime());
                chatItem.setUnreadCount(unreadCount);

                chatItems.add(chatItem);
            }
        }

        if (chatItems.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            chatsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            chatsRecyclerView.setVisibility(View.VISIBLE);
        }

        chatListAdapter.updateChats(chatItems);
        titleText.setText("My Chats (" + chatItems.size() + " students)");
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}