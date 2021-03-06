package com.janice.osc.Soda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.Model.Soda;
import com.janice.osc.NamesActivity;
import com.janice.osc.R;
import com.janice.osc.Util.Util;

public class HomeSoda extends AppCompatActivity {

    private FirebaseFirestore db;
    FirebaseUser currentUser;
    Soda soda;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_products:
                    selectedFragment = new ProductsFragment();
                    break;
                case R.id.nav_orders:
                    selectedFragment = new OrdersFragment();
                    break;
                case R.id.nav_profile_soda:
                    selectedFragment = new ProfileSodaFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_soda);

        setItems();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).commit();
    }

    private void setItems(){
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("usuarios").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        soda = task.getResult().toObject(Soda.class);
                        setTitle("Hola, " + soda.getNombre());
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acerca_de, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_acerca_de:
                Intent i = new Intent(this, NamesActivity.class);
                startActivity(i);
                break;
            case R.id.verVideo:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/KWP6E9sgr70")));
                break;
            case R.id.item_cerrar_sesion:
                Util.logout(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed(){
        Util.logout(this);
    }*/
}
