package com.janice.osc.Soda;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;

public class AgregarProductoActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private EditText mEtTitulo, mEtDescripcion, mEtPrecio;
    private Button mBtnGuardar;
    private ImageView mAdd_product_imageview;
    private Producto producto;
    private Uri uriImagenSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        setItems();
        setViewListeners();

        cargarDatos();
    }

    private void setItems() {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        producto = new Producto();
        mEtTitulo = findViewById(R.id.etTitulo);
        mEtDescripcion = findViewById(R.id.etDescripcion);
        mEtPrecio = findViewById(R.id.etPrecio);
        mAdd_product_imageview = findViewById(R.id.add_product_imageview);
        mBtnGuardar = findViewById(R.id.btnGuardar);
    }

    private void setViewListeners() {
        mBtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (mTvImagen.getText() == "Imagen seleccionada") {
                if (uriImagenSeleccionada != null) {
                    String img = System.currentTimeMillis() + "." + getFileExtension(uriImagenSeleccionada);

                    final StorageReference ref = mStorageRef.child(img);
                    ref.putFile(uriImagenSeleccionada).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                producto.setImg(downloadUri.toString());
                                validar();
                            }
                        }
                    });
                } else {
                    validar();
                }
            }
        });

        mAdd_product_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void guardarPlato(){
        //Si llega aqui es porque ya se valido que ningun campo venga vacio
        producto.setPrecio(Long.parseLong(mEtPrecio.getText().toString()));
        producto.setTitulo(mEtTitulo.getText().toString());
        producto.setDescripcion(mEtDescripcion.getText().toString());

        DatabaseReference refPlato = null;
        if (producto.getId() == null)
            refPlato = mDatabase.child("platos").push();
        else{
            refPlato = mDatabase.child("platos").child(producto.getId());
        }
        refPlato.setValue(producto);

        Intent i = new Intent(getApplicationContext(), HomeSoda.class);
        startActivity(i);
    }

    private void cargarDatos() {
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            producto.setTitulo(extras.getString("titulo"));
            producto.setId(extras.getString("id"));
            producto.setDescripcion(extras.getString("descripcion"));
            producto.setImg(extras.getString("img"));
            producto.setPrecio(extras.getLong("precio"));

            mEtTitulo.setText(producto.getTitulo());
            mEtDescripcion.setText(producto.getDescripcion());
            mEtPrecio.setText(producto.getPrecio().toString());
            //mTvImagen.setText(producto.getImg() != null ? "Imagen anterior" : "No hay imagen");
            uriImagenSeleccionada = Uri.parse(extras.getString("img"));

            getSupportActionBar().setTitle("Editar Producto");
        }else{
            getSupportActionBar().setTitle("Agregar Producto");
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), 1);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            /*if(mTvImagen.getText().toString().equals("Imagen anterior")){
                borrarImagen();
            }*/

            uriImagenSeleccionada = data.getData();
            //mTvImagen.setText("Imagen seleccionada");
        }
    }

    public void borrarImagen(){
        StorageReference r = FirebaseStorage.getInstance().getReferenceFromUrl(uriImagenSeleccionada.toString());
        r.delete();
    }

    private void validar() {
        // Resetear errores.
        mEtTitulo.setError(null);
        mEtDescripcion.setError(null);
        mEtPrecio.setError(null);

        boolean cancel = false;
        boolean focus = true;
        View focusView = null;

        //Checkear espacios en blanco
        if (uriImagenSeleccionada == null) {
            Toast.makeText(AgregarProductoActivity.this, R.string.required_image, Toast.LENGTH_LONG).show();
            cancel = true;
            focus = false;
        }
        if (TextUtils.isEmpty(mEtTitulo.getText().toString())) {
            mEtTitulo.setError(getString(R.string.error_field_required));
            focusView = mEtTitulo;
            cancel = true;
        }
        if (TextUtils.isEmpty(mEtDescripcion.getText().toString())) {
            mEtDescripcion.setError(getString(R.string.error_field_required));
            focusView = mEtDescripcion;
            cancel = true;
        }
        if (TextUtils.isEmpty(mEtPrecio.getText().toString())) {
            mEtPrecio.setError(getString(R.string.error_field_required));
            focusView = mEtPrecio;
            cancel = true;
        }

        if (cancel) {
            if(focus) {
                focusView.requestFocus(); //Mostrar errores
            }
        } else {
            guardarPlato();
        }
    }
}
