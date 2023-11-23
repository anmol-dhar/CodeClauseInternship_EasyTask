package com.anmol.easytaskpro;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {

    static CollectionReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("Tasks")
                .document(currentUser.getUid()).collection("My_Tasks");
    }

    static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd MMM, yyyy "+ " hh:mm aa").format(timestamp.toDate());
    }

}
