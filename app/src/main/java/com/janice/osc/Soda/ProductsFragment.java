package com.janice.osc.Soda;

import android.app.AlertDialog;
import android.content.Context;
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
    private boolean ya_hay_plato_principal;
    private List<Producto> mProductos;
    private GridViewWithHeaderAndFooter mGrid;
    private View plato_del_dia;
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
        //cargarProductos();
        setListeners();
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
        plato_del_dia = null;
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
                mProductos.clear(); // Limpio la lista.
                switch (position) {
                    case 1:
                        filterProducts(Values.ACTIVO);
                        break;
                    case 2:
                        filterProducts(Values.INACTIVO);
                        break;
                    default:
                        AllProducts();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterProducts(final int estado) {
        mProductos.clear(); // limpio los productos que tengo al momento.
        db.collection("usuarios")
                .document(mUser.getUid())
                .collection("productos")
                .whereEqualTo("estado_cantidad", estado)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mProductos.add(document.toObject(Producto.class));
                    }
                    setUpGridView(mGrid,estado==Values.INACTIVO); //Inicializar el grid view
                }
            }
        });
    }

    private void AllProducts() {
        mProductos.clear(); // limpio los productos que tengo al momento
        db.collection("usuarios").document(mUser.getUid())
                .collection("productos")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mProductos.add(document.toObject(Producto.class));
                    }
                    setUpGridView(mGrid,false); //Inicializar el grid view
                }
            }
        });
    }

    private void cargarProductos() {
        AllProducts();
        /*switch (mSpinnerVer.getSelectedItemPosition()) {
            case 1:
                filterProducts(Values.ACTIVO);
                break;
            case 2:
                filterProducts(Values.INACTIVO);
                break;
            default:
                AllProducts();
                break;
        }*/
    }

    /**
     * Infla el grid view del fragmento dependiendo de la sección
     *
     * @param grid Instancia del grid view
     */
    private void setUpGridView(GridViewWithHeaderAndFooter grid, boolean solo_inactivos) {
        //Limpiar el Header del GridView
        if(plato_del_dia != null)
            mGrid.removeHeaderView(plato_del_dia);
        mGrid.setAdapter(null);
        plato_del_dia = null;

        if (mProductos.size() > 0) {
            if(solo_inactivos){ //Muestro todos los productos de la lista sin Header
                grid.setAdapter(new GridAdapter<Producto>(getActivity(), mProductos, ProductsFragment.this, R.layout.template_ingrediente_soda));
            }
            else{ //De fijo si viene el plato principal...
                if(plato_del_dia == null){
                    plato_del_dia = createHeaderView(mProductos.get(0));
                    grid.addHeaderView(plato_del_dia); //El plato principal siempre estara en la primera posicion
                }


                List<Producto> productos_sin_plato_principal = new ArrayList<>(mProductos); //Siempre hay que enviar la lista sin el plato principal al Adapter
                productos_sin_plato_principal.remove(0);
                grid.setAdapter(new GridAdapter<Producto>(getActivity(), productos_sin_plato_principal, ProductsFragment.this, R.layout.template_ingrediente_soda));
            }
        }
    }

    /**
     * Crea un view de cabecera para mostrarlo en el principio del grid view.
     *
     * @return Header View
     */
    private View createHeaderView(final Producto item) {
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

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenuPlatoPrincipal(item);
                return true;
            }
        });

        return view;
    }

    private void showMenuPlatoPrincipal(final Producto producto) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_edit, null);
        builder.setView(viewAux);

        TextView edit = viewAux.findViewById(R.id.edit);//atributo de la vista del AlertDialog.Builder

        final AlertDialog alert = builder.create();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendToEditarProductoActivity(producto);
            }
        });
        alert.show();
    }

    private void sendToAgregarProductoActivity() {
        Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
        intent.putExtra("sodaID", mUser.getUid());
        if (mProductos.isEmpty())
            intent.putExtra("id_ultimo_producto", "-1");
        else
            intent.putExtra("id_ultimo_producto", mProductos.get(mProductos.size() - 1).getId());
        startActivity(intent);
    }

    public void sendToEditarProductoActivity(Producto producto) {
        Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
        intent.putExtra("sodaID", mUser.getUid());
        Bundle bundle = new Bundle();
        bundle.putSerializable("producto_para_editar", producto);
        intent.putExtras(bundle);
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
