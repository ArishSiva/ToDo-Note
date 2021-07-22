package com.example.todonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements NotesRecyclerAdapter.NoteListener {
    //initialization
    Toolbar myToolbar;
    Spinner mySpinner;
    private static final String TAG = "HistoryActivity";
    FirebaseAuth mAuth;
    TextView date,time,title;
    RecyclerView recyclerView;
    List<String> dataList;
    NotesRecyclerAdapter notesRecyclerAdapter;
    private Query query;
    private FirestoreRecyclerOptions<Note> options;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histry);
        //Merge the toolBar
        Window window = HistoryActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(HistoryActivity.this, R.color.purple_500));
        //declaration
        recyclerView=findViewById(R.id.recyclerView);
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        mySpinner = findViewById(R.id.spinner);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(R.string.history);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //String array adapting
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.month));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                /*Toast.makeText(HistryActivity.this,mySpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();*/
                controlData(mAuth.getCurrentUser());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void controlData(FirebaseUser user) {
        //String user = mAuth.getCurrentUser().getUid();
        String value = mySpinner.getSelectedItem().toString();
        Log.d(TAG, value);
        switch (value) {
            case "All":
                Log.d(TAG, "user Id:" + user);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "January":
                String Jan = String.valueOf(1);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", Jan)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "February":
                String February = String.valueOf(2);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", February)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "March":
                String March = String.valueOf(3);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", March)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "April":
                String April = String.valueOf(4);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", April)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "May":
                String May = String.valueOf(5);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", May)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "June":
                String June = String.valueOf(6);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", June)
                        .whereEqualTo("userId", user.getUid());
                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "July":
                String July = String.valueOf(7);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", July)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "August":
                String August = String.valueOf(8);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month",August)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "September":
                String September = String.valueOf(9);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", September)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "October":
                String October = String.valueOf(10);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", October)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "November":
                String November = String.valueOf(11);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", November)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
            case "December":
                String December = String.valueOf(12);
                query = FirebaseFirestore.getInstance()
                        .collection("TodoList")
                        .orderBy("created", Query.Direction.ASCENDING)
                        .whereEqualTo("month", December)
                        .whereEqualTo("userId", user.getUid());

                //get options into FirebaseRecyclerAdapter.
                options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
                notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(notesRecyclerAdapter);
                //start Listening
                notesRecyclerAdapter.startListening();
                break;
        }

        //Rpefresh layouts
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controlData(mAuth.getCurrentUser());
                notesRecyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void handleDeleteItem(DocumentSnapshot snapshot) {

    }
}