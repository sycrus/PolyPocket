package com.joe.market.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.joe.market.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joe.market.R;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int CHOOSE_IMAGE = 101;

    EditText editTextDisplayName;
    TextView displayEmail, displayUID;
    ImageView displayImage;
    String mDisplayName;

    Uri uriProfileImage;
    String profileImageUrl;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayImage = (ImageView) findViewById(R.id.display_image);
        editTextDisplayName = (EditText) findViewById(R.id.edit_text_display_name);

        displayUID = (TextView) findViewById(R.id.display_UID);
        displayEmail = (TextView) findViewById(R.id.display_email);

        //btnLogOut = (Button) findViewById(R.id.btnLogOut);
        //btnSaveChanges = (Button) findViewById(R.id.btnSaveChanges);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnLogOut).setOnClickListener(this);
        findViewById(R.id.btnProceed).setOnClickListener(this);
        findViewById(R.id.btnSaveChanges).setOnClickListener(this);

        findViewById(R.id.display_image).setOnClickListener(this);
        findViewById(R.id.edit_text_display_name).setOnClickListener(this);
        loadUserInformation();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.display_image:
                changeUserPic();
                break;
            case R.id.btnSaveChanges:
                saveChanges();
                break;
            case R.id.btnLogOut:
                logOutUser();
                break;
            case R.id.btnProceed:
                proceed();
                break;
        }
    }
    public void savePic() {
        //upload everything to database
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            if (profileImageUrl != null) {
                ;
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(profileImageUrl))
                        .build();

                user.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
    public void saveName() {
        //upload everything to database
        FirebaseUser user = mAuth.getCurrentUser();
        mDisplayName = editTextDisplayName.getText().toString().trim();

        if (user != null) {
            if (mDisplayName != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mDisplayName)
                        .build();

                user.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
    public void saveChanges() {
        //upload everything to database
        saveName();
        savePic();
        Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    public void logOutUser() {
        mAuth.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(),LogInActivity.class));

    }

    public void proceed() {
        finish();
        startActivity(new Intent(getApplicationContext(), com.joe.market.poly.MainActivity.class));
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getEmail() != null) {
                displayEmail.setText(user.getEmail());
            }
            if (user.getPhotoUrl() != null) {
               Glide.with(this)
                       .load(user.getPhotoUrl().toString())
                       .placeholder(R.drawable.default_profile)
                       .into(displayImage);
            }
            if (user.getDisplayName() != null) {
                editTextDisplayName.setText(user.getDisplayName());
            } else {
                editTextDisplayName.setText(user.getUid());
            }
            if (user.getUid() != null) {
                displayUID.setText(user.getUid());
            }

        }
    }

    public void changeUserPic() {
        showImageChooser();
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                displayImage.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            //Toast.makeText(getApplicationContext(), "Success uploading pic", Toast.LENGTH_SHORT).show();
                            profileImageUrl = downloadUrl.toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
