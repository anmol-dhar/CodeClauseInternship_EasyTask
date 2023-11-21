package com.anmol.easytaskpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;

public class TaskAdapter extends FirestoreRecyclerAdapter<Note, TaskAdapter.TaskViewHolder> {
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        TaskAdapter.context = context;
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

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView titleTextView, contentTextView, timestampTextView;
        ImageButton optionsButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            timestampTextView = itemView.findViewById(R.id.time_text_view);
            optionsButton = itemView.findViewById(R.id.options_button);

            optionsButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showPopMenu(v);
        }

        void showPopMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.options_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.delete_popup) {
                int position = getAdapterPosition();
                String docId = getSnapshots().getSnapshot(position).getId();
                DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document(docId);

                documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
            return false;
        }
    }
}
