package com.janice.osc.Soda;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Util.GridAdapter;
import com.janice.osc.Util.Values;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class ProductsFragment extends Fragment {

    private FloatingActionButton mfloatAB;
    private Spinner mSpinnerVer;
    private List<Producto> mProductos;
    private GridViewWithHeaderAndFooter mGrid;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fijar verticalmente
        setItems(view);
        CargarSpinner();
        setListeners();
        cargarProductos();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProductos == null)
            cargarProductos();
    }

    private void setItems(View view) {
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mfloatAB = view.findViewById(R.id.agregar_float_button);
        mSpinnerVer = view.findViewById(R.id.spinner_ver);
        mGrid = view.findViewById(R.id.gridview); //Obtención del grid view
        mProductos = new ArrayList<>();
    }

    private void CargarSpinner() {
        final String[] options = {"Todos", "Activos", "Inactivos"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        mSpinnerVer.setAdapter(adapter);
    }// fin de CargarSpinner

    private void setListeners() {
        mfloatAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAgregarProductoActivity();
            }
        });

        mSpinnerVer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //cambio de seleccion
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarProductos() {
        mProductos = new ArrayList<>(); //Resetear lista de productos para volverla a cargar desde 0
        //TODO: Luego reemplazar este plato principal quemado por uno de a deveras :v
        mProductos.add(new Producto("Plato del Dia", "Casado con carne", "", Values.ACTIVO, (long) 3500, "A_Plato del dia"));
        db.collection("usuarios").document(mUser.getUid())//De la soda actual...
                .collection("productos") //Traigame los productos...
                .get() //Vamos al get de una vez (sin el where) porque quiero todos los productos de la soda
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mProductos.add(document.toObject(Producto.class));
                            }
                            //Mostrar los objetos en el Grid
                            setUpGridView(mGrid); //Inicializar el grid view
                        }
                    }
                });
    }

    /**
     * Infla el grid view del fragmento dependiendo de la sección
     *
     * @param grid Instancia del grid view
     */
    private void setUpGridView(GridViewWithHeaderAndFooter grid) {
        if (mProductos.size() > 0) {
            grid.addHeaderView(createHeaderView(mProductos.get(0))); //El plato principal siempre estara en la primera posicion
            List<Producto> productos_sin_plato_principal = mProductos; //Siempre hay que enviar la lista sin el plato principal al Adapter
            productos_sin_plato_principal.remove(0);
            //grid.setAdapter(new GridAdapter(getActivity(),productos_sin_plato_principal, this));
            grid.setAdapter(new GridAdapter(getActivity(), productos_sin_plato_principal, ProductsFragment.this));
        }
    }

    /**
     * Crea un view de cabecera para mostrarlo en el principio del grid view.
     *
     * @return Header View
     */
    private View createHeaderView(Producto item) {
        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.template_plato_del_dia, null, false);

        //Seteando Imagen
        ImageView image = (ImageView) view.findViewById(R.id.imagen);
        if (!item.getImg().equals("")) //Solo seteamos una imagen si el objeto trae una. Si no trae ninguna, se queda con la imagen default del layout
            Glide.with(image.getContext()).load(item.getImg()).into(image);

        // Seteando Titulo
        TextView name = (TextView) view.findViewById(R.id.titulo);
        name.setText(item.getTitulo());

        // Seteando Descripción
        TextView descripcion = (TextView) view.findViewById(R.id.descripcion);
        descripcion.setText(item.getDescripcion());

        // Seteando Precio
        TextView precio = (TextView) view.findViewById(R.id.precio);
        precio.setText(String.format("₡ %s", item.getPrecio().toString()));

        return view;
    }

    private void sendToAgregarProductoActivity() {
        Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
        intent.putExtra("sodaID", mUser.getUid());
        startActivity(intent);
    }

    public void borrarProducto(Producto producto) {
        borrarImagenDelProducto(producto.getImg());
        db.collection("usuarios").document(mUser.getUid())
                .collection("productos").document(producto.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cargarProductos();
                        Toast.makeText(getActivity(), "Producto borrado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al borrar producto", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void borrarImagenDelProducto(String url) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Imagen borrada", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Error al borrar imagen", Toast.LENGTH_LONG).show();
            }
        });

    }
}
