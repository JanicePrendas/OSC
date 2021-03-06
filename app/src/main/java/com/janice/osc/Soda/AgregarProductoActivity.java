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
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Util.Values;
import com.squareup.picasso.Picasso;

public class AgregarProductoActivity extends AppCompatActivity {

    private EditText mEtTitulo, mEtDescripcion, mEtPrecio;
    private Switch mSwitch_activate;
    private Button mBtnGuardar;
    private ImageView mAdd_product_imageview;
    private Producto producto;
    private String sodaID;
    private String id_ultimo_producto;
    private int accion = Values.AGREGAR;
    private Uri uriImagenSeleccionada = null;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private UploadTask mUploadTask; //Objeto para administrar el estado de cargar la imagen en el Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
        setItems();
        cargarDatos();
        setViewListeners();
        escogerTitulo();
    }

    @Override
    public boolean onSupportNavigateUp() { //Para que funciona flecha back en el toolbar
        onBackPressed();
        return true;
    }

    private void setItems() {
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("img_productos"); //Referencio a la carpeta img_productos para guardar las imagenes de productos
        producto = new Producto();
        mSwitch_activate = findViewById(R.id.switch_activate);
        mEtTitulo = findViewById(R.id.etTitulo);
        mEtDescripcion = findViewById(R.id.etDescripcion);
        mEtPrecio = findViewById(R.id.etPrecio);
        mAdd_product_imageview = findViewById(R.id.add_product_imageview);
        mBtnGuardar = findViewById(R.id.btnGuardar);

        //Para habilitar flecha back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void cargarDatos() {
        sodaID = getIntent().getStringExtra("sodaID");
        id_ultimo_producto = getIntent().getStringExtra("id_ultimo_producto");

        Bundle objetoRecibido = getIntent().getExtras();
        if (objetoRecibido != null) {
            if(objetoRecibido.getSerializable("producto_para_editar") != null){
                accion = Values.EDITAR;
                producto = (Producto) objetoRecibido.getSerializable("producto_para_editar");
                setDatosDelProductoAEditar();
            }
        }
    }

    private void setViewListeners() {
        if(accion == Values.AGREGAR){ //Solo puede cargar una imagen para productos nuevos. Al editar, no puede cambiar la imagen
            mAdd_product_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });
        }
        mBtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });
    }

    private void setDatosDelProductoAEditar(){
        mSwitch_activate.setChecked(producto.getEstado_cantidad()==Values.ACTIVO);
        mEtTitulo.setText(producto.getTitulo());
        mEtDescripcion.setText(producto.getDescripcion());
        mEtPrecio.setText(String.valueOf(producto.getPrecio()));
        uriImagenSeleccionada = Uri.parse(producto.getImg());
        Glide.with(mAdd_product_imageview.getContext()).load(producto.getImg()).into(mAdd_product_imageview);
        if (producto.getId().equals("0")) //Es el plato principal
            mSwitch_activate.setClickable(false);
    }

    private void escogerTitulo(){
        if(accion==Values.AGREGAR){
            setTitle("Agregar Producto");
            if(id_ultimo_producto.equals("-1"))
                setTitle("Agregar Plato Principal");
        }
        else{
            setTitle("Editar Producto");
            if(producto.getId().equals("0"))
                setTitle("Editar Plato Principal");
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Values.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Values.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImagenSeleccionada = data.getData();
            Picasso.get().load(uriImagenSeleccionada).fit().centerCrop().into(mAdd_product_imageview);
        }
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
            if(accion == Values.AGREGAR) //Si es un nuevo producto, debe subir una nueva imagen
                subirImagen();
            else //Si va a editar un producto, no puede cambiar la imagen
                guardarProducto(producto.getImg());
        }
    }

    private void guardarProducto(String imagen_url){
        //Si llega aqui es porque ya se valido que ningun campo venga vacio y ya se escogio una imagen
        producto.setTitulo(mEtTitulo.getText().toString());
        producto.setDescripcion(mEtDescripcion.getText().toString());
        producto.setImg(imagen_url);
        producto.setEstado_cantidad(mSwitch_activate.isChecked() ? Values.ACTIVO : Values.INACTIVO);
        producto.setPrecio(Long.parseLong(mEtPrecio.getText().toString()));
        if(accion == Values.AGREGAR) //Solo se setea el id cuando es un nuevo producto
            producto.setId(String.valueOf(Integer.valueOf(id_ultimo_producto)+1));
        setProducto(producto);
    }

    private void setProducto(Producto p){
        //Creamos nuevo documento dentro de la subcoleccion productos de la soda (Si ya existe, le cae encima)
        db.collection("usuarios").document(sodaID)
                .collection("productos").document(producto.getId())
                .set(p);
        if(accion == Values.AGREGAR)
            Toast.makeText(AgregarProductoActivity.this, R.string.product_added, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(AgregarProductoActivity.this, R.string.product_updated, Toast.LENGTH_LONG).show();
        sendToHomeSoda();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void subirImagen() {
        if (uriImagenSeleccionada != null) {
            final StorageReference fileReference =
                    mStorageRef.child( //Meto la nueva imagen en la carpeta img_productos
                            System.currentTimeMillis() + "." + getFileExtension(uriImagenSeleccionada)  //Esto es para obtener una direccion de tipo "path/to/images/rivers.jpg"
                    );
            mUploadTask = fileReference.putFile(uriImagenSeleccionada);

            // Register observers to listen for when the download is done or if it fails
            mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    obtenerImagenRecienSubida(fileReference);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Error al subir archivo...
                }
            });
        } else {
            Toast.makeText(this, R.string.required_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerImagenRecienSubida(final StorageReference ref){
        Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imagen_url = downloadUri.toString(); // Obtener URL de la imagen subida a Firebase Storage
                    guardarProducto(imagen_url);
                } else {
                    // Fallo en obtener imagen recien subida
                }
            }
        });
    }

    private void sendToHomeSoda() {
        Intent intent = new Intent(getApplicationContext(), HomeSoda.class);
        startActivity(intent);
        finish();
    }
}
