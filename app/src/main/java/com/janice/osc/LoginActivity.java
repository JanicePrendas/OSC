package com.janice.osc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.janice.osc.Registro.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private SignInButton mGoogleSignInButton;
    private TextView mTvRegistrar;
    private FirebaseAuth mAuth;
    private EditText mEtCorreo;
    private EditText mEtContrasena;
    private Button mBtnIniciarSesion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide(); //Para quitar el ActionBar
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_login);
        setItems();
        setViewListeners();
    }

    private void setItems(){
        mGoogleSignInButton = findViewById(R.id.sign_in_button);
        mTvRegistrar = findViewById(R.id.tvRegistrar);
        mEtContrasena = findViewById(R.id.password_edittext);
        mEtCorreo = findViewById(R.id.email_edittext);
        mBtnIniciarSesion = findViewById(R.id.signin_button);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setViewListeners(){
        mBtnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticar();
            }
        });

        mTvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
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
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            Toast.makeText(this, "Autenticated.",
                    Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getApplicationContext(), PanelNavegacion.class);
//            startActivity(intent);
        }else{
            Toast.makeText(this, "Not Autenticated.",
                    Toast.LENGTH_LONG).show();
        }

    }
}
