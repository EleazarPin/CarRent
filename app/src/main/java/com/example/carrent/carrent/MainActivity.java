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

import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;
import com.example.carrent.carrent.com.example.carrent.carrent.services.BTService;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private Switch swActivarBluetooth;
    private Button btnBuscarDispositivos;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDlg;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, BTService.class);
        startService(intent);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        else {

            swActivarBluetooth = (Switch) findViewById(R.id.switchBluetooth);
            btnBuscarDispositivos = (Button) findViewById(R.id.btnBuscarDispositivos);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Buscando dispositivos...");
            mProgressDlg.setCancelable(false);

            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Detener", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            });


            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth no es soportado por el dispositivo móvil", Toast.LENGTH_LONG).show();
            } else {
                swActivarBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                });
                btnBuscarDispositivos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothAdapter.startDiscovery();
                    }
                });

                if (mBluetoothAdapter.isEnabled()) {
                    swActivarBluetooth.setChecked(true);
                }

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Cambia el estado del bluetooth (Acrtivado /Desactivado)
                filter.addAction(BluetoothDevice.ACTION_FOUND); //Se encuentra un dispositivo bluetooth al realizar una busqueda
                filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Cuando se comienza una busqueda de bluetooth
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //cuando la busqueda de bluetooth finaliza
                registerReceiver(mReceiver, filter);
            }
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

                //Si esta activado
                if (state == BluetoothAdapter.STATE_ON)
                    Toast.makeText(getApplicationContext(),"Bluetooth activado",Toast.LENGTH_SHORT).show();

            }
            //Si se inicio la busqueda de dispositivos bluetooth
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                mDeviceList = new ArrayList<BluetoothDevice>();
                mProgressDlg.show();
            }
            //Si finalizo la busqueda de dispositivos bluetooth
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                mProgressDlg.dismiss();

                if (mDeviceList.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"No se ha encontrado ningún dispositivo",Toast.LENGTH_SHORT).show();
                } else {
                    Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                    newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
                    startActivity(newIntent);
                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);

                String nombre = device.getName();
                Toast.makeText(getApplicationContext(), (nombre != null && !nombre.isEmpty() ? "Dispositivo Encontrado: " + nombre : "Dispositivo encontrado" ),Toast.LENGTH_LONG).show();
            }
        }
    };
}
