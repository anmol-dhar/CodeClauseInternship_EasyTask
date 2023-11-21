package com.anmol.easytaskpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class AddNoteScreen extends AppCompatActivity {

    EditText title, content;
    Button createTask;
    ImageButton closeButton;
    TextView pageTitleText;
    String editTitle, editContent, docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_screen);

        title = findViewById(R.id.titleText);
        content = findViewById(R.id.description);
        createTask = findViewById(R.id.createTask);
        closeButton = findViewById(R.id.closeButton);
        pageTitleText = findViewById(R.id.newTaskTitle);

        //Getting data from TaskAdapter to edit the text
        editTitle = getIntent().getStringExtra("title");
        editContent = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        title.setText(editTitle);
        content.setText(editContent);
        if(isEditMode){
            pageTitleText.setText("Edit  task");
            createTask.setText("Edit");
        }

        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void saveNote() {
        String noteTitle = title.getText().toString();
        String noteContent = content.getText().toString();

        if(noteTitle == null || noteTitle.isEmpty()){
            title.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else{
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Failed while adding task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}