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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

public class NeumaticosFragment extends Fragment {

    private EditText editTextVelocidadActual;
    private Button btnEnviarVelocidad;
    private RadioButton rbNormal;
    private RadioButton rbConClavo;

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

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 4:
                        editTextVelocidadActual.setText(((Double)msg.obj).toString());
                        break;
                    case 6:
                        if ((Boolean)msg.obj) {
                            btActivity.btService.actions.getVel();
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(), msg.what + " " + (String)msg.obj, Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
        btActivity.btService.actions.getVel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_neumaticos, container, false);

        editTextVelocidadActual =  (EditText) view.findViewById(R.id.editVelocidadActual);
        editTextVelocidadActual.setEnabled(false);
        btnEnviarVelocidad = (Button) view.findViewById(R.id.btnVelMax);
        rbConClavo = (RadioButton) view.findViewById(R.id.rdbConClavo);
        rbNormal = (RadioButton) view.findViewById(R.id.rdbNormales);

        btnEnviarVelocidad.setOnClickListener(new View.OnClickListener() {
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

        double valor = 0;

        if (rbConClavo.isChecked()) {
            valor = 60;
        } else if (rbNormal.isChecked()) {
            valor = 120;
        } else {
            return;
        }

        btActivity.btService.actions.setVel(valor);
    }
}