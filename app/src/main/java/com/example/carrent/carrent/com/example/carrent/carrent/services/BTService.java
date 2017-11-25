package com.example.carrent.carrent.com.example.carrent.carrent.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.EventoItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BTService extends Service {

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private enum ConnectionState {
        Connecting,
        Connected,
        Disconnected
    }

    private  final IBinder btIBinder = new LocalBinder();

    private Handler bluetoothIn = null;
    private BluetoothAdapter btAdapter = null;
    private ConnectionState state = ConnectionState.Disconnected;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;
    public Actions actions;

    private boolean stopThread;
    private StringBuilder recDataString = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BT SERVICE", "SERVICE CREATED");
        stopThread = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BT SERVICE", "SERVICE STARTED");
        bluetoothIn = new Handler() {

            public void handleMessage(android.os.Message msg) {
                Log.d("DEBUG", "handleMessage");
                String readMessage = (String) msg.obj;
                recDataString.append(readMessage);
                Log.d("RECORDED", recDataString.toString());
                recDataString.delete(0, recDataString.length());                    //clear all string data
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        Log.d("SERVICE", "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return btIBinder;
    }

    public void setHandler(Handler handler) {
        bluetoothIn = handler;
    }

    public boolean connect(String mac) {
        this.state = ConnectionState.Connecting;
        try {
            BluetoothDevice device = btAdapter.getRemoteDevice(mac);
            Log.d("DEBUG BT", "ATTEMPTING TO CONNECT TO REMOTE DEVICE : " + mac);
            mConnectingThread = new ConnectingThread(device);
            mConnectingThread.start();
        } catch (IllegalArgumentException e) {
            Log.d("DEBUG BT", "PROBLEM WITH MAC ADDRESS : " + e.toString());
            Log.d("BT SEVICE", "ILLEGAL MAC ADDRESS, STOPPING SERVICE");
            //stopSelf();
            disconnect();
        }

        while(this.state == ConnectionState.Connecting) {
            SystemClock.sleep(50);
        }

        if (this.state == ConnectionState.Connected)
            this.actions = new Actions();

        return this.state == ConnectionState.Connected;
    }

    public void disconnect() {

        if (bluetoothIn != null){
            bluetoothIn.removeCallbacksAndMessages(null);
        }
        stopThread = true;
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null) {
            mConnectingThread.closeSocket();
        }
        if (actions != null) {
            actions = null;
        }

        this.state = ConnectionState.Disconnected;
    }

    // New Class for Connecting Thread
    private class ConnectingThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectingThread(BluetoothDevice device) {
            Log.d("DEBUG BT", "IN CONNECTING THREAD");
            mmDevice = device;
            BluetoothSocket temp = null;
            Log.d("DEBUG BT", "BT UUID : " + BTMODULEUUID);
            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
                Log.d("DEBUG BT", "SOCKET CREATED : " + temp.toString());
            } catch (IOException e) {
                Log.d("DEBUG BT", "SOCKET CREATION FAILED :" + e.toString());
                Log.d("BT SERVICE", "SOCKET CREATION FAILED, STOPPING SERVICE");
                disconnect();
            }
            mmSocket = temp;
        }

        @Override
        public void run() {
            super.run();
            Log.d("DEBUG BT", "IN CONNECTING THREAD RUN");
            // Establish the Bluetooth socket connection.
            try {
                mmSocket.connect();
                Log.d("DEBUG BT", "BT SOCKET CONNECTED");
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                Log.d("DEBUG BT", "CONNECTED THREAD STARTED");
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("ACK*");
                state = ConnectionState.Connected;
            } catch (IOException e) {
                try {
                    Log.d("DEBUG BT", "SOCKET CONNECTION FAILED : " + e.toString());
                    Log.d("BT SERVICE", "SOCKET CONNECTION FAILED, STOPPING SERVICE");
                    mmSocket.close();
                    disconnect();
                } catch (IOException e2) {
                    Log.d("DEBUG BT", "SOCKET CLOSING FAILED :" + e2.toString());
                    Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                    disconnect();
                }
            } catch (IllegalStateException e) {
                Log.d("DEBUG BT", "CONNECTED THREAD START FAILED : " + e.toString());
                Log.d("BT SERVICE", "CONNECTED THREAD START FAILED, STOPPING SERVICE");
                disconnect();
            }
        }

        public void closeSocket() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                //stopSelf();
            }
        }
    }

    // New Class for Connected Thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final StringBuilder dataReceived;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            Log.d("DEBUG BT", "IN CONNECTED THREAD");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d("DEBUG BT", e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                disconnect();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            dataReceived = new StringBuilder();
        }

        public void run() {
            Log.d("DEBUG BT", "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true && !stopThread) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);

                    for(int i = 0; i < readMessage.length(); i++) {
                        char caracter = readMessage.charAt(i);
                        if (dataReceived.length() == 0) {
                            if (caracter == '<') {
                                dataReceived.append(caracter);
                            }
                        } else {
                            if (caracter == '>') {
                                dataReceived.append(caracter);

                                enviar(dataReceived.toString());

                                dataReceived.setLength(0);
                            } else {
                                dataReceived.append(caracter);
                            }
                        }
                    }


                    Log.d("DEBUG BT PART", "CONNECTED THREAD " + readMessage);
                    // Send the obtained bytes to the UI Activity via handler

                } catch (IOException e) {
                    Log.d("DEBUG BT", e.toString());
                    Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                    stopSelf();
                    break;
                }
            }
        }

        private void enviar(String registro) {
            //Se eliminan caracter de inicio y fin
            String limpio = registro.substring(1, registro.length() - 1);
            int codigo =  Integer.parseInt(limpio.substring(0, 2));
            String mensaje =  limpio.substring(2);
            Object data = null;

            switch (codigo) {
                case 1:
                    List<TrackingItem> trackingItems = new ArrayList<TrackingItem>();
                    String[] mensajesTracking = mensaje.split("\\*");
                    for(int i = 0; i < mensajesTracking.length; i++){
                        TrackingItem item = TrackingItem.Parse(mensajesTracking[i]);
                        if(item != null)
                            trackingItems.add(item);
                    }
                    data = trackingItems;
                    break;
                case 2:
                    List<EventoItem> eventoItems = new ArrayList<EventoItem>();
                    String[] mensajesEvento = mensaje.split("\\*");
                    for(int i = 0; i < mensajesEvento.length; i++){
                        EventoItem item = EventoItem.Parse(mensajesEvento[i]);
                        if(item != null)
                            eventoItems.add(item);
                    }
                    data = eventoItems;
                    break;
                case 3:
                case 4:
                    data = Double.parseDouble(mensaje);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    data = "true".equals(mensaje);
                    break;
                default:
                    data = mensaje;
                    break;
            }

            if (bluetoothIn != null)
                bluetoothIn.obtainMessage(codigo, -1, -1, data).sendToTarget();
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Log.d("DEBUG BT", "UNABLE TO READ/WRITE " + e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                //stopSelf();
                disconnect();
            }
        }

        public void closeStreams() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream.close();
                mmOutStream.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "STREAM CLOSING FAILED, STOPPING SERVICE");
                //stopSelf();
            }
        }
    }

    public class LocalBinder extends Binder {
        public BTService getInstance() {
            return  BTService.this;
        }
    }

    public class Actions {

        // PROTOCOLO DE COMUNICACION CON ARDUINO
        final String ACTION_GET_FILE_TRACKING = "getFile#Tracking";
        final String ACTION_GET_FILE_EVENTOS = "getFile#Evento";
        final String ACTION_GET_MQ7 = "get#MQ7";
        final String ACTION_GET_VEL = "get#Vel";
        final String ACTION_SET_MQ7 ="setMQ7#";
        final String ACTION_SET_VEL ="setVel#";
        final String ACTION_CLEAR_ALL ="clear#all";
        final String ACTION_CLEAR_TRACKING ="clear#tracking";
        final String ACTION_CLEAR_EVENTOS ="clear#evento";

        public void getFileTracking(){
            this.send(ACTION_GET_FILE_TRACKING);
        }

        public void getFileEventos(){
            this.send(ACTION_GET_FILE_EVENTOS);
        }

        public void getMQ7(){
            this.send(ACTION_GET_MQ7);
        }

        public void getVel(){
            this.send(ACTION_GET_VEL);
        }

        public void setMQ7(double valor) {
            this.send(ACTION_SET_MQ7 + valor);
        }

        public void setVel(double valor) {
            this.send(ACTION_SET_VEL + valor);
        }

        public void clearAll(){
            this.send(ACTION_CLEAR_ALL);
        }

        public void clearTracking(){
            this.send(ACTION_CLEAR_TRACKING);
        }

        public void clearEvento(){
            this.send(ACTION_CLEAR_EVENTOS);
        }

        private void send(String action) {
            mConnectedThread.write(action + "*");
        }
    }
}