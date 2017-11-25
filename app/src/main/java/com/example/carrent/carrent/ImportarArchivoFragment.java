package com.example.carrent.carrent;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class ImportarArchivoFragment extends Fragment {

    private ListView lista;
    private BTActivity btActivity;
    private Button btnDescargarArchivo;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                    case 2:
                        String[] archivo = (String[])msg.obj;
                        for(int i = 0; i < archivo.length; i++) {
                            Toast.makeText(getActivity(), archivo[i], Toast.LENGTH_LONG).show();
                            SystemClock.sleep(250);
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(), msg.what + " " + (String)msg.obj, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_importar_archivo, container, false);

        // Inflate the layout for this fragment
        btnDescargarArchivo = (Button) view.findViewById(R.id.btnDescargarArchivo);
        btnDescargarArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btActivity.btService.actions.getFileTracking();
            }
        });
        return inflater.inflate(R.layout.fragment_importar_archivo, container, false);
    }
}
