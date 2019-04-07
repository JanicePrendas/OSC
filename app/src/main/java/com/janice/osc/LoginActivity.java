package com.janice.osc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janice.osc.Registro.RegisterActivity;
import com.janice.osc.Util.Util;

public class LoginActivity extends AppCompatActivity {

    private TextView mTvRegistrar;
    private FirebaseAuth mAuth;
    private EditText mEtCorreo;
    private EditText mEtContrasena;
    private Button mBtnIniciarSesion;
    private View mFocusView;
    private boolean mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide(); //Para quitar el ActionBar
        } catch (NullPointerException e) {}
        setContentView(R.layout.activity_login);
        setItems();
        setViewListeners();
    }

    private void setItems() {
        mTvRegistrar = findViewById(R.id.tvRegistrar);
        mEtContrasena = findViewById(R.id.password_edittext);
        mEtCorreo = findViewById(R.id.email_edittext);
        mBtnIniciarSesion = findViewById(R.id.signin_button);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setViewListeners() {
        mBtnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar()) {
                    autenticar();
                }
            }
        });

        mTvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });

    }

    public void autenticar() {
        mAuth.signInWithEmailAndPassword(mEtCorreo.getText().toString(), mEtContrasena.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Authentication good.",
                                    Toast.LENGTH_SHORT).show();
                            Util.updateUI(user, LoginActivity.this); //Para mandar al usuario a su home correspondiente
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Util.updateUI(null, LoginActivity.this);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Util.updateUI(currentUser, LoginActivity.this);
    }


    public boolean validar() {
        mCancel = false;
        mFocusView = null;
        validate_edittext(mEtCorreo);
        validate_edittext(mEtContrasena);
        return !mCancel;
    }

    private void validate_edittext(EditText e) {
        e.setError(null);
        if (TextUtils.isEmpty(e.getText().toString())) {
            e.setError("Este campo es requerido");
            if (!mCancel)
                mCancel = true;
            if (mFocusView == null)
                mFocusView = e;
        }
    }

    private void sendToRegister(){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
