package com.example.carrent.carrent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;

public class ShakeDetector implements SensorEventListener {

    private static final int SHAKE_THRESHOLD = 800;

    private SensorManager sensorManager;
    private Sensor shake;

    private long lastShake;
    private long lastUpdate;
    private float lastX;
    private float lastY;
    private float lastZ;

    private OnShakeListener listener;

    public ShakeDetector(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.shake = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.lastUpdate = 0;
        this.lastShake = 0;
    }

    public void registerListener(OnShakeListener listener) {
        this.sensorManager.registerListener(this, shake, SensorManager.SENSOR_DELAY_NORMAL);
        this.listener = listener;
    }

    public void unregisterListener() {
        this.sensorManager.unregisterListener(this);
        this.listener = null;
    }

    public interface OnShakeListener {
        public void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {



        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = curTime - lastUpdate;
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs((x + y + z - lastX - lastY - lastZ) / diffTime * 10000);
                if (speed > SHAKE_THRESHOLD) {
                    if (System.currentTimeMillis() - 5000 > lastShake) {
                        if (listener != null)
                            listener.onShake();
                        lastShake = System.currentTimeMillis();
                    }
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }
}

