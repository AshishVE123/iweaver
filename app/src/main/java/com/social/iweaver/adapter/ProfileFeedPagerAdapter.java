package com.social.iweaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.social.iweaver.R;
import com.social.iweaver.activity.PostActivity;

import java.util.List;

public class ProfileFeedPagerAdapter extends RecyclerView.Adapter<ProfileFeedPagerAdapter.ViewHolder> {
    private List<String> postList;
    private Context ctx;
    private String postId;


    public ProfileFeedPagerAdapter(Context ctx, List<String> imageList, String postId) {
        this.ctx = ctx;
        postList = imageList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ProfileFeedPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.pager_profile_feed, parent, false);
        return new ProfileFeedPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileFeedPagerAdapter.ViewHolder holder, int position) {
        Glide.with(ctx).
                load(postList.get(position)).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                placeholder(R.drawable.image_progress_animation).
                into(holder.images);
        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, PostActivity.class);
                intent.putExtra("postId", postId);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.iv_post_image);
        }
    }
}
