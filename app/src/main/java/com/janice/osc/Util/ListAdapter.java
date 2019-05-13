package com.janice.osc.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Customer.CustomerOrdersFragment;
import com.janice.osc.Customer.SodaProductsFragment;
import com.janice.osc.Customer.SodasFragment;
import com.janice.osc.Model.Customer;
import com.janice.osc.Model.Order;
import com.janice.osc.Model.Producto;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;
import com.janice.osc.Soda.OrdersFragment;
import com.janice.osc.Soda.ProductsFragment;

import java.util.List;

public class ListAdapter<T> extends BaseAdapter {
    Context mContext;
    Fragment fragment;
    List<T> items;
    int layoutId;


    public ListAdapter(Context mContext, List<T> items, Fragment fragment, int layout ) {
       this.mContext = mContext;
       this.fragment = fragment;
       this.layoutId = layout;
       this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // Make sure we have a view to work with (may have been given null)
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, viewGroup, false);
        }

        if (fragment instanceof SodasFragment)
            setViewSodas(view, position);
        else if(fragment instanceof SodaProductsFragment)
            setViewSodaProducts(view, position);
        else if(fragment instanceof OrdersFragment) {
            setViewSodaOrders(view, position);
        }
        else if(fragment instanceof CustomerOrdersFragment){
            setViewCustomerOrders(view, position);
        }
        return view;
    }


    private void setViewSodas(View view, int position){
        final Soda item = (Soda) getItem(position);

        // Seteando Nombre
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getNombre());

        // Seteando Direccion
        //TextView correo = (TextView) view.findViewById(R.id.correo);
        //correo.setText(String.format("Email: %s", /*item.getDireccion()*/item.getCorreo()));

        // Seteando Telefono
        TextView numTelefonico = (TextView) view.findViewById(R.id.telefono);
        numTelefonico.setText(String.format("Teléfono: %s", item.getTelefono()));

        // Setear el mapa
        MapView map = (MapView) view.findViewById(R.id.mapView2);
        setViewMap(map, position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((SodasFragment) fragment).verSoda(item);
            }
        });

    }

    private void setViewSodaProducts(View view, int position){
        final Producto item = (Producto) getItem(position);
        int item_cantidad = item.getEstado_cantidad();

        // Seteando Cantidad
        TextView cantidad = (TextView) view.findViewById(R.id.cantidad);
        cantidad.setText(String.format("%d%s",item_cantidad,"x"));

        // Seteando Titulo
        TextView titulo = (TextView) view.findViewById(R.id.titulo);
        titulo.setText(item.getTitulo());

        // Seteando Precio
        TextView precio = (TextView) view.findViewById(R.id.precio);
        precio.setText(String.format("%s%d",mContext.getString(R.string.simbolo_colones),item.getPrecio()*item_cantidad));

        // Seteando Descripcion
        TextView descripcion = (TextView) view.findViewById(R.id.descripcion);
        descripcion.setText(item.getDescripcion());
    }

    private void setViewMap(MapView mMapView, int position){
        // Aqui se setea el mapa.
        try {
            final Soda soda = (Soda) items.get(position);
            MapsInitializer.initialize(mContext);
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    if (!soda.getLatitud().equals("0") && !soda.getLongitud().equals("0")) {
                        LatLng pos = new LatLng(Double.parseDouble(soda.getLatitud()), Double.parseDouble(soda.getLongitud()));
                        googleMap.addMarker(new MarkerOptions().position(pos));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    }
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                }
            });
        }catch(Exception ex){
            System.out.print(ex.getMessage());
            String a = ex.getMessage();
        }
    }

    private void setViewSodaOrders(final View view, int position){
        final Order item = (Order) getItem(position);

        FirebaseFirestore.getInstance().collection("usuarios").document(item.getCustomerId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Customer c = document.toObject(Customer.class);
                                TextView tvNombre = view.findViewById(R.id.name);
                                tvNombre.setText(c.getNombre());

                                TextView tvPayment = view.findViewById(R.id.payment);
                                tvPayment.setText("Efectivo");

                                TextView tvEstado = view.findViewById(R.id.estado);
                                tvEstado.setText(Values.valueName(item.getEstado()));

                                TextView tvPrecio = view.findViewById(R.id.precio);
                                tvPrecio.setText(item.getTotal()+"");
                            } else {
                                Log.d("LV ORDERS SODA", "No such document");
                            }
                        }
                    }
                });
    }

    private void setViewCustomerOrders(final View view, int position){
        final Order item = (Order) getItem(position);

        FirebaseFirestore.getInstance().collection("usuarios").document(item.getSodaId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Soda s = document.toObject(Soda.class);
                                TextView tvNombre = view.findViewById(R.id.name);
                                tvNombre.setText(s.getNombre());

                                TextView tvPayment = view.findViewById(R.id.payment);
                                tvPayment.setText("Efectivo");

                                TextView tvEstado = view.findViewById(R.id.estado);
                                tvEstado.setText(Values.valueName(item.getEstado()));

                                TextView tvPrecio = view.findViewById(R.id.precio);
                                tvPrecio.setText(item.getTotal()+"");
                            } else {
                                Log.d("LV ORDERS SODA", "No such document");
                            }
                        }
                    }
                });
    }
}
