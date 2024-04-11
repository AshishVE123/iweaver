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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.social.iweaver.R;
import com.social.iweaver.activity.MyProfileActivity;
import com.social.iweaver.apiresponse.usercomment.Datum;
import com.social.iweaver.listener.RecyclerViewClickListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final Context context;
    private final List<Datum> commentResponse;
    private final RecyclerViewClickListener clickListener;

    /**
     *
     * @param context
     * @param commentResponse
     * @param clickListener
     */
    public CommentAdapter(Context context, List<Datum> commentResponse, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.commentResponse = commentResponse;
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
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment_layout, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        holder.userName.setText(commentResponse.get(position).getCommentUser());
        holder.info.setText(commentResponse.get(position).getComment());
        Glide.with( context).
                load(commentResponse.get(position).getCommentUserImage()).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                placeholder(R.drawable.image_progress_animation).
                into(holder.userImage);

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return commentResponse.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView info;
        private final TextView userName;

        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_avataar);
            info = itemView.findViewById(R.id.tv_comment_info);
            userName = itemView.findViewById(R.id.tv_commented_user_name);

        }
    }


}
