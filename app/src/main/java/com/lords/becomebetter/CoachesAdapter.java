package com.lords.becomebetter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoachesAdapter extends RecyclerView.Adapter<CoachesAdapter.CoachViewHolder> {

    private List<Coach> coaches;
    private OnCoachClickListener listener;

    public interface OnCoachClickListener {
        void onCoachClick(Coach coach);
    }

    public CoachesAdapter(OnCoachClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CoachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coach_for_chat, parent, false);
        return new CoachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoachViewHolder holder, int position) {
        Coach coach = coaches.get(position);
        holder.bind(coach, listener);
    }

    @Override
    public int getItemCount() {
        return coaches != null ? coaches.size() : 0;
    }

    public void updateCoaches(List<Coach> newCoaches) {
        this.coaches = newCoaches;
        notifyDataSetChanged();
    }

    static class CoachViewHolder extends RecyclerView.ViewHolder {
        private TextView coachNameText;
        private TextView coachSpecializationText;
        private TextView coachExperienceText;

        public CoachViewHolder(@NonNull View itemView) {
            super(itemView);
            coachNameText = itemView.findViewById(R.id.coachNameText);
            coachSpecializationText = itemView.findViewById(R.id.coachSpecializationText);
            coachExperienceText = itemView.findViewById(R.id.coachExperienceText);
        }

        public void bind(Coach coach, OnCoachClickListener listener) {
            coachNameText.setText(coach.getName());
            coachSpecializationText.setText(coach.getSpecialization());
            coachExperienceText.setText(coach.getExperienceYears() + " years experience");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCoachClick(coach);
                }
            });
        }
    }
}