package com.janice.osc;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janice.osc.Util.Util;
import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setItems();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isAutenticated()){
                    sendtoLogin();
                }
            }
        }, SPLASH_TIME_OUT );

    }

    private void setItems(){
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isAutenticated(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Util.updateUI(currentUser, SplashActivity.this);
            return true;
        }
        return false;
    }

    private void sendtoLogin(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
