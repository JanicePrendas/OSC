package com.janice.osc.Soda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.janice.osc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSodaFragment extends Fragment {


    public ProfileSodaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_soda, container, false);

        Toast.makeText(getContext(), "profile", Toast.LENGTH_LONG);
        return view;
    }

}
