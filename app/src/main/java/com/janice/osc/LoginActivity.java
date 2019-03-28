package com.janice.osc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide(); //Para quitar el ActionBar
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_login);
        setItems();
    }

    private void setItems(){
        //Poner aquí todos los findViewById...
    }
}
