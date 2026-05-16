package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

    private List<TrainModel> trainList;
    private OnSpeakClickListener speakClickListener;

    public interface OnSpeakClickListener {
        void onSpeakClick(TrainModel train);
    }

    public TrainAdapter(List<TrainModel> trainList, OnSpeakClickListener listener) {
        this.trainList = trainList;
        this.speakClickListener = listener;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_train, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        TrainModel train = trainList.get(position);
        holder.tvTrainName.setText(train.getTrainName());
        holder.tvPlatform.setText("Platform: " + train.getPlatform());
        holder.tvArrivalTime.setText(train.getArrivalTime());
        holder.tvDelay.setText(train.getDelay());

        // Add coach sequence views
        holder.coachLayout.removeAllViews();
        for (String coach : train.getCoachSequence()) {
            TextView coachView = new TextView(holder.itemView.getContext());
            coachView.setText(coach);
            coachView.setPadding(16, 12, 16, 12);
            coachView.setTextSize(16);
            coachView.setTextColor(Color.WHITE);
            coachView.setBackgroundResource(R.drawable.bg_coach);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            coachView.setLayoutParams(params);
            holder.coachLayout.addView(coachView);
        }

        holder.btnSpeakItem.setOnClickListener(v -> {
            if (speakClickListener != null) {
                speakClickListener.onSpeakClick(train);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }

    public static class TrainViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrainName, tvPlatform, tvArrivalTime, tvDelay;
        LinearLayout coachLayout;
        Button btnSpeakItem;

        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrainName = itemView.findViewById(R.id.tvTrainName);
            tvPlatform = itemView.findViewById(R.id.tvPlatform);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvDelay = itemView.findViewById(R.id.tvDelay);
            coachLayout = itemView.findViewById(R.id.coachLayout);
            btnSpeakItem = itemView.findViewById(R.id.btnSpeakItem);
        }
    }
}
