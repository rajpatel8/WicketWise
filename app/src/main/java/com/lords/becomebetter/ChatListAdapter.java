package com.lords.becomebetter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatListItem> chatItems;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatListItem chatItem);
    }

    public ChatListAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatListItem chatItem = chatItems.get(position);
        holder.bind(chatItem, listener);
    }

    @Override
    public int getItemCount() {
        return chatItems != null ? chatItems.size() : 0;
    }

    public void updateChats(List<ChatListItem> newChatItems) {
        this.chatItems = newChatItems;
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView coachNameText;
        private TextView lastMessageText;
        private TextView lastMessageTimeText;
        private TextView unreadCountText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            coachNameText = itemView.findViewById(R.id.coachNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            lastMessageTimeText = itemView.findViewById(R.id.lastMessageTimeText);
            unreadCountText = itemView.findViewById(R.id.unreadCountText);
        }

        public void bind(ChatListItem chatItem, OnChatClickListener listener) {
            coachNameText.setText(chatItem.getCoachName());
            lastMessageText.setText(chatItem.getLastMessage());
            lastMessageTimeText.setText(chatItem.getLastMessageTime());

            // Show/hide unread count
            if (chatItem.hasUnreadMessages()) {
                unreadCountText.setVisibility(View.VISIBLE);
                unreadCountText.setText(chatItem.getUnreadCountText());
            } else {
                unreadCountText.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatClick(chatItem);
                }
            });
        }
    }
}