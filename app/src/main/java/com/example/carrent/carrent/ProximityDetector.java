package com.example.carrent.carrent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximityDetector implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximity;
    private static final int SENSOR_SENSITIVITY = 4;

    private OnProximityListener listener;
    private  long lastProximity;

    public void registerListener(OnProximityListener listener) {
        this.sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        this.listener = listener;
    }

    public void unregisterListener() {
        this.sensorManager.unregisterListener(this);
        this.listener = null;
    }

    public interface OnProximityListener {
        public void onProximity();
    }

    public ProximityDetector(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.proximity = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.lastProximity = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                if (System.currentTimeMillis() - 5000 > lastProximity) {
                    //near
                    if (listener != null)
                        listener.onProximity();
                    lastProximity = System.currentTimeMillis();
                }

            }
            //else {
                //far
             //   Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
            //}
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
