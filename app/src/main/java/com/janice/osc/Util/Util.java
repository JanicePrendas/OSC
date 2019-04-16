package com.janice.osc.Util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.Customer.HomeCustomer;
import com.janice.osc.LoginActivity;
import com.janice.osc.Soda.HomeSoda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

    public static String idSodaSelected = "";

    public static void logout(final AppCompatActivity app){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(app);
        builder1.setMessage("Estás seguro de cerrar sesión?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(app, LoginActivity.class);
                        app.startActivity(i);

                    } });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {} });
        AlertDialog alert11 = builder1.create();
        alert11.show();



    }

    public static void updateUI(FirebaseUser user, final AppCompatActivity activity) {
        if (user != null) {
            //Toast.makeText(activity, "Autenticated.", Toast.LENGTH_LONG).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String id = user.getUid();
            DocumentReference reference =  db.collection("usuarios").document(user.getUid());
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot result = task.getResult();
                            Intent i;
                            if(result.get("tipo").equals("soda")){
                                i = new Intent(activity, HomeSoda.class);
                            }
                            else{
                                i = new Intent(activity, HomeCustomer.class);
                            }
                            activity.startActivity(i);
                            activity.finish();
                        }else {//Error obteniendo el documento
                            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
        else {
           // Toast.makeText(activity, "Not Autenticated.", Toast.LENGTH_LONG).show();
        }
    }

    public static String DeInputStreamAString(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {sb.append(line+"\n");}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {br.close();}
                catch (IOException e)
                {e.printStackTrace();}
            }
        }
        return sb.toString();
    }
}
