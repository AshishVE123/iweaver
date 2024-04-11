package com.social.iweaver.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.activity.MyProfileActivity;
import com.social.iweaver.apiresponse.LikePosteponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.globalpost.PostDetails;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.viewmodel.LikePostViewModel;


import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {

    private static Activity context;
    private final List<PostDetails> postFeedResponse;
    private final LikePostViewModel likeModel;
    private final LoginAttributeResponse loginDetails;
    private final RecyclerViewClickListener listener;
    private final CustomAlertDialog dialog;
    private HomeImagePagerAdapter pagerAdapter;


    /**
     *
     * @param context
     * @param homeFeedResponse
     * @param loginDetails
     * @param listener
     */
    public HomeFeedAdapter(Activity context, List<PostDetails> homeFeedResponse, LoginAttributeResponse loginDetails, RecyclerViewClickListener listener) {
        HomeFeedAdapter.context = context;
        this.postFeedResponse = homeFeedResponse;
        this.loginDetails = loginDetails;
        this.listener = listener;
        dialog = CustomAlertDialog.getInstance(context);
        likeModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.getApplication()).create(LikePostViewModel.class);


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
    public HomeFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_globalfeed_layout, parent, false);
        return new HomeFeedAdapter.ViewHolder(view);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HomeFeedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!postFeedResponse.get(position).getIsPostReport().equalsIgnoreCase("yes")) {
            holder.userName.setText(postFeedResponse.get(position).getUserName());
            holder.userPost.setText(postFeedResponse.get(position).getContent());
            holder.postedTime.setText(postFeedResponse.get(position).getPostedOn());
            holder.userLikeCount.setText(postFeedResponse.get(position).getPostLikeCount() + "");
            holder.userCommentCount.setText(postFeedResponse.get(position).getPostCommentCount() + "");
            if(postFeedResponse.get(position).getPostLikeCount()>0){
                 holder.likeView.setImageResource(R.drawable.heart_liked);
            }
            else{
                holder.likeView.setImageResource(R.drawable.ic_heart);
            }
            Glide.with(context).
                    load(postFeedResponse.get(position).getUserImage()).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    skipMemoryCache(true).
                    placeholder(R.drawable.image_progress_animation).
                    into(holder.userImage);

            if ((postFeedResponse.get(position).getPostImage().size() > 0)) {
                holder.imagePager.setVisibility(View.VISIBLE);
                pagerAdapter = new HomeImagePagerAdapter(context, postFeedResponse.get(position).getPostImage(),postFeedResponse.get(position).getPostId().toString());
                holder.imagePager.setAdapter(pagerAdapter);
            } else {
                holder.imagePager.setVisibility(View.GONE);
            }
        } else {
            holder.cv_parent_cardview_params.setVisibility(View.GONE);
        }
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
        holder.userLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject object = new JsonObject();
                object.addProperty("postId", postFeedResponse.get(position).getPostId());
                callLikeApi(position, object, holder);
                holder.likeView.setImageResource(R.drawable.heart_liked);
            }
        });
        holder.iv_block_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.showBlockPostAlertDialog(context, postFeedResponse.get(position).getPostId(), position);
            }
        });
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyProfileActivity.class);
                intent.putExtra("userId", postFeedResponse.get(position).getUserId().toString());
                context.startActivity(intent);


            }
        });

    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return postFeedResponse.size();
    }

    /**
     *
     * @param position
     * @param object
     * @param holder
     */
    public void callLikeApi(int position, JsonObject object, HomeFeedAdapter.ViewHolder holder) {
        likeModel.initiateLikeProcess(context, object)
                .observe((LifecycleOwner) context, new Observer<LikePosteponse>
                        () {
                    @Override
                    public void onChanged(LikePosteponse likePosteponse) {

                        if (likePosteponse != null) {
                            holder.userLikeCount.setText(postFeedResponse.get(position).getPostLikeCount() + 1 + "");
                        } else {

                        }
                    }
                });
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final TextView userPost;
        private final TextView userLikeCount;
        private final TextView userCommentCount;
        private final ImageView userImage;
        private final ImageView iv_block_post;
        private final ImageView iv_comment;
        private final ImageView likeView;
        private final ImageView userLike;
        private final TextView postedTime;
        private CardView cv_parent_cardview_params;

        private ViewPager2 imagePager;

        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            userPost = itemView.findViewById(R.id.tv_user_post);
            userLikeCount = itemView.findViewById(R.id.tv_gf_like_count);
            userCommentCount = itemView.findViewById(R.id.tv_gf_comment_count);
            iv_comment = itemView.findViewById(R.id.iv_comment);
            userLike = itemView.findViewById(R.id.iv_like);
            userImage = itemView.findViewById(R.id.iv_user_image);
            iv_block_post = itemView.findViewById(R.id.iv_block_post);
            postedTime = itemView.findViewById(R.id.tv_user_post_time);
            cv_parent_cardview_params = itemView.findViewById(R.id.cv_parent_cardview_params);
            imagePager = itemView.findViewById(R.id.vp_post_image);
            likeView = itemView.findViewById(R.id.iv_like);

        }
    }
}
