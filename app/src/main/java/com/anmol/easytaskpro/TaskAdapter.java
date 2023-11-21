package com.anmol.easytaskpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TaskAdapter extends FirestoreRecyclerAdapter<Note, TaskAdapter.TaskViewHolder> {
    Context context;

    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Note note) {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNoteScreen.class);
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);
            String docId = TaskAdapter.this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);

            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, contentTextView, timestampTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            timestampTextView = itemView.findViewById(R.id.time_text_view);
        }
    }
}
