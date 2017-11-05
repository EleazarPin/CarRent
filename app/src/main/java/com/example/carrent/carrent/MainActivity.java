package com.example.carrent.carrent;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private Switch swActivarBluetooth;
    private Button btnBuscarDispositivos;
    private Button btnVer;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDlg;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        else {

            swActivarBluetooth = (Switch) findViewById(R.id.switchBluetooth);
            btnBuscarDispositivos = (Button) findViewById(R.id.btnBuscarDispositivos);
            btnVer = (Button) findViewById(R.id.btnVer);
            btnVer.setOnClickListener(botonListener);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Buscando dispositivos...");
            mProgressDlg.setCancelable(false);

            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", btnCancelarDialogListener);


            if (mBluetoothAdapter == null) {
                //si el celular no soporta bluetooth
                Toast.makeText(getApplicationContext(), "Bluetooth no es soportado por el dispositivo móvil", Toast.LENGTH_LONG).show();
            } else {
                swActivarBluetooth.setOnCheckedChangeListener(swBluetooth);
                btnBuscarDispositivos.setOnClickListener(btnBuscarDispositivosListener);

                if (mBluetoothAdapter.isEnabled()) {
                    swActivarBluetooth.setChecked(true);
                    Toast.makeText(getApplicationContext(), "Bluetooth activado", Toast.LENGTH_SHORT).show();
                }
            }

            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Cambia el estado del bluetooth (Acrtivado /Desactivado)
            filter.addAction(BluetoothDevice.ACTION_FOUND); //Se encuentra un dispositivo bluetooth al realizar una busqueda
            filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Cuando se comienza una busqueda de bluetooth
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //cuando la busqueda de bluetooth finaliza

            //se define (registra) el handler que captura los broadcast anterirmente mencionados.
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            checkLocationPermission();
            registerReceiver(mReceiver, filter);
        }

    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    protected void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    View.OnClickListener botonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;

            if(v.getId()==R.id.btnVer) {
                intent=new Intent(MainActivity.this, NavigationDrawer.class);
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

        }
    };

    private DialogInterface.OnClickListener btnCancelarDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            mBluetoothAdapter.cancelDiscovery();
        }
    };

//    Handler que captura los brodacast que emite el SO al ocurrir los eventos del bluetooth
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //Atraves del Intent obtengo el evento de bluetooth que informo el broadcast del SO
            String action = intent.getAction();

            //Si cambio de estado el bluetooth(Activado/desactivado)
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                //Obtengo el parametro, aplicando un Bundle, que me indica el estado del bluetooth
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                Log.i(TAG, "-------------------BlueTooth ACTION STATE CHANGED-----------------");

                //Si esta activado
                if (state == BluetoothAdapter.STATE_ON)
                    Toast.makeText(getApplicationContext(),"Bluetooth activado",Toast.LENGTH_SHORT).show();

            }
            //Si se inicio la busqueda de dispositivos bluetooth
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                //Creo la lista donde voy a mostrar los dispositivos encontrados
                mDeviceList = new ArrayList<BluetoothDevice>();

                //muestro el cuadro de dialogo de busqueda
                mProgressDlg.show();

                Log.i(TAG, "-------------------BlueTooth ACTION DISCOVERY STARTED-----------------");
            }
            //Si finalizo la busqueda de dispositivos bluetooth
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                //se cierra el cuadro de dialogo de busqueda
                mProgressDlg.dismiss();

                Log.i(TAG, "-------------------BlueTooth ACTION DISCOVERY FINISHED-----------------");

                //se inicia el activity DeviceListActivity pasandole como parametros, por intent,
                //el listado de dispositovos encontrados
                Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);

                newIntent.putParcelableArrayListExtra("device.list", mDeviceList);

                startActivity(newIntent);
            }
            //si se encontro un dispositivo bluetooth
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Se lo agregan sus datos a una lista de dispositivos encontrados
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "-------------------BlueTooth ACTION FOUND!!!!-----------------");
                mDeviceList.add(device);
                Toast.makeText(getApplicationContext(),"Dispositivo Encontrado: " + device.getName(),Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener btnBuscarDispositivosListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBluetoothAdapter.startDiscovery();
        }
    };

    private CompoundButton.OnCheckedChangeListener swBluetooth = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1000);
            }
            else
            {
                mBluetoothAdapter.disable();
                Toast.makeText(getApplicationContext(),"Bluetooth desactivado",Toast.LENGTH_SHORT).show();
            }
        }
    };
}
