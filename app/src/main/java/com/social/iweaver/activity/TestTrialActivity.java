package com.social.iweaver.activity;

import static com.social.iweaver.activity.CreatePostActivity.CAMERA_REQUEST;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.social.iweaver.R;

import java.io.File;
import java.io.IOException;

public class TestTrialActivity extends AppCompatActivity {
 String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_trial);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = "photo";
                File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(filename,".jpg", dir);
                    path = image.getAbsolutePath();
                    Uri imageUsri = FileProvider.getUriForFile(TestTrialActivity.this,"com.social.iweaver.fileprovider",image);
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,imageUsri);
                    startActivityForResult(takePicture, 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            ImageView image = findViewById(R.id.image);
            image.setRotation(90.0f);
            image.setImageBitmap(bitmap);

        }
    }
}