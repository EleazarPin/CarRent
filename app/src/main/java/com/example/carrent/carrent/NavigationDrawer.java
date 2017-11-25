package com.example.carrent.carrent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;

public class NavigationDrawer extends BTActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String NombreDispositivoBluetooth;
    private String DireccionDispositivoBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        setTitle("Car Rent Tracking");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent=getIntent();
        Bundle extras=intent.getExtras();

        NombreDispositivoBluetooth = extras.getString("Nombre_dispositivo");
        DireccionDispositivoBluetooth = extras.getString("Direccion_bluetooth");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Inicio por default con el fragmento bluetooth
        Bundle bundle = new Bundle();
        bundle.putString("N_BT",NombreDispositivoBluetooth);
        bundle.putString("D_BT",DireccionDispositivoBluetooth);
        BluetoothFragment bluetoothFragment = new BluetoothFragment();
        bluetoothFragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                bluetoothFragment,
                bluetoothFragment.getTag()).commit();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    private void desconectar() {
        
        this.btService.disconnect();
        
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("N_BT",NombreDispositivoBluetooth);
        bundle.putString("D_BT",DireccionDispositivoBluetooth);

        if (id == R.id.nav_neumaticos) {
            NeumaticosFragment neumaticosFragment = new NeumaticosFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    neumaticosFragment,
                    neumaticosFragment.getTag()).commit();
        } else if (id == R.id.nav_nivelhumo) {
            LimiteHumoFragment limiteHumoFragment = new LimiteHumoFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    limiteHumoFragment,
                    limiteHumoFragment.getTag()).commit();
        } else if (id == R.id.nav_tracking) {
            TrackingFragment trackingFragment = new TrackingFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    trackingFragment,
                    trackingFragment.getTag()).commit();
        } else if (id == R.id.nav_evento) {
            EventoFragment eventoFragment = new EventoFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    eventoFragment,
                    eventoFragment.getTag()).commit();
        }else if (id == R.id.nav_bluetooth){
            BluetoothFragment bluetoothFragment = new BluetoothFragment();
            bluetoothFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    bluetoothFragment,
                    bluetoothFragment.getTag()).commit();
        } else if (id == R.id.nav_reset_arduino){
            ResetFragment resetFragment = new ResetFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,
                    resetFragment,
                    resetFragment.getTag()).commit();

        } else if (id == R.id.nav_desconectar){

            desconectar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
