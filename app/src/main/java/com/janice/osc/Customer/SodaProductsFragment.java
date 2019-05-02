package com.janice.osc.Customer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Util.GridAdapter;
import com.janice.osc.Util.Util;
import com.janice.osc.Util.Values;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class SodaProductsFragment extends Fragment {

    private TextView mNombreSoda;
    private Button mOrdenarButton;
    private List<Producto> mProductos;
    private GridViewWithHeaderAndFooter mGrid;
    private String sodaId;
    int total = 0; //Total de productos pedidos por el cliente
    Hashtable<String, Integer> orden; //Id del producto, cantidad
    private FirebaseFirestore db;

    public SodaProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soda_products, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fijar verticalmente
        setItems(view);
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
        mGrid = view.findViewById(R.id.gridview); //Obtención del grid view
        mNombreSoda = view.findViewById(R.id.nombre_soda);
        mOrdenarButton = view.findViewById(R.id.ordenar_button);
        mNombreSoda.setText(Util.nameSodaSelected);
        sodaId = Util.idSodaSelected;
        mProductos = new ArrayList<>();
        orden = new Hashtable<>();
        //To retrieve a number, use the following code:
        /*Integer n = orden.get("two");
        if (n != null) {
            System.out.println("two = " + n);
        }*/
    }

    private void setListeners() {
        mOrdenarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ordenar();
            }
        });
    }

    private void cargarProductos() {
        mProductos = new ArrayList<>(); //Resetear lista de productos para volverla a cargar desde 0
        db.collection("usuarios").document(sodaId)//De la soda actual...
                .collection("productos") //Traigame los productos...
                .whereEqualTo("estado", Values.ACTIVO)
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
            grid.setAdapter(new GridAdapter(getActivity(), productos_sin_plato_principal, SodaProductsFragment.this, R.layout.template_ingrediente_customer));
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

        // Mostrar NumberPicker
        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        numberPicker.requestLayout();//Esta linea es para refrescar la pantalla
        numberPicker.setValue(0); //Por default, siempre comienza en 0...
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                int incremento = (action == ActionEnum.INCREMENT) ? 1 : -1;
                agregarAlPedido(item.getId(), numberPicker.getValue(), incremento);
            }
        });

        return view;
    }

    public void agregarAlPedido(String id_producto, int cantidad, int incremento) {
        total += incremento; //Aqui incrementa o decrementa...
        mOrdenarButton.setEnabled(total > 0); //Habilitamos el boton de ordenar porque el cliente ya ordeno algo
        orden.put(id_producto, cantidad);
    }

    private void ordenar() {

    }
}
