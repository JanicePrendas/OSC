package com.janice.osc.Registro;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.janice.osc.R;
import com.janice.osc.Util.SectionsPagerAdapter;

public class RegisterActivity extends AppCompatActivity {
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.register);
        setToolbar();
        setItems();
        setViewPagerAdapter();
        setTabs();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setItems(){
        //...
    }

    private void setTabs(){
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        final Drawable icono1= ContextCompat.getDrawable(this, R.drawable.baseline_person_black_24);
        icono1.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        final Drawable icono2 = ContextCompat.getDrawable(this, R.drawable.baseline_restaurant_black_24);
        icono2.mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);

        tabs.getTabAt(0).setIcon(icono1);
        tabs.getTabAt(1).setIcon(R.drawable.baseline_restaurant_black_24);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        icono1.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        tab.setIcon(icono1);
                        break;
                    default:
                        icono2.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        tab.setIcon(icono2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        icono1.mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
                        tab.setIcon(icono1);
                        break;
                    default:
                        icono2.mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
                        tab.setIcon(icono2);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setViewPagerAdapter(){
        // Setear adaptador al viewpager.
        mViewPager = findViewById(R.id.fragment_container);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RegisterCustomerFragment());
        adapter.addFragment(new RegisterSodaFragment());
        viewPager.setAdapter(adapter);
    }
}
