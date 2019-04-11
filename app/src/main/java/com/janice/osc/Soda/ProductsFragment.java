package com.janice.osc.Soda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.janice.osc.Model.Producto;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;
import com.janice.osc.Util.GridAdapter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class ProductsFragment extends Fragment {

    private FloatingActionButton mfloatAB;
    private ListView mLvMenu;
    private Spinner mSpinnerVer;
    private List<Producto> mProductos;
    private ArrayAdapter<Producto> mAdapter;
    private GridViewWithHeaderAndFooter mGrid;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        setItems(view);
        setListeners();
        cargarProductos();
        return view;
    }

    private void setItems(View view) {
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mfloatAB = view.findViewById(R.id.agregar_float_button);
        mSpinnerVer = view.findViewById(R.id.spinner_ver);
        mLvMenu = view.findViewById(R.id.productos_listview);
        mProductos = new ArrayList<>();
        //TODO: Luego reemplazar este plato principal quemado por uno de a deveras :v
        mProductos.add(new Producto("Plato del Dia", "Casado con carne", "", (long) 3500));

        //mAdapter = new MyListAdapter();
        //mLvMenu.setAdapter(mAdapter);
        //registerForContextMenu(mLvMenu);
        mGrid = view.findViewById(R.id.gridview); //Obtención del grid view
        CargarSpinner();
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
        if(mProductos.size()>0){
            grid.addHeaderView(createHeaderView(0, mProductos)); //El plato principal siempre estara en la primera posicion
            grid.setAdapter(new GridAdapter(getActivity(), mProductos));
        }
    }

    /**
     * Crea un view de cabecera para mostrarlo en el principio del grid view.
     *
     * @param position Posición del item que sera el grid view dentro de {@code items}
     * @param items    Array de productos
     * @return Header View
     */
    private View createHeaderView(int position, List<Producto> items) {
        View view;
        //TODO: Debuggear mas porque se cae al cambiar de pestañas
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.template_plato_del_dia, null, false);

        Producto item = items.get(position);

        //TODO Seteando Imagen
        ImageView image = (ImageView) view.findViewById(R.id.imagen);
        //Glide.with(image.getContext()).load(item.getIdThumbnail()).into(image);

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








    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Producto p = mProductos.get(info.position);
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getContext(), "Hay que editar este producto...", Toast.LENGTH_LONG).show();
                /*
                Intent intento = new Intent(getActivity(), AgregarProductoActivity.class);
                Bundle paquete = new Bundle();
                paquete.putString("img", p.getImg());
                paquete.putString("titulo", p.getTitulo());
                paquete.putString("descripcion", p.getDescripcion());
                paquete.putLong("precio",p.getPrecio());
                intento.putExtras(paquete);
                startActivity(intento);
                */
                break;
            case 2:
                Toast.makeText(getContext(), "Hay que borrar este producto...", Toast.LENGTH_LONG).show();
                //borrarProducto(p.getId());
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Administrar Producto");
        menu.add(0, 1, 0, "Editar");
        menu.add(0, 2, 0, "Borrar");
    }

    public void borrarProducto(String id) {
        Toast.makeText(getContext(), "Hay que borrar este producto...", Toast.LENGTH_LONG).show();
    }

    private class MyListAdapter extends ArrayAdapter<Producto> {
        public MyListAdapter() {
            super(ProductsFragment.this.getActivity(), R.layout.template_soda_product, mProductos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = ProductsFragment.this.getActivity().getLayoutInflater().inflate(R.layout.template_soda_product, parent, false);
            }

            Producto productoActual = mProductos.get(position);
            ImageView ivProducto = itemView.findViewById(R.id.imagen);
            Picasso.get()
                    .load(productoActual.getImg())
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivProducto);
            TextView tvTitulo = itemView.findViewById(R.id.plato);
            tvTitulo.setText(productoActual.getTitulo());

            TextView tvDescripcion = itemView.findViewById(R.id.nombre);
            tvDescripcion.setText(productoActual.getDescripcion());

            TextView tvPrecio = itemView.findViewById(R.id.precio);
            DecimalFormat df = new DecimalFormat("₡###,###.###");
            tvPrecio.setText(df.format(productoActual.getPrecio()));

            return itemView;
        }
    }

    private void sendToAgregarProductoActivity() {
        Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
        intent.putExtra("sodaID", mUser.getUid());
        startActivity(intent);
    }
}
