package com.example.carrent.carrent;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

public class LimiteHumoFragment extends Fragment {

    EditText editTextNivelActual;
    EditText editTextNivelNuevo;
    Button btnEnviarNuevoNivelHumo;

    private ShakeDetector shakeDetector;
    private BTActivity btActivity;

    @Override
    public void onResume() {
        super.onResume();
        shakeDetector.registerListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {
                enviar();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.unregisterListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 3:
                        editTextNivelActual.setText(((Double)msg.obj).toString());
                    break;
                    case 5:
                        if ((Boolean)msg.obj) {
                            btActivity.btService.actions.getMQ7();
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(), msg.what + " " + (String)msg.obj, Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
        btActivity.btService.actions.getMQ7();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_limite_humo, container, false);

        editTextNivelActual = (EditText) view.findViewById(R.id.editNivelHumoActual);
        editTextNivelActual.setEnabled(false);
        editTextNivelNuevo = (EditText) view.findViewById(R.id.editNivelHumoNuevo);
        btnEnviarNuevoNivelHumo = (Button) view.findViewById(R.id.btnEstablecerNivelHumo);
        btnEnviarNuevoNivelHumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar();
            }
        });

        SensorManager sensorManager = (SensorManager)this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(sensorManager);


        return view;
    }

    private void enviar() {
        double valor = Double.parseDouble(editTextNivelNuevo.getText().toString());
        btActivity.btService.actions.setMQ7(valor);
    }
}