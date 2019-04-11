package com.janice.osc.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;

import java.util.List;


public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Producto> items;

    public GridAdapter(Context c, List<Producto>  items) {
        mContext = c;
        this.items = items;
    }

    @Override
    public int getCount() {
        // Decremento en 1, para no contar el header view
        return items.size() - 1;
    }

    @Override
    public Producto getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_ingrediente, viewGroup, false);
        }

        Producto item = getItem(position);

        //TODO Seteando Imagen
        ImageView image = (ImageView) view.findViewById(R.id.imagen);
        //Glide.with(image.getContext()).load(item.getIdThumbnail()).into(image);

        // Seteando Titulo
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getTitulo());

        // Seteando Descripción
        TextView descripcion = (TextView) view.findViewById(R.id.descripcion);
        descripcion.setText(item.getDescripcion());

        // Seteando Precio
        TextView precio = (TextView) view.findViewById(R.id.precio);
        precio.setText(String.format("₡ %s", item.getPrecio().toString()));

        return view;
    }
}