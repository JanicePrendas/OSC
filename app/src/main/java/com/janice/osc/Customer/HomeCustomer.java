package com.janice.osc.Customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.Model.Customer;
import com.janice.osc.R;
import com.janice.osc.Soda.OrdersFragment;
import com.janice.osc.Soda.ProductsFragment;
import com.janice.osc.Soda.ProfileSodaFragment;
import com.janice.osc.Util.SectionsPagerAdapter;

public class HomeCustomer extends AppCompatActivity {

    private FirebaseFirestore db;
    FirebaseUser currentUser;
    ViewPager mViewPager;
    Customer cliente;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_products:
                    return true;
                case R.id.nav_orders:
                    return true;
                case R.id.nav_profile_soda:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        setItems();
        setViewPagerAdapter();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setViewPagerAdapter(){
        // Setear adaptador al viewpager.
        mViewPager = findViewById(R.id.fragment_container);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SodasFragment());
        adapter.addFragment(new CustomerOrdersFragment());
        adapter.addFragment(new CustomerOrdersFragment());
        viewPager.setAdapter(adapter);
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
                        cliente = task.getResult().toObject(Customer.class);
                        setTitle("Hola, " + cliente.getNombre());
                    }
                }
            }
        });
    }
}
