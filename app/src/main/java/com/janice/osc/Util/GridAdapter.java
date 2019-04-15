package com.janice.osc.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Soda.ProductsFragment;

import java.util.EventListener;
import java.util.List;


public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Producto> items;
    private ProductsFragment fragment;

    public GridAdapter(Context c, List<Producto> items, ProductsFragment fragment/*EventListener listener*/) {
        mContext = c;
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return items.size();
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

        final Producto item = getItem(position);

        //Seteando Imagen
        ImageView image = (ImageView) view.findViewById(R.id.imagen);
        if (!item.getImg().equals("")) //Solo seteamos una imagen si el objeto trae una. Si no trae ninguna, se queda con la imagen default del layout
            Glide.with(image.getContext()).load(item.getImg()).into(image);

        // Seteando Titulo
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getTitulo());

        // Seteando Descripción
        TextView descripcion = (TextView) view.findViewById(R.id.descripcion);
        descripcion.setText(item.getDescripcion());

        // Seteando Precio
        TextView precio = (TextView) view.findViewById(R.id.precio);
        precio.setText(String.format("₡ %s", item.getPrecio().toString()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showMenu(item);
            }
        });

        return view;
    }

    private void showMenu(final Producto producto) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_edit_delete, null);
        builder.setView(viewAux);

        //atributos de la vista del AlertDialog.Builder
        TextView edit = viewAux.findViewById(R.id.edit);
        TextView delete = viewAux.findViewById(R.id.delete);

        final AlertDialog alert = builder.create();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(mContext, "Hay que editar", Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment.borrarProducto(producto);
                alert.dismiss();
            }
        });


        alert.show();
    }
}