package com.akash.memories.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akash.memories.R;
import com.akash.memories.memories.AddMemories;
import com.akash.memories.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = "CreateAccountActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseUser currentUser;
    private Button buttonSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidName(editTextUserName.getText().toString())){
                    Toast.makeText(CreateAccountActivity.this, "Name should be at leas 3 char's", Toast.LENGTH_SHORT).show();
                }else if (!isValidGmail(editTextEmail.getText().toString())) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(editTextPassword.getText().toString())) {
                    Toast.makeText(CreateAccountActivity.this, "Password must be at least 6 char's", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextUserName.getText().toString());
                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;

    }private boolean isValidName(String name) {
        return name.length() >= 3;
    }

    public static boolean isValidGmail(String email) {
        // Gmail address pattern
        String gmailPattern = "^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*@gmail\\.com$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(gmailPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(email);

        // Check if the email matches the pattern
        return matcher.matches();
    }

    private void createUser(String email, String password, String userName) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName)) {


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isComplete()) {
                        // Sign in success, update UI with the signed-in user's information
                        currentUser = mAuth.getCurrentUser();
                        String userId = null;
                        if (currentUser != null) {
                            userId = currentUser.getUid();
                        }

                        Map<String, String> user = new HashMap<>();
                        user.put("userId", userId);
                        user.put("userName", userName);

                        db.collection("User").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String name = documentSnapshot.getString("userName");
                                        String userId = documentSnapshot.getString("userId");

                                        Intent intent = new Intent(CreateAccountActivity.this, AddMemories.class);

                                        UserApi.getInstance().setUserName(name);
                                        UserApi.getInstance().setUserId(userId);

                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: something went wrong");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e);
                            }
                        });


                    } else {
                        // Error in sign up
                        Toast.makeText(CreateAccountActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e);
                }
            });
        } else {
            Toast.makeText(this, "Please enter all field", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();


    }
}