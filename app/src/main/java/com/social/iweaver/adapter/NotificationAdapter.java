package com.social.iweaver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.notificationresponse.Datum;
import com.social.iweaver.listener.RecyclerViewClickListener;


import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private List<Datum> notificationList;
    private RecyclerViewClickListener listener;

    // Constructor
    public NotificationAdapter(Context context, List<Datum> notificationList, RecyclerViewClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notification_layout, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        holder.tv_user_post_time.setText(notificationList.get(position).getCreatedAt());
        holder.info.setText(notificationList.get(position).getNotification());

        Glide.with(context).
                load(notificationList.get(position).getUserImage()).
                placeholder(R.drawable.image_progress_animation).
                into(holder.userImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return notificationList.size();
    }

    /**
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView info;
        private TextView tv_user_post_time;

        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_avatar);
            info = itemView.findViewById(R.id.tv_notification_text);
            tv_user_post_time = itemView.findViewById(R.id.tv_user_post_time);
        }
    }
}
