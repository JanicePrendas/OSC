package com.janice.osc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {

    SignInButton mGoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setItems();
    }

    private void setItems(){
        mGoogleSignInButton = findViewById(R.id.sign_in_button);
    }

    private void setGoogleButtonText(){
        try {
            ((TextView) mGoogleSignInButton.getChildAt(0)).setText(R.string.signin);
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
