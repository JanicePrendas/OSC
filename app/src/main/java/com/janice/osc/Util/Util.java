package com.janice.osc.Util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.janice.osc.Home;

public class Util {

    public static String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public static void updateUI(FirebaseUser user, final AppCompatActivity activity) {
        if (user != null) {
            Toast.makeText(activity, "Autenticated.",
                    Toast.LENGTH_LONG).show();
            String uid = user.getUid();
            Intent i = new Intent(activity, Home.class);
            activity.startActivity(i);
//            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://osc-app-a1dc6.firebaseio.com/").getReference("usuarios");
//            mDatabase.child(uid).child("tipo").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String tipo = dataSnapshot.getValue(String.class);
//                    if (tipo.equals("soda")) {
//                        Intent i = new Intent(activity, Home.class);
//                        activity.startActivity(i);
//                    }
//                }
//
//            @Override
//            public void onCancelled (@NonNull DatabaseError databaseError){
//
//            }
//        });
        } else {
            Toast.makeText(activity, "Not Autenticated.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
