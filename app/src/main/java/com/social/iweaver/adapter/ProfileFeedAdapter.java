package com.social.iweaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.social.iweaver.R;
import com.social.iweaver.activity.CommentActivity;
import com.social.iweaver.activity.PostActivity;
import com.social.iweaver.apiresponse.userpost.Post;

import java.util.List;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> profileFeedResponse;

    private ProfileFeedPagerAdapter pagerAdapter;


    // Constructor
    public ProfileFeedAdapter(Context context, List<Post> profileFeedResponse) {
        this.context = context;
        this.profileFeedResponse = profileFeedResponse;

    }

    @NonNull
    @Override
    public ProfileFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profilefeed_adapter, parent, false);
        return new ProfileFeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileFeedAdapter.ViewHolder holder, int position) {
        holder.userName.setText(profileFeedResponse.get(position).getUserName());
        holder.userPost.setText(profileFeedResponse.get(position).getContent());
        holder.postedTime.setText(profileFeedResponse.get(position).getPostedOn());
        System.out.println("CHecking the like and post count::" + profileFeedResponse.get(position).getPostLikeCount() + " " + profileFeedResponse.get(position).getPostCommentCount());
        holder.userLike.setText(profileFeedResponse.get(position).getPostLikeCount() + "");
        holder.userComment.setText(profileFeedResponse.get(position).getPostCommentCount() + "");
        if(profileFeedResponse.get(position).getPostLikeCount()>0){
           holder.likePost.setImageResource(R.drawable.heart_liked);
        }
        else{
            holder.likePost.setImageResource(R.drawable.ic_heart);
        }
        Glide.with(context).load(profileFeedResponse.get(position).getUserImage()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.image_progress_animation).into(holder.userImage);
        if (profileFeedResponse.get(position).getPostImage().size() > 0) {
            holder.pager.setVisibility(View.VISIBLE);
            pagerAdapter = new ProfileFeedPagerAdapter(context, profileFeedResponse.get(position).getPostImage(),profileFeedResponse.get(position).getPostId().toString());
            holder.pager.setAdapter(pagerAdapter);
            // Picasso.get().load(profileFeedResponse.get(position).getPostImage().get(0)).into(holder.userPostImage);
          //  Glide.with(context).load(profileFeedResponse.get(position).getPostImage().get(0)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.image_progress_animation).into(holder.userPostImage);
        } else {
            holder.pager.setVisibility(View.GONE);
        }
        holder.commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", profileFeedResponse.get(position).getPostId().toString());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return profileFeedResponse.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;

        private ImageView likePost;
        private final TextView userName;
        private final TextView userPost;
        private final TextView userLike;
        private final TextView userComment;
        private final ImageView commentPost;
        private final TextView postedTime;

        private ViewPager2 pager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            userPost = itemView.findViewById(R.id.tv_user_post);
            userLike = itemView.findViewById(R.id.tv_like_count);
            userComment = itemView.findViewById(R.id.tv_comment_count);
            postedTime = itemView.findViewById(R.id.tv_posted_time);
            userImage = itemView.findViewById(R.id.iv_user_image);
            likePost = itemView.findViewById(R.id.iv_like);
            commentPost = itemView.findViewById(R.id.iv_comment);
            pager = itemView.findViewById(R.id.vp_post_image);
        }
    }
}
