package com.janice.osc.Soda;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private EditText mEtTitulo;
    private EditText mEtDescripcion;
    private EditText mEtPrecio;
    private TextView mTvImagen;
    private Button mBtnGuardar;
    private Button mBtnBuscarImagen;

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
        mTvImagen = findViewById(R.id.tvImagen);
        mBtnGuardar = findViewById(R.id.btnGuardar);
        mBtnBuscarImagen = findViewById(R.id.btnBuscarImagen);
    }

    private void setViewListeners() {
        mBtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long precio = Long.parseLong(mEtPrecio.getText().toString());
                producto.setPrecio(precio);
                producto.setTitulo(mEtTitulo.getText().toString());
                producto.setDescripcion(mEtDescripcion.getText().toString());

                if (mTvImagen.getText() == "Imagen seleccionada") {
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
                                guaradarPlato();
                            }
                        }
                    });
                } else {
                    guaradarPlato();
                }

            }
        });

        mBtnBuscarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void guaradarPlato(){
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
            mTvImagen.setText(producto.getImg() != null ? "Imagen anterior" : "No hay imagen");
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
            if(mTvImagen.getText().toString().equals("Imagen anterior")){
                borrarImagen();
            }
            uriImagenSeleccionada = data.getData();
            mTvImagen.setText("Imagen seleccionada");
        }
    }

    public void borrarImagen(){
        StorageReference r = FirebaseStorage.getInstance().getReferenceFromUrl(uriImagenSeleccionada.toString());
        r.delete();

    }
}
