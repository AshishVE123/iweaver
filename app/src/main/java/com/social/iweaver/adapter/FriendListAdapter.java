package com.social.iweaver.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.social.iweaver.R;
import com.social.iweaver.apiresponse.friendlist.Datum;
import com.social.iweaver.listener.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private final Context context;
    private List<Datum> friendList;
    private final RecyclerViewClickListener clickListener;

    /**
     *
     * @param context
     * @param friendListResponse
     * @param clickListener
     */
    public FriendListAdapter(Context context, List<Datum> friendListResponse, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.friendList = friendListResponse;
        this.clickListener = clickListener;
    }

    /**
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friendlist_layout, parent, false);
        return new FriendListAdapter.ViewHolder(view);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // to set data to textview and imageview of each card layout
        holder.userName.setText(friendList.get(position).getName());
        holder.info.setText(friendList.get(position).getEmail());
        holder.bt_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(position);
            }
        });
        //    holder.info.setText("" + model.getCourse_rating());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return friendList.size();
    }

    /**
     *
     * @param filterer
     */
    public void filterList(ArrayList<Datum> filterer) {
        friendList = filterer;
        notifyDataSetChanged();
    }

    public void refreshData(int position) {
        friendList.remove(position);
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView info;
        private final TextView userName;
        private final Button bt_unfollow;

        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_avataar);
            info = itemView.findViewById(R.id.tv_info);
            userName = itemView.findViewById(R.id.tv_user_name);
            bt_unfollow = itemView.findViewById(R.id.bt_unfriend);
        }
    }
}
