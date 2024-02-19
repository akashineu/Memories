package com.akash.memories.memories;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.akash.memories.R;
import com.akash.memories.model.PostModel;
import com.akash.memories.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateMemory extends AppCompatActivity {
    public static final String TAG = "CreateMemory";

    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageViewCamera;
    private ImageView imageViewPhoto;
    private EditText editTextCaption;
    private EditText editTextDescription;
    private Button buttonPost;
    private ProgressBar progressBar;
    private Uri selectedImageUri;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memory);

        imageViewCamera = findViewById(R.id.imageViewCamera);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        editTextCaption = findViewById(R.id.editTextTextCaption);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPost = findViewById(R.id.buttonPost);
        progressBar = findViewById(R.id.progressBar);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    // If the device has a camera, ask the user whether to use the camera or gallery
                    showImageSourceDialog();
                } else {
                    // If no camera is available, open the gallery directly
                    openGallery();
                }
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataInFirebaseDatabase();
                Log.d(TAG, "onClick: button post clicked");

            }
        });
    }


    private void addDataInFirebaseDatabase() {
        //Todo add data in database and set the visibility of progress bar as INVISIBLE
        //get all the things want to store in database

        String caption = editTextCaption.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();


        Log.d(TAG, "addDataInFirebaseDatabase: " + UserApi.getInstance().getUserName());

        if (!TextUtils.isEmpty(caption) && !TextUtils.isEmpty(description) && selectedImageUri != null) {
            progressBar.setVisibility(View.VISIBLE);

            Log.d(TAG, "addDataInFirebaseDatabase: user added all fields");

            //Add image in firebase storage
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images").child("my_image_" + Timestamp.now().getSeconds());
            imagesRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: kept the file in storage");
                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            db.collection("Posts").add(new PostModel(uri.toString(), caption, description, UserApi.getInstance().getUserName(), System.currentTimeMillis())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Log.d(TAG, "onSuccess: saving to db : " + uri + ", " + caption + ", " + description + ", " + UserApi.getInstance().getUserName());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(CreateMemory.this, AddMemories.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onFailure: " + e);
                }
            });

            //Add postModel in db

//            db.collection("Posts").add(new PostModel(selectedImageUri, caption, description, UserApi.getInstance().getUserName()));

        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);

    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Camera option selected
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        // Gallery option selected
                        openGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the selected image, for example, get its URI.
            selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: " + selectedImageUri);

            // Now you can use 'selectedImageUri' or load the image, etc.

            imageViewPhoto.setImageURI(selectedImageUri);
            imageViewCamera.setVisibility(View.INVISIBLE);
        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();

            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            // Now you can use 'imageBitmap' or save it, etc.

            // Convert Bitmap to Uri (assuming you have a method for this)
            if(imageBitmap != null){
                selectedImageUri = bitmapToUri(imageBitmap);
                Log.d(TAG, "selectedImageUri: " + selectedImageUri);
            }
            imageViewPhoto.setImageURI(selectedImageUri);
            imageViewCamera.setVisibility(View.INVISIBLE);
        }

    }
    private Uri bitmapToUri(Bitmap bitmap) {
        // You need to implement this method based on your requirements.
        // Here is a simple example:
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}