package com.example.carrent.carrent;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

public class ResetFragment extends Fragment {

    private Button btnResetTracking;
    private Button btnResetEvents;
    private Button btnResetAll;
    private ProximityDetector proximityDetector;
    private BTActivity btActivity;

    @Override
    public void onResume() {
        super.onResume();
        proximityDetector.registerListener(new ProximityDetector.OnProximityListener() {
            @Override
            public void onProximity() {
                btActivity.btService.actions.clearAll();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        proximityDetector.unregisterListener();
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 7:
                    case 8:
                    case 9:
                        String mensaje = (Boolean)msg.obj ? "Borrado exitoso" : "No se han podido borrar los archivos";
                        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
                        break;
                    default:

                        break;
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_reset, container, false);

        btnResetTracking = (Button) view.findViewById(R.id.btnClearTracking);
        btnResetTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btActivity.btService.actions.clearTracking();
            }
        });

        btnResetEvents = (Button) view.findViewById(R.id.btnClearEvents);
        btnResetEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btActivity.btService.actions.clearEvento();
            }
        });

        btnResetAll = (Button) view.findViewById(R.id.btnClearAll);
        btnResetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btActivity.btService.actions.clearAll();
            }
        });

        SensorManager sensorManager = (SensorManager)this.getActivity().getSystemService(Context.SENSOR_SERVICE);

        proximityDetector = new ProximityDetector(sensorManager);


        return view;
    }
}