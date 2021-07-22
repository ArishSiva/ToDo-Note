package com.example.todonote;

import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class NotesRecyclerAdapter extends FirestoreRecyclerAdapter<Note, NotesRecyclerAdapter.NoteViewHolder> {
    private static final String TAG = " NotesRecyclerAdapter";
    NoteListener noteListener;

    public NotesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options,NoteListener noteListener) {
        super(options);
        this.noteListener=noteListener;
    }
    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
        holder.title.setText(model.getTitle());
        holder.date.setText(String.valueOf(model.getDate()));
        holder.time.setText(String.valueOf(model.getTime()));
    }
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // View view = layoutInflater.inflate(R.layout.note_item, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }
    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView title;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.card_date);
            time = itemView.findViewById(R.id.card_time);
            title = itemView.findViewById(R.id.card_title);
        }
        //use MainActivity adapting options.
        public void deleteItem() {
            noteListener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }
    //this interface to use get reference in firebase
    interface NoteListener{
        public void handleDeleteItem(DocumentSnapshot snapshot);
    }
}
