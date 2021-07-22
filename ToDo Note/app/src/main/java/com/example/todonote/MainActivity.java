package com.example.todonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity<notificationHelper> extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NotesRecyclerAdapter.NoteListener {

    private static final String TAG = "MainActivity";
    FloatingActionButton fabMain,fabOne,fabTwo;
    //RecyclerView
    RecyclerView recyclerView;
    NotesRecyclerAdapter notesRecyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Float translationY=100f;
    Boolean isMenuOpen=false;
    OvershootInterpolator interpolator=new OvershootInterpolator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to match the status bar color with layout color
        Window window=MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.purple_500));

        //init Floating Action Button
        initFab();

        recyclerView = findViewById(R.id.recyclerView);

        //check the User is null.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startLoginActivity();
        }
    }

    private void initFab() {
        fabMain=findViewById(R.id.fab);
        fabOne= findViewById(R.id.fab1);
        fabTwo=findViewById(R.id.fab2);

        fabOne.setAlpha(0f);
        fabTwo.setAlpha(0f);

        fabOne.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
            }
        });

        fabOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
                if (isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
            }
        });

        fabTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(MainActivity.this,HistoryActivity.class);
               startActivity(intent);
                if (isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
            }
        });
    }
    private  void openMenu(){
        isMenuOpen=!isMenuOpen;
        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        fabOne.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }
    private void closeMenu(){
        isMenuOpen=!isMenuOpen;
        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }
    private void showProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String profile = user.getEmail();
            View toast = LayoutInflater.from(this).inflate(R.layout.custom_toast, null);
            Toast custom = new Toast(this);
            custom.setView(toast);
            TextView message = toast.findViewById(R.id.userprofile);
            message.setText(profile);
            custom.setDuration(Toast.LENGTH_LONG);
            custom.show();
        }
    }
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    public void handleAddNotes(MenuItem item) {
        Intent intent = new Intent(this, Add_Note.class);
        startActivity(intent);
    }

    public void handleLogoutButton(MenuItem item) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);*/
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme);
        builder.setTitle("LOGOUT")
                .setMessage("Do you really want to exit ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startLoginActivity();
                    }
                }).setNegativeButton("CANCEL", null)
                .create().show();
    }

    private void initRecyclerView(FirebaseUser user) {
        //connect with the Firebase note adapter.
        Query query = FirebaseFirestore.getInstance()
                .collection("TodoList")
                .orderBy("created", Query.Direction.ASCENDING)
                .whereEqualTo("userId", user.getUid());

        //get options into FirebaseRecyclerAdapter.
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesRecyclerAdapter);
        //start Listening
        notesRecyclerAdapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (direction == ItemTouchHelper.LEFT) {
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                NotesRecyclerAdapter.NoteViewHolder noteViewHolder = (NotesRecyclerAdapter.NoteViewHolder) viewHolder;
                noteViewHolder.deleteItem();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder
                , float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.purple_500))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @Override
    public void handleDeleteItem(DocumentSnapshot snapshot) {
        //get document references in snapshot.
        DocumentReference documentReference = snapshot.getReference();
        Note note = snapshot.toObject(Note.class);
        //swipe to delete the document.
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //check updating
                        Log.d(TAG, "Item Deleted..!");
                    }
                });
        //deleted document restoring process using undo keys.
        Snackbar.make(recyclerView, "Item Deleted..!", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        documentReference.set(note);
                    }
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (notesRecyclerAdapter != null) {
            notesRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startLoginActivity();
            return;
        }
        //Rpefresh layouts
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView(firebaseAuth.getCurrentUser());
                notesRecyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        initRecyclerView(firebaseAuth.getCurrentUser());

    }
}