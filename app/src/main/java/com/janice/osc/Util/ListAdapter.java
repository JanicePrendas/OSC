package com.janice.osc.Util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
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
import com.janice.osc.Customer.SodasFragment;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;

import java.util.List;
import java.util.Map;

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


        return view;
    }


    private void setViewSodas(View view, int position){
        final Soda item = (Soda) getItem(position);

        // Seteando Nombre
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getNombre());

        // Seteando Direccion
        TextView correo = (TextView) view.findViewById(R.id.correo);
        correo.setText(String.format("Email: %s", /*item.getDireccion()*/item.getCorreo()));

        // Seteando Precio
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
}
