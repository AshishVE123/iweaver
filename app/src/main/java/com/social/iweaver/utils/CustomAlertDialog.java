package com.social.iweaver.utils;

import static com.social.iweaver.activity.CreatePostActivity.CAMERA_REQUEST;
import static com.social.iweaver.activity.CreatePostActivity.GALLERY_REQUEST;
import static com.social.iweaver.activity.CreatePostActivity.REQUEST_TAKE_GALLERY_VIDEO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.social.iweaver.R;
import com.social.iweaver.activity.CreatePostActivity;
import com.social.iweaver.activity.EditProfileActivity;
import com.social.iweaver.activity.HomeActivity;
import com.social.iweaver.activity.TestTrialActivity;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.viewmodel.LogoutViewModel;

import java.io.File;
import java.io.IOException;

public class CustomAlertDialog {

    public static CustomAlertDialog instance = null;
    private final UserPreference preference;
    private final DatabaseHelper databaseHelper;
    private final Context mContext;

    public CustomAlertDialog(Context mContext) {
        this.mContext = mContext;
        preference = UserPreference.getInstance(mContext);
        databaseHelper = new DatabaseHelper(mContext);
    }

    public static CustomAlertDialog getInstance(Context context) {
        if (instance == null) {
            instance = new CustomAlertDialog(context);
        }

        return instance;
    }

    public void showInfoAlertDialog(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = ((Activity) activity).getLayoutInflater().inflate(R.layout.custon_dialog_layout, null);
        builder.setView(customLayout);

        TextView detailMsg = customLayout.findViewById(R.id.tv_detailMsg);
        TextView btnOkay = customLayout.findViewById(R.id.tv_okayBtn);

        detailMsg.setText(msg);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activity.finish();
            }
        });
        dialog.show();
    }

    public void showLogOutAlertDialog(Activity activity, String msg, LogoutViewModel logoutModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = ((Activity) activity).getLayoutInflater().inflate(R.layout.custom_dialog_logout, null);
        builder.setView(customLayout);

        TextView detailMsg = customLayout.findViewById(R.id.tv_detailMsg);
        TextView btnOkay = customLayout.findViewById(R.id.tv_okay);
        TextView btnCancel = customLayout.findViewById(R.id.tv_cancel);

        detailMsg.setText(msg);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((HomeActivity) activity).initiateLogoutProcess(activity, logoutModel);


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showExitAlertDialog(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = ((Activity) activity).getLayoutInflater().inflate(R.layout.custom_dialog_exit, null);
        builder.setView(customLayout);

        TextView detailMsg = customLayout.findViewById(R.id.tv_detailMsg);
        TextView btnYes = customLayout.findViewById(R.id.tv_yes);
        TextView btnNo = customLayout.findViewById(R.id.tv_no);

        detailMsg.setText(msg);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activity.finishAffinity();


            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showBlockPostAlertDialog(Activity activity, int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = ((Activity) activity).getLayoutInflater().inflate(R.layout.error_post_dialog, null);
        builder.setView(customLayout);

        TextView detailMsg = customLayout.findViewById(R.id.tv_detailMsg);
        TextView btnOkay = customLayout.findViewById(R.id.tv_okay_post);
        TextView btnCancel = customLayout.findViewById(R.id.tv_cancel_post);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((HomeActivity) activity).callBlockPostApi(postId, position);


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void choosePostImage(Activity context) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_chooser_dialog, null);
        dialogBuilder.setView(dialogView);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView camera = (TextView) dialogView.findViewById(R.id.tv_camera);
        TextView gallery = (TextView) dialogView.findViewById(R.id.tv_gallery);
        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                String filename = "photo";
                File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(filename,".jpg", dir);
                    CreatePostActivity.path = image.getAbsolutePath();
                    Uri imageUsri = FileProvider.getUriForFile(context,"com.social.iweaver.fileprovider",image);
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,imageUsri);
                    context.startActivityForResult(takePicture, CAMERA_REQUEST);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                // choose from  external storage
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //  pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                   /* pickPhoto.setType("image/*");
                    pickPhoto.setAction(Intent.ACTION_GET_CONTENT);*/
                context.startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), GALLERY_REQUEST);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.show();
    }

        public void choosePostMedia(Activity context) {
            android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = context.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.image_chooser_dialog, null); // Note: Change the layout name if needed
            dialogBuilder.setView(dialogView);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView camera = (TextView) dialogView.findViewById(R.id.tv_camera);
            TextView gallery = (TextView) dialogView.findViewById(R.id.tv_gallery);
            TextView cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                    String filename = "video";
                    File dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                    try {
                        File video = File.createTempFile(filename, ".mp4", dir);
                        CreatePostActivity.path = video.getAbsolutePath();
                        Uri videoUri = FileProvider.getUriForFile(context, "com.social.iweaver.fileprovider", video);
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivityForResult(takeVideoIntent, CAMERA_REQUEST);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                    // Choose video from external storage
    //                Intent pickVideo = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    //                context.startActivityForResult(Intent.createChooser(pickVideo, "Select Video"), GALLERY_REQUEST);
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    context.startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }


    public void choosePost(Activity context) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_choose_post, null); // Note: Change the layout name if needed
        dialogBuilder.setView(dialogView);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView tv_image = (TextView) dialogView.findViewById(R.id.tv_image);
        TextView tv_video = (TextView) dialogView.findViewById(R.id.tv_video);
        TextView tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);

        tv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePostImage(context);
                dialog.cancel();
            }
        });

        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePostMedia(context);
                dialog.cancel();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void chooseProfileImage(Activity context) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_chooser_dialog, null);
        dialogBuilder.setView(dialogView);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView camera = (TextView) dialogView.findViewById(R.id.tv_camera);
        TextView gallery = (TextView) dialogView.findViewById(R.id.tv_gallery);
        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                String filename = "photo";
                File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(filename,".jpg", dir);
                    EditProfileActivity.path = image.getAbsolutePath();
                    Uri imageUsri = FileProvider.getUriForFile(context,"com.social.iweaver.fileprovider",image);
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,imageUsri);
                    context.startActivityForResult(takePicture, EditProfileActivity.CAMERA_REQUEST);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                // choose from  external storage
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //  pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                   /* pickPhoto.setType("image/*");
                    pickPhoto.setAction(Intent.ACTION_GET_CONTENT);*/
                context.startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), EditProfileActivity.GALLERY_REQUEST);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.show();
    }


    public void showVersionUpdateAlertDialog(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = ((Activity) activity).getLayoutInflater().inflate(R.layout.custom_dialog_logout, null);
        builder.setView(customLayout);

        TextView detailMsg = customLayout.findViewById(R.id.tv_detailMsg);
        TextView btnOkay = customLayout.findViewById(R.id.tv_okay);
        TextView btnCancel = customLayout.findViewById(R.id.tv_cancel);

        detailMsg.setText(msg);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
              mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.social.iweaver")));

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
