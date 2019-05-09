package com.janice.osc.Customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.janice.osc.Model.Order;
import com.janice.osc.Util.ListAdapter;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class SodaProductsFragment extends Fragment {

    private TextView mNombreSoda;
    private Button mOrdenarButton;
    private List<Producto> mProductos;
    private GridViewWithHeaderAndFooter mGrid;
    private String sodaId;
    private int total_prod = 0; //Total de productos pedidos por el cliente
    private int monto_total = 0;
    private List<Producto> orden;
    private FirebaseFirestore db;
    private AlertDialog recibo;

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
        orden = new ArrayList<>();
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
                .whereEqualTo("estado_cantidad", Values.ACTIVO)
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
            List<Producto> productos_sin_plato_principal = new ArrayList<>(mProductos); //Siempre hay que enviar la lista sin el plato principal al Adapter
            productos_sin_plato_principal.remove(0);
            grid.setAdapter(new GridAdapter<Producto>(getActivity(), productos_sin_plato_principal, SodaProductsFragment.this, R.layout.template_ingrediente_customer));
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
        precio.setText(String.format("%s %s", getString(R.string.simbolo_colones), item.getPrecio().toString()));

        // Mostrar NumberPicker
        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        numberPicker.requestLayout();//Esta linea es para refrescar la pantalla
        numberPicker.setValue(0); //Por default, siempre comienza en 0...
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                //int incremento = (action == ActionEnum.INCREMENT) ? 1 : -1;
                //agregarAlPedido(item, numberPicker.getValue(), incremento);
                if(action == ActionEnum.INCREMENT){
                    agregarAlPedido(item, numberPicker.getValue());
                }
                else{
                    removerDelPedido(item, numberPicker.getValue());
                }
            }
        });

        return view;
    }

    public void agregarAlPedido(Producto producto_escogido, int cantidad/*, int incremento*/) {
        /*total_prod += incremento; //Aqui incrementa o decrementa...
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar porque el cliente ya ordeno algo
        orden.add(new Producto(producto_escogido, cantidad));*/

        boolean es_nuevo = true;
        total_prod++;
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar cuando el cliente ya ordeno algo
        monto_total += producto_escogido.getPrecio();

        for(Producto p : orden) { //Recorremos la lista de ordenes para ver si este producto ya estaba...
            if(p.getId().equals(producto_escogido.getId())){
                p.setEstado_cantidad(cantidad);
                es_nuevo = false;
            }
        }

        if(es_nuevo)
            orden.add(new Producto(producto_escogido, cantidad));
    }

    public void removerDelPedido(Producto producto_para_remover, int cantidad) {
        total_prod--; //Decrementa...
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar cuando el cliente ya ordeno algo
        monto_total -= producto_para_remover.getPrecio();
        /*Iterator<Producto> i = orden.iterator();
        while (i.hasNext()) {
            Producto siguiente = i.next(); // must be called before you can call i.remove()
            if(i.getId().equals(producto_para_remover.getId())){
                if(cantidad>0){
                    i.setEstado_cantidad(cantidad);
                }
                else{
                    i.remove(p);
                }
            }
        }*/


        for(Producto p : orden) {
            if(p.getId().equals(producto_para_remover.getId())){
                if(cantidad>0){
                    p.setEstado_cantidad(cantidad);
                }
                else{
                    orden.remove(p); //TODO: Si se cae, cambiar al iterador :v
                }
            }
        }
    }

    private void ordenar() {
        //Mostramos alert dialog con el recibo
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_recibo, null);
        builder.setView(viewAux);

        //Atributos de la vista del AlertDialog.Builder
        ListView lista_productos_pedido = viewAux.findViewById(R.id.lista_productos_pedido);
        TextView total = viewAux.findViewById(R.id.total);
        Button confirm_button = viewAux.findViewById(R.id.confirm_button);
        Button cancel_button = viewAux.findViewById(R.id.cancel_button);

        total.setText(String.format("%s %d", getString(R.string.simbolo_colones), monto_total));
        setUpListViewDelPedido(lista_productos_pedido);

        recibo = builder.create();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                confirmarOrden();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                recibo.dismiss();
            }
        });

        recibo.show();
    }

    /**
     * Infla el list view del fragmento dependiendo de la sección
     *
     * @param list Instancia de  la lista
     */

    private void setUpListViewDelPedido(ListView list) {
        if(orden.size()>0){
            list.setAdapter(new ListAdapter<Producto>(getActivity(), orden, SodaProductsFragment.this, R.layout.template_producto_pedido));
        }
    }

    private void confirmarOrden() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Order nueva_orden = new Order(sodaId, customerId, Values.PENDIENTE ,orden, monto_total);
        db.collection("ordenes").add(nueva_orden) //Guardar en la BD
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Orden realizada con éxito", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al ordenar", Toast.LENGTH_LONG).show();
                    }
                });
        recibo.dismiss(); //Cerrar recibo
    }

}
