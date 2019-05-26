package com.janice.osc.Soda;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Order;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Util.ListAdapter;
import com.janice.osc.Util.Util;
import com.janice.osc.Util.Values;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    private List<Order> mOrders;
    private ListView listView;
    private FirebaseFirestore db;
    private final FirebaseUser userSoda = FirebaseAuth.getInstance().getCurrentUser();

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fijar verticalmente
        setItems(view);
        setListeners();
        cargarPedidos();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOrders == null)
            cargarPedidos();
    }

    private void setItems(View view) {
        db = FirebaseFirestore.getInstance();
        listView = view.findViewById(R.id.listViewOrdersSoda); //Obtención de la lista
        mOrders = new ArrayList<>();
    }

    private void setListeners() {
    }

    private void cargarPedidos() {
        mOrders = new ArrayList<>(); //Resetear lista de sodas para volverla a cargar desde 0
        db.collection("ordenes")
                .whereEqualTo("sodaId", userSoda.getUid()) //filtramos por sodas equivale a where tipo = soda
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Order pedidoObtenido = document.toObject(Order.class);
                                mOrders.add(pedidoObtenido);
                            }
                            //Mostrar los objetos en el List View
                            setUpListView(listView); //Inicializar el List view
                        }
                    }
                });
    }

    /**
     * Infla el list view del fragmento dependiendo de la sección
     *
     * @param list Instancia de  la lista
     */

    private void setUpListView(ListView list) {
        if (mOrders.size() > 0) {
            list.setAdapter(new ListAdapter<Order>(getActivity(), mOrders, OrdersFragment.this, R.layout.template_pedido));
        }
    }

    public void mostrarDetallesOrden(final Order orden) {
        if (orden.getEstado() == Values.COMPLETO)
            mostrarOrdenCompleta(orden);
        else
            mostrarOrdenPendiente(orden);
    }

    private void mostrarOrdenCompleta(final Order orden) {
        //Mostramos alert dialog con el recibo
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_recibo_show, null);
        builder.setView(viewAux);

        //Atributos de la vista del AlertDialog.Builder
        TextView order = viewAux.findViewById(R.id.order);
        order.setText(R.string.orden);
        ListView lista_productos_pedido = viewAux.findViewById(R.id.lista_productos_pedido);
        TextView total = viewAux.findViewById(R.id.total);
        ImageView icono_pago = viewAux.findViewById(R.id.icono_pago);
        TextView tipo_de_pago = viewAux.findViewById(R.id.tipo_de_pago);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        if(/*orden.getTipoPago() == Values.GOOGLE_PAY*/false){
            icono_pago.setImageDrawable(getActivity().getDrawable(R.drawable.googlepaymark));
            tipo_de_pago.setText(R.string.googlepay_button_content_description);
        }

        total.setText(String.format("%s %d", getString(R.string.simbolo_colones), orden.getTotal()));
        setUpListViewDelPedido(lista_productos_pedido, orden);

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void mostrarOrdenPendiente(final Order orden) {
        //Mostramos alert dialog con el recibo
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_recibo, null);
        builder.setView(viewAux);

        //Atributos de la vista del AlertDialog.Builder
        TextView order = viewAux.findViewById(R.id.order);
        order.setText(R.string.orden);
        ListView lista_productos_pedido = viewAux.findViewById(R.id.lista_productos_pedido);
        TextView total = viewAux.findViewById(R.id.total);
        ImageView icono_pago = viewAux.findViewById(R.id.icono_pago);
        TextView tipo_de_pago = viewAux.findViewById(R.id.tipo_de_pago);
        Button confirm_button = viewAux.findViewById(R.id.confirm_button);
        Button cancel_button = viewAux.findViewById(R.id.cancel_button);
        confirm_button.setText(R.string.complete);

        if(/*orden.getTipoPago() == Values.GOOGLE_PAY*/false){
            icono_pago.setImageDrawable(getActivity().getDrawable(R.drawable.googlepaymark));
            tipo_de_pago.setText(R.string.googlepay_button_content_description);
        }

        total.setText(String.format("%s %d", getString(R.string.simbolo_colones), orden.getTotal()));
        setUpListViewDelPedido(lista_productos_pedido, orden);

        final AlertDialog alert = builder.create();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                completarOrden(orden.getId());
                alert.dismiss();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void setUpListViewDelPedido(ListView list, Order orden) {
        if (orden.getProductos().size() > 0) {
            list.setAdapter(new ListAdapter<Producto>(getActivity(), orden.getProductos(), null, R.layout.template_producto_pedido));
        }
    }

    private void completarOrden(String ordenID) {
        db.collection("ordenes").document(ordenID)
                .update("estado", Values.COMPLETO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Orden completada con exito", Toast.LENGTH_LONG).show();
                        cargarPedidos(); //Para refrescar pantalla
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al completar orden", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
