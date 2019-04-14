package com.janice.osc.Soda;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.Customer.SodasFragment;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSodaFragment extends Fragment {

    private EditText mNombre_edittext, mEmail_edittext, mTelefono_edittext, mDireccion_edittext, mContrasena_edittext, mNueva_contrasena_edittext;
    private Button mUpdate_button;
    private AppCompatActivity mActivity;
    private FirebaseFirestore db;
    private boolean mCancel;
    private View mFocusView;
    private final FirebaseUser userSoda = FirebaseAuth.getInstance().getCurrentUser();
    private Soda soda;


    public ProfileSodaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_soda, container, false);

        setItems(view);
        cargarDatos();
        setViewListeners(view);

        return view;
    }

    public void setViewListeners(View view) {
        mUpdate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    updateCustomerInformation();
                }
            }
        });
    }

    public void cargarDatos() {
        DocumentReference reference = db.collection("usuarios").document(userSoda.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                soda = documentSnapshot.toObject(Soda.class);
                mNombre_edittext.setText(soda.getNombre());
                mTelefono_edittext.setText(soda.getTelefono());
                mDireccion_edittext.setText(soda.getDireccion());
                mEmail_edittext.setText(userSoda.getEmail());
            }
        });
    }

    private boolean validate() {
        mCancel = false;
        mFocusView = null;
        validate_edittext(mNombre_edittext);
        validate_edittext(mEmail_edittext);
        validate_edittext(mTelefono_edittext);
        validate_edittext(mDireccion_edittext);
        validate_edittext(mContrasena_edittext);
        valida_contrasena();
        return !mCancel;
    }

    public void updateCustomerInformation() {
        AuthCredential credential = EmailAuthProvider
                .getCredential(userSoda.getEmail(),mContrasena_edittext.getText().toString());
        userSoda.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if (!userSoda.getEmail().equals(mEmail_edittext.getText().toString()))
                        userSoda.updateEmail(mEmail_edittext.getText().toString());
                    if(!mNueva_contrasena_edittext.getText().toString().equals(""))
                        userSoda.updatePassword(mNueva_contrasena_edittext.getText().toString());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("nombre", mNombre_edittext.getText().toString());
                    updates.put("direccion", mDireccion_edittext.getText().toString());
                    updates.put("telefono", mTelefono_edittext.getText().toString());
                    db.collection("usuarios").document(userSoda.getUid())
                            .update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mActivity.getSupportActionBar().setTitle("Hola, "+mNombre_edittext.getText().toString());
                                    Toast.makeText(mActivity, "Información actualizada", Toast.LENGTH_LONG);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileSodaFragment()).commit();
                                }
                            });
                }else{
                    mContrasena_edittext.setError("Contraseña incorrecta");
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

    private void setItems(View view) {
        mNombre_edittext = view.findViewById(R.id.nombre_soda_edittext);
        mTelefono_edittext = view.findViewById(R.id.telefono_edittext);
        mDireccion_edittext = view.findViewById(R.id.direccion_edittext);
        mEmail_edittext = view.findViewById(R.id.email_edittext);
        mUpdate_button = view.findViewById(R.id.update_button);
        mContrasena_edittext = view.findViewById(R.id.contrasena_edittext);
        mNueva_contrasena_edittext = view.findViewById(R.id.nueva_contrasena_edittext);
        db = FirebaseFirestore.getInstance();
        mActivity = (AppCompatActivity) getActivity();
    }

    private void valida_contrasena() {
        String contra = mNueva_contrasena_edittext.getText().toString();
        if (contra.length() > 0 && contra.length() < 6) {
            mNueva_contrasena_edittext.setError("La contraseña debe contener al menos 6 dígitos");
            if (!mCancel)
                mCancel = true;
            if (mFocusView == null)
                mFocusView = mContrasena_edittext;
        }
    }

}
