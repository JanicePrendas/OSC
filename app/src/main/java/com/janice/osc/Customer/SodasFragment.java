package com.janice.osc.Customer;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Soda;
import com.janice.osc.R;
import com.janice.osc.Util.GridAdapter;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SodasFragment extends Fragment {

    private List<Soda> mSodas;
    private GridViewWithHeaderAndFooter mGrid;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    public SodasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sodas, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fijar verticalmente
        setItems(view);
        setListeners();
        cargarSodas();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSodas==null)
            cargarSodas();
    }

    private void setItems(View view) {
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mGrid = view.findViewById(R.id.gridview); //Obtención del grid view
        mSodas = new ArrayList<>();
    }

    private void setListeners() {
    }

    private void cargarSodas() {
        mSodas = new ArrayList<>(); //Resetear lista de sodas para volverla a cargar desde 0
        db.collection("usuarios")
                .whereEqualTo("tipo","soda") //filtramos por sodas equivale a where tipo = soda
                .get() 
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Soda sodaObtenida = document.toObject(Soda.class);
                                sodaObtenida.setId(document.getId());
                                mSodas.add(sodaObtenida);
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
        if(mSodas.size()>0){
            grid.addHeaderView(createHeaderView(mSodas.get(0))); //El plato principal siempre estara en la primera posicion
            List<Soda> sodas_sin_plato_principal = mSodas; //Siempre hay que enviar la lista sin el plato principal al Adapter
            sodas_sin_plato_principal.remove(0);
            grid.setAdapter(new GridAdapter<Soda>(getContext(), sodas_sin_plato_principal, SodasFragment.this));
        }
    }

    /**
     * Crea un view de cabecera para mostrarlo en el principio del grid view.
     *
     * @return Header View
     */
    private View createHeaderView(Soda item) {
        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.template_soda, null, false);

        // Seteando Nombre
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getNombre());

        // Seteando Direccion
        TextView direccion = (TextView) view.findViewById(R.id.direccion);
        direccion.setText(String.format("Dirección: %s", item.getDireccion()));

        // Seteando Telefono
        TextView telefono = (TextView) view.findViewById(R.id.telefono);
        telefono.setText(String.format("Teléfono: %s", item.getTelefono()));

        return view;
    }


}
