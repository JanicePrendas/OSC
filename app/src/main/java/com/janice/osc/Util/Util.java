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
import com.janice.osc.Customer.HomeCustomer;
import com.janice.osc.Soda.HomeSoda;

public class Util {

    public static void updateUI(FirebaseUser user, final AppCompatActivity activity) {
        if (user != null) {
            Toast.makeText(activity, "Autenticated.",
                    Toast.LENGTH_LONG).show();
            String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://osc-app-a1dc6.firebaseio.com/").getReference("usuarios");
            mDatabase.child(uid).child("tipo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String tipo = dataSnapshot.getValue(String.class);
                    if (tipo.equals("soda")) {
                        Intent i = new Intent(activity, HomeSoda.class);
                        activity.startActivity(i);
                    }else{
                        Intent i = new Intent(activity, HomeCustomer.class);
                        activity.startActivity(i);
                    }
                }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }
        });
        } else {
            Toast.makeText(activity, "Not Autenticated.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
