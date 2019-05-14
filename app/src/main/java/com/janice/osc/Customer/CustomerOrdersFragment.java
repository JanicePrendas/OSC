package com.janice.osc.Customer;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Order;
import com.janice.osc.R;
import com.janice.osc.Util.ListAdapter;

import java.util.ArrayList;
import java.util.List;

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
        if(mOrders==null)
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
                .whereEqualTo("customerId",userCustomer.getUid()) //filtramos por sodas equivale a where tipo = soda
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
        if(mOrders.size()>0){
            list.setAdapter(new ListAdapter<Order>(getActivity(),mOrders, CustomerOrdersFragment.this, R.layout.template_pedido));
        }
    }


}
