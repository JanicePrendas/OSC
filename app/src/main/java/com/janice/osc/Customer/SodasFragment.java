package com.janice.osc.Customer;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.janice.osc.Util.ListAdapter;
import com.janice.osc.Util.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SodasFragment extends Fragment {

    private List<Soda> mSodas;
    private ListView listView;
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
        listView = view.findViewById(R.id.lista_sodas); //Obtención de la lista
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
        if(mSodas.size()>0){
            list.setAdapter(new ListAdapter<Soda>(getActivity(),mSodas, SodasFragment.this, R.layout.template_soda));
        }
    }

    public void verSoda(Soda soda){
        Util.idSodaSelected = soda.getId();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SodaProductsFragment()).commit();
    }


}
