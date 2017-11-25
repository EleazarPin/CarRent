package com.example.carrent.carrent;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DeviceListActivity extends BTActivity {
    private ListView mListView;
    private DeviceListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
    private int posicionListBluethoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //defino los componentes de layout
        mListView = (ListView) findViewById(R.id.lv_paired);

        //obtengo por medio de un Bundle del intent la lista de dispositivos encontrados
        mDeviceList = getIntent().getExtras().getParcelableArrayList("device.list");

        //defino un adaptador para el ListView donde se van mostrar en la activity los dispositovs encontrados
        mAdapter = new DeviceListAdapter(this);

        //asocio el listado de los dispositovos pasado en el bundle al adaptador del Listview
        mAdapter.setData(mDeviceList);

        //defino un listener en el boton emparejar del listview
        mAdapter.setListener(listenerBotonEmparejar);
        mListView.setAdapter(mAdapter);

        //se definen un broadcastReceiver que captura el broadcast del SO cuando captura los siguientes eventos:
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //Cuando se empareja o desempareja el bluethoot

        //se define (registra) el handler que captura los broadcast anterirmente mencionados.
        registerReceiver(mPairReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mPairReceiver);
        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectDevice(BluetoothDevice device) {
        if (DeviceListActivity.this.btService.connect(device.getAddress())) {
            Intent navigationIntent = new Intent(DeviceListActivity.this, NavigationDrawer.class);
            navigationIntent.putExtra("Nombre_dispositivo", device.getName());
            navigationIntent.putExtra("Direccion_bluetooth", device.getAddress());

            startActivity(navigationIntent);
            finish();
        } else {
            showToast("Unable to connect device!");
        }
    }

    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private DeviceListAdapter.OnPairButtonClickListener listenerBotonEmparejar = new DeviceListAdapter.OnPairButtonClickListener() {
        @Override
        public void onPairButtonClick(int position) {
            //Se obtiene el device seleccionado
            BluetoothDevice device = mDeviceList.get(position);

            //Se checkea si el sipositivo ya esta emparejado
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                //Si esta emparejado,quiere decir que se selecciono conectar
                connectDevice(device);
            } else {
                //Si no esta emparejado se debe emparejar para luego conectar
                showToast("Emparejando");
                posicionListBluethoot = position;
                pairDevice(device);
            }
        }
    };

    //Handler que captura los brodacast que emite el SO al ocurrir los eventos del bluethoot
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //Atraves del Intent obtengo el evento de Bluethoot que informo el broadcast del SO
            String action = intent.getAction();

            //Se verifica si está emparejado o no
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                //Obtengo los parametros, aplicando un Bundle, que me indica el estado del Bluethoot
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                //se analiza si se puedo emparejar o no
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING)
                {
                    //Si se detecto que se puedo emparejar el bluethoot
                    showToast("Emparejado");
                    BluetoothDevice device = (BluetoothDevice) mAdapter.getItem(posicionListBluethoot);
                    connectDevice(device);

                }
                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {

                    showToast("Emparejado");
                    BluetoothDevice device = (BluetoothDevice) mAdapter.getItem(posicionListBluethoot);
                    connectDevice(device);
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };
}


