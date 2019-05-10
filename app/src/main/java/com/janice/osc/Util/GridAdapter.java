package com.janice.osc.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.travijuu.numberpicker.library.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.janice.osc.Customer.SodaProductsFragment;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Soda.ProductsFragment;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import java.util.List;


public class GridAdapter<T> extends BaseAdapter {

    private final Context mContext;
    private final List<T> items;
    private Fragment fragment;
    private int layoutId;

    public GridAdapter(Context c, List<T> items, Fragment fragment, int layout) {
        mContext = c;
        this.items = items;
        this.fragment = fragment;
        this.layoutId =layout;
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

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, viewGroup, false);
        }

        if (fragment instanceof ProductsFragment)
            setViewSodasProductos(view, position, true);
        else if (fragment instanceof SodaProductsFragment)
            setViewCustomerProductos(view, position, false);
        return view;
    }

    public void setViewCustomerProductos(View view, final int position, boolean setListener) {
        final Producto item = (Producto) getItem(position);

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

        // Setear NumberPicker
        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setValue(0); //Por default, siempre comienza en 0...
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                //int incremento = (action == ActionEnum.INCREMENT) ? 1: -1;
                //((SodaProductsFragment) fragment).agregarAlPedido(item, numberPicker.getValue(),incremento);
                if(action == ActionEnum.INCREMENT){
                    ((SodaProductsFragment) fragment).agregarAlPedido(item, numberPicker.getValue());
                }
                else{
                    ((SodaProductsFragment) fragment).removerDelPedido(item, numberPicker.getValue());
                }
            }
        });
    }

    public void setViewSodasProductos(View view, final int position, boolean setListener) {
        final Producto item = (Producto) getItem(position);

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

        if (setListener) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenuProductos((Producto) getItem(position));
                    return true;
                }
            });
        }
    }

    private void showMenuProductos(final Producto producto) {
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
                ((ProductsFragment) fragment).sendToEditarProductoActivity(producto);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((ProductsFragment) fragment).borrarProducto(producto);
                alert.dismiss();
            }
        });

        alert.show();
    }
}