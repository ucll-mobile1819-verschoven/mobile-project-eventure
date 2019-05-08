package com.ucll.eventure;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

public class SelectImage extends AppCompatActivity implements IPickResult {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimage);
        PickImageDialog.build(new PickSetup().setTitle(getString(R.string.unpp))).setOnPickCancel(new IPickCancel() {
            @Override
            public void onCancelClick() {
                goBack();
            }
        }).show(getSupportFragmentManager());
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            User me = new UserDatabase(getApplicationContext()).readFromFile();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilePictures")
                    .child(me.getDatabaseID()).child("profile_picture.jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            r.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    goBack();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_LONG).show();
                            goBack();
                        }
                    });

                }
            });

        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goBack(){
        Intent i = new Intent(SelectImage.this, SettingsActivity.class);
        startActivity(i);
        finish();
    }
}
