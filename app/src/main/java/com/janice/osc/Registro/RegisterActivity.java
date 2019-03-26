package com.janice.osc.Registro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.janice.osc.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.register);
    }
}
