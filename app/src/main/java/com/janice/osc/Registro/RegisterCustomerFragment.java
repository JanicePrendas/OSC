package com.janice.osc.Registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.R;
import com.janice.osc.Util.Util;

import java.util.HashMap;
import java.util.Map;

public class RegisterCustomerFragment extends Fragment {

    private EditText mNombre_edittext, mEmail_edittext, mContrasena_edittext, mConfirmar_contrasena_edittext;
    private Button mRegister_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatActivity mActivity;
    private View mFocusView;
    private boolean mCancel;

    public RegisterCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_customer, container, false);
        setItems(view);
        setViewListeners(view);
        return view;
    }

    public void setViewListeners(View view) {
        mRegister_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    registrar();
                }
            }
        });
    }

    private void setItems(View view) {
        mNombre_edittext = view.findViewById(R.id.nombre_edittext);
        mEmail_edittext = view.findViewById(R.id.email_edittext);
        mContrasena_edittext = view.findViewById(R.id.contrasena_edittext);
        mConfirmar_contrasena_edittext = view.findViewById(R.id.confirmar_contrasena_edittext);
        mRegister_button = view.findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mActivity = (AppCompatActivity) getActivity();
    }

    private boolean validate() {
        mCancel = false;
        mFocusView = null;
        validate_edittext(mNombre_edittext);
        validate_edittext(mEmail_edittext);
        validate_edittext(mContrasena_edittext);
        validate_edittext(mConfirmar_contrasena_edittext);
        valida_contrasena();
        return !mCancel;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Util.updateUI(currentUser, mActivity);
    }

    public void registrar() {
        String email = mEmail_edittext.getText().toString();
        String password = mContrasena_edittext.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Se ha creado el usuario exitosamente
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Ahora creamos el objeto cliente con sus atributos
                            Map<String, Object> nueva_cliente = new HashMap<>();
                            nueva_cliente.put("nombre", mNombre_edittext.getText().toString());
                            nueva_cliente.put("tipo", "cliente");

                            // Agregamos un nuevo documento a la colección usuarios con un ID generado automaticamente
                            db.collection("usuarios").document(user.getUid())
                                    .set(nueva_cliente)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {}
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("REGISTER SODA", e.getMessage());
                                        }
                                    });
                            Util.updateUI(user, mActivity);
                        } else {
                            Toast.makeText(mActivity, "Registration failed.\n" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
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

    private void valida_contrasena() {
        if (mContrasena_edittext.getText().toString().length() < 6) {
            mContrasena_edittext.setError("La contraseña debe contener al menos 6 dígitos");
            if (!mCancel)
                mCancel = true;
            if (mFocusView == null)
                mFocusView = mContrasena_edittext;
        } else {
            if (!mContrasena_edittext.getText().toString().equals(mConfirmar_contrasena_edittext.getText().toString())) {
                mConfirmar_contrasena_edittext.setError("Las contraseñas no coinciden");
                if (!mCancel)
                    mCancel = true;
                if (mFocusView == null)
                    mFocusView = mConfirmar_contrasena_edittext;
            }
        }
    }
}
