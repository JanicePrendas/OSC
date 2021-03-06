package com.janice.osc.Registro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.janice.osc.R;
import com.janice.osc.Util.Util;

import java.util.HashMap;
import java.util.Map;

public class RegisterSodaFragment extends Fragment {

    private EditText mNombre_edittext, mEmail_edittext, mTelefono_edittext, mDireccion_edittext, mContrasena_edittext, mConfirmar_contrasena_edittext;
    private Button mRegister_button, mCancelarUbic_button, mConfirmarUbic_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatActivity mActivity;
    private View mFocusView;
    private boolean mCancel;
    private double mLatitud;
    private double mLongitud;
    private Dialog mDialog;
    LocationManager locationManager;
    public RegisterSodaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_soda, container, false);
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
        //ImageView MiImageView = view.findViewById(R.id.ubicacion);
        /*MiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestra_dialogo_mapa();
            }
        });*/

        mDireccion_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestra_dialogo_mapa();
            }
        });
    }

    private void setItems(View view) {
        mNombre_edittext = view.findViewById(R.id.nombre_soda_edittext);
        mTelefono_edittext = view.findViewById(R.id.telefono_edittext);
        mDireccion_edittext = view.findViewById(R.id.direccion_edittext);
        mEmail_edittext = view.findViewById(R.id.email_edittext);
        mContrasena_edittext = view.findViewById(R.id.contrasena_edittext);
        mConfirmar_contrasena_edittext = view.findViewById(R.id.confirmar_contrasena_edittext);
        mRegister_button = view.findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mActivity = (AppCompatActivity) getActivity();
        mDireccion_edittext.setFocusable(false);
        mDireccion_edittext.setClickable(true);
        mDialog = null;
        mLatitud = 0;
        mLongitud = 0;
        mDireccion_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestra_dialogo_mapa();
            }
        });
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getUbicacion();
        }
    }

    private boolean validate() {
        mCancel = false;
        mFocusView = null;
        validate_edittext(mNombre_edittext);
        validate_edittext(mEmail_edittext);
        validate_edittext(mTelefono_edittext);
        validate_edittext(mDireccion_edittext);
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

        mAuth.createUserWithEmailAndPassword(email, password) //Es metodo crea un usuario sin necesidad de que este confirme su email
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Se ha creado el usuario exitosamente
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Ahora creamos el objeto soda con sus atributos
                            Map<String, Object> nueva_soda = new HashMap<>();
                            nueva_soda.put("id", user.getUid());
                            nueva_soda.put("nombre", mNombre_edittext.getText().toString());
                            nueva_soda.put("latitud", mLatitud+"");
                            nueva_soda.put("longitud", mLongitud+"");
                            nueva_soda.put("telefono", mTelefono_edittext.getText().toString());
                            nueva_soda.put("tipo", "soda");

                            // Agregamos un nuevo documento a la colección usuarios con un ID generado automaticamente por el authentication
                            db.collection("usuarios").document(user.getUid())
                                    .set(nueva_soda)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                            Util.updateUI(user, mActivity);
                        } else {
                            Toast.makeText(mActivity, "Registration failed: " + task.getException(), Toast.LENGTH_LONG).show();
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

    private void muestra_dialogo_mapa() {
        final Dialog dialog = new Dialog(getActivity());
        mDialog = dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /////make map clear
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.alert_dialog_map);////your custom content
        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LatLng posisiabsen = new LatLng(9.998301, -84.117022); ////your lat lng
                if (mLatitud != 0 && mLongitud != 0) {
                    LatLng pos = new LatLng(mLatitud, mLongitud);
                    googleMap.addMarker(new MarkerOptions().position(pos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                }
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(point));
                        mLatitud = point.latitude;
                        mLongitud = point.longitude;
                    }
                });
            }
        });
        mConfirmarUbic_button = dialog.findViewById(R.id.confirm_location_button);
        mCancelarUbic_button = dialog.findViewById(R.id.cancel_location_button);
        mConfirmarUbic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLatitud != 0 && mLongitud != 0) {
                    mDireccion_edittext.setText("Ubicación Confirmada");
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Debe seleccionar una ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCancelarUbic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void getUbicacion() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                setLatitudyLongitud(location.getLatitude(),location.getLongitude());
            } else  if (location1 != null) {
                setLatitudyLongitud(location1.getLatitude(),location1.getLongitude());

            } else  if (location2 != null) {
                setLatitudyLongitud(location2.getLatitude(),location2.getLongitude());
            }else{ //No se encontro la ubicacion de ninguna forma...
                setLatitudyLongitud(0,0);
                Toast.makeText(getActivity(), "No se logró obtener la ubicación actual", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void alertaNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("Por favor activa el GPS")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void setLatitudyLongitud(double lat, double lang){
        mLatitud = lat;
        mLongitud = lang;
    }
}
