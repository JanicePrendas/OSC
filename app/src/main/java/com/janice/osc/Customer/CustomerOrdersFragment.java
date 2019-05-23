package com.janice.osc.Customer;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Order;
import com.janice.osc.Model.Producto;
import com.janice.osc.Payment.PaymentsUtil;
import com.janice.osc.R;
import com.janice.osc.Util.ListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerOrdersFragment extends Fragment {

    private List<Order> mOrders;
    private ListView listView;
    private FirebaseFirestore db;
    private final FirebaseUser userCustomer = FirebaseAuth.getInstance().getCurrentUser();


    public CustomerOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_orders, container, false);
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


        listView = view.findViewById(R.id.listViewOrdersCustomer); //Obtención de la lista
        mOrders = new ArrayList<>();
    }

    private void setListeners() {
    }

    private void cargarPedidos() {
        mOrders = new ArrayList<>(); //Resetear lista de sodas para volverla a cargar desde 0
        db.collection("ordenes")
                .whereEqualTo("customerId", userCustomer.getUid()) //filtramos por sodas equivale a where tipo = soda
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
            list.setAdapter(new ListAdapter<Order>(getActivity(), mOrders, CustomerOrdersFragment.this, R.layout.template_pedido));
        }
    }

    public void mostrarDetallesOrden(Order orden) {
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
        confirm_button.getLayoutParams().height = 0;
        cancel_button.getLayoutParams().height = 0;

        total.setText(String.format("%s %d", getString(R.string.simbolo_colones), orden.getTotal()));
        setUpListViewDelPedido(lista_productos_pedido, orden);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpListViewDelPedido(ListView list, Order orden) {
        if (orden.getProductos().size() > 0) {
            list.setAdapter(new ListAdapter<Producto>(getActivity(), orden.getProductos(), null, R.layout.template_producto_pedido));
        }
    }




}
