package com.janice.osc.Customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.janice.osc.R;
import com.janice.osc.Soda.OrdersFragment;
import com.janice.osc.Soda.ProductsFragment;
import com.janice.osc.Soda.ProfileSodaFragment;
import com.janice.osc.Util.SectionsPagerAdapter;

public class HomeCustomer extends AppCompatActivity {

    ViewPager mViewPager;

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
        adapter.addFragment(new ProductsFragment());
        adapter.addFragment(new OrdersFragment());
        adapter.addFragment(new ProfileSodaFragment());
        viewPager.setAdapter(adapter);
    }

}
