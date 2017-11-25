package com.example.carrent.carrent;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class BluetoothFragment extends Fragment {

    TextView txtNombreDispositivo;
    TextView txtDireccionDispositivo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_bluetooth, container, false);

        Bundle bundle = this.getArguments();

        txtNombreDispositivo = (TextView)view.findViewById(R.id.txtNombreDispositivo);
        txtDireccionDispositivo = (TextView)view.findViewById(R.id.txtMacAddress);

        txtNombreDispositivo.setText(bundle.getString("N_BT"));
        txtDireccionDispositivo.setText(bundle.getString("D_BT"));

        return view;
    }
}
