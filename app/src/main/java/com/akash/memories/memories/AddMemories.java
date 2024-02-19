package com.akash.memories.memories;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.memories.R;
import com.akash.memories.login.LoginActivity;
import com.akash.memories.model.PostModel;
import com.akash.memories.util.CustomAdapter;
import com.akash.memories.util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddMemories extends AppCompatActivity {
    public static final String TAG = "AddMemories";
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private ArrayList<PostModel> postModelArrayList;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memories);

        db = FirebaseFirestore.getInstance();
        floatingActionButton = findViewById(R.id.floatingActionButton);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setSupportActionBar(toolbar);
        currentUser = mAuth.getCurrentUser();
        db.collection("User").whereEqualTo("userId", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "onEvent: " + error);
                } else {
                    if (value != null) {
                        for (QueryDocumentSnapshot snapshot : value) {
                            UserApi.getInstance().setUserName(snapshot.getString("userName"));
                        }
                    } else {
                        Log.d(TAG, "onEvent: QuerySnapshot value is null");
                    }

                }
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddMemories.this, CreateMemory.class));
            }
        });

        String userName = UserApi.getInstance().getUserName();
        String userId = UserApi.getInstance().getUserId();

        Log.d(TAG, "onCreate: user Name " + userName + " user id : " + userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signoutMenu) {
            signout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AddMemories.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        postModelArrayList = new ArrayList<>();

        db.collection("Posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    PostModel postModel = snapshot.toObject(PostModel.class);
                    postModelArrayList.add(postModel);
                }
                customAdapter = new CustomAdapter(AddMemories.this, postModelArrayList);
                recyclerView.setAdapter(customAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e);
            }
        });
    }
}