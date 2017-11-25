package com.example.carrent.carrent.com.example.carrent.carrent.commons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.carrent.carrent.com.example.carrent.carrent.services.BTService;

public class BTActivity extends AppCompatActivity {

    private boolean btIsBound;
    public BTService btService = null;
    public Handler btHandler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                BTService.class), btConnection, Context.BIND_AUTO_CREATE);
        btIsBound = true;
    }

    private void doUnbindService() {
        if (btIsBound){
            unbindService(btConnection);
            btIsBound = false;
        }
    }

    private ServiceConnection btConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            btService = ((BTService.LocalBinder)iBinder).getInstance();
            btService.setHandler(btHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            btService = null;
        }
    };
}
