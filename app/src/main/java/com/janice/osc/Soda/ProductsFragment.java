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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.janice.osc.Model.Producto;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ProductsFragment extends Fragment {

    private FloatingActionButton mfloatAB;
    private ListView mLvMenu;
    private Spinner mSpinnerVer;

    private List<Producto> mProductos;
    private ArrayAdapter<Producto> mAdapter;
    private List<String> mIds;

    private StorageReference mStorageRef;

    private FirebaseUser mUser;

    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        setItems(view);
        setViewListeners();
        CargarSpinner();

        return view;
    }

    private void CargarSpinner() {
        final String[] options = {"Todos","Activos","Inactivos"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);

        mSpinnerVer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //cambio de seleccion
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSpinnerVer.setAdapter(adapter);
    }// fin de CargarSpinner

    private void setViewListeners() {
        mfloatAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAgregarProductoActivity();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Producto p = mProductos.get(info.position);
        switch (item.getItemId()) {
            case 1:
                Intent intento = new Intent(getActivity(), AgregarProductoActivity.class);
                Bundle paquete = new Bundle();
                paquete.putString("id", p.getId());
                paquete.putString("img", p.getImg());
                paquete.putString("titulo", p.getTitulo());
                paquete.putString("descripcion", p.getDescripcion());
                paquete.putLong("precio",p.getPrecio());
                intento.putExtras(paquete);
                startActivity(intento);
                break;
            case 2:
                borrarProducto(p.getId());
                break;
            default:
                break;
        }
        return true;
    }

    private void setItems(View view) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mfloatAB = view.findViewById(R.id.agregar_float_button);
        mSpinnerVer = view.findViewById(R.id.spinner_ver);
        mLvMenu = view.findViewById(R.id.productos_listview);
        mProductos = new ArrayList<>();
        mIds = new ArrayList<>();
        mAdapter = new MyListAdapter();
        mLvMenu.setAdapter(mAdapter);
        registerForContextMenu(mLvMenu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Administrar Producto");
        menu.add(0, 1, 0, "Editar");
        menu.add(0, 2, 0, "Borrar");
    }

    public void borrarProducto(String id){
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

    private void sendToAgregarProductoActivity(){
        Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
        startActivity(intent);
    }
}
