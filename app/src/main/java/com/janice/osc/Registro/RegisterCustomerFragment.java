package com.janice.osc.Registro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.janice.osc.R;

public class RegisterCustomerFragment extends Fragment {

    private EditText mNombre_edittext;
    private EditText mEmail_edittext;
    private EditText mContrasena_edittext;
    private EditText mConfirmar_contrasena_edittext;
    private Button mRegister_button;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public RegisterCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register_customer, container, false);

        setItems(view);
        setViewListeners(view);

        return view;
    }

    public void setViewListeners(View view){
        mRegister_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    registrar();
                }
            }
        });
    }

    private void setItems(View view){
        mNombre_edittext = view.findViewById(R.id.nombre_edittext);
        mEmail_edittext = view.findViewById(R.id.email_edittext);
        mContrasena_edittext = view.findViewById(R.id.contrasena_edittext);
        mConfirmar_contrasena_edittext = view.findViewById(R.id.confirmar_contrasena_edittext);
        mRegister_button = view.findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://osc-app-a1dc6.firebaseio.com/").getReference("usuarios");
    }

    private boolean validate() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            Toast.makeText(getActivity(), "Autenticated.",
                    Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getApplicationContext(), PanelNavegacion.class);
//            startActivity(intent);
        }else{
            Toast.makeText(getActivity(), "Not Autenticated.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void registrar() {
        String email =mEmail_edittext.getText().toString();
        String password =mContrasena_edittext.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference dbusuario = mDatabase.child(user.getUid());
                            dbusuario.child("nombre").setValue(mNombre_edittext.getText().toString());
                            updateUI(user);
                        } else {
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }



}
