package com.social.iweaver.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.searchresponse.Datum;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.viewmodel.HandleRequestModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Activity context;
    private CustomLoader loader;
    private HandleRequestModel requestModel;
    private final List<Datum> serachResiltList;
    private DatabaseHelper databseHelper;
    private LoginAttributeResponse loginDetails;
    private final RecyclerViewClickListener mListener;

    // Constructor
    public SearchAdapter(Activity context, List<Datum> serachResiltList, RecyclerViewClickListener mListener) {
        this.context = context;
        this.serachResiltList = serachResiltList;
        this.mListener = mListener;
        databseHelper = new DatabaseHelper(context);
        loader = CustomLoader.getInstance();
        loginDetails = databseHelper.getUserDetails();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_layout, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.userName.setText(serachResiltList.get(position).getName());
        holder.info.setText("" + serachResiltList.get(position).getEmail());
        if (serachResiltList.get(position).getIsUserFriend().equalsIgnoreCase("no") && !serachResiltList.get(position).getEmail().equalsIgnoreCase(loginDetails.getEmail())) {
            holder.addFriend.setVisibility(View.VISIBLE);
        } else {
            holder.addFriend.setVisibility(View.GONE);
        }

        holder.paramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(position);
            }
        });
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject object = new JsonObject();
                object.addProperty("status", "send");
                object.addProperty("request_to", serachResiltList.get(position).getId());
                sendRequest(object);
                loader.showLoading(context, "Please wait. Processing your request");
                holder.addFriend.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return serachResiltList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout paramLayout;
        private final ImageView userImage;
        private final TextView info;
        private final TextView userName;
        private final Button addFriend;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_avataar);
            info = itemView.findViewById(R.id.tv_info);
            userName = itemView.findViewById(R.id.tv_user_name);
            addFriend = itemView.findViewById(R.id.bt_add);
            paramLayout = itemView.findViewById(R.id.rl_param_layout);
        }
    }

    public void sendRequest(JsonObject object) {
        requestModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.getApplication()).create(HandleRequestModel.class);
        requestModel.initiateRequestProcess(context, object)
                .observe((LifecycleOwner) context, new Observer<HandleRequestResponse>() {
                    @Override
                    public void onChanged(HandleRequestResponse requestResponse) {
                        requestModel = null;
                        loader.hideLoading();
                        if (requestResponse != null) {
                            loader.showSnackBar(context, requestResponse.getMessage(), false);
                        } else {
                            loader.showSnackBar(context, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }
}
