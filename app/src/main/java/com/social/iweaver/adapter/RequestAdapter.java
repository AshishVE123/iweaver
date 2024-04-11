package com.social.iweaver.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.activity.NetworkIssueActivity;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.friendrequest.Datum;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.HandleRequestModel;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private  Activity context;
    private  List<Datum> requestList;
    private HandleRequestModel acceptModel;
    private int clickedPosition = -1;
    private CustomLoader loader;

    // Constructor
    public RequestAdapter(Activity context, List<Datum> requestList) {
        this.context = context;
        this.requestList = requestList;
        loader = CustomLoader.getInstance();

    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_request_layout, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // to set data to textview and imageview of each card layout;
        holder.userName.setText(requestList.get(position).getName());
        holder.info.setText(requestList.get(position).getEmail());
        holder.bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.getConnectivityStatusString(context)) {
                    loader.showLoading(context, "Please wait. Processing your request");
                    clickedPosition = position;
                    JsonObject object = new JsonObject();
                    object.addProperty("request_to", requestList.get(position).getId());
                    object.addProperty("status", "accept");
                    callRequestHandleApi(object, clickedPosition, holder);
                } else {
                    context.startActivity(new Intent(context, NetworkIssueActivity.class));
                }
            }
        });
        holder.bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.getConnectivityStatusString(context)) {
                    loader.showLoading(context, "Please wait. Processing your request");
                    clickedPosition = position;
                    JsonObject object = new JsonObject();
                    object.addProperty("request_to", requestList.get(position).getId());
                    object.addProperty("status", "reject");
                    callRequestHandleApi(object, clickedPosition, holder);
                } else {
                    context.startActivity(new Intent(context, NetworkIssueActivity.class));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return requestList.size();
    }

    public void callRequestHandleApi(JsonObject object, int position, RequestAdapter.ViewHolder holder) {
        acceptModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.getApplication()).create(HandleRequestModel.class);
        acceptModel.initiateRequestProcess(context, object)
                .observe((LifecycleOwner) context, new Observer<HandleRequestResponse>() {
                    @Override
                    public void onChanged(HandleRequestResponse acceptResponse) {

                        if (acceptResponse != null) {
                            loader.showSnackBar(context, acceptResponse.getMessage(), false);
                            requestList.remove(position);
                            notifyDataSetChanged();
                            acceptModel = null;
                            clickedPosition = -1;
                            loader.hideLoading();
                        } else {
                            clickedPosition = -1;
                            loader.showSnackBar(context, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView info;
        private final TextView userName;
        private final Button bt_accept;
        private final Button bt_cancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_avataar);
            info = itemView.findViewById(R.id.tv_info);
            userName = itemView.findViewById(R.id.tv_user_name);
            bt_accept = itemView.findViewById(R.id.bt_accept);
            bt_cancel = itemView.findViewById(R.id.bt_cancel);
        }
    }


}
