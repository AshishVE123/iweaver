package com.social.iweaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.social.iweaver.R;
import com.social.iweaver.activity.PostActivity;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class HomeImagePagerAdapter extends RecyclerView.Adapter<HomeImagePagerAdapter.ViewHolder> {
    private List<String> postList;
    private Context ctx;
    private String postId;

    /**
     *
     * @param ctx
     * @param imageList
     * @param postId
     */
    HomeImagePagerAdapter(Context ctx, List<String> imageList, String postId) {
        this.ctx = ctx;
        postList = imageList;
        this.postId = postId;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.pager_image_adapter, parent, false);
        return new ViewHolder(view);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(ctx).
                    load(postList.get(position)).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    skipMemoryCache(true).
                    placeholder(R.drawable.image_progress_animation).
                    into(holder.images);
        } catch (Exception ex) {
            System.out.println("Catching the glide exception::" + ex.getMessage());
            ex.printStackTrace();
        }
        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, PostActivity.class);
                intent.putExtra("postId", postId);
                ctx.startActivity(intent);
            }
        });
    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;

        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.iv_post_image);
        }
    }
}
