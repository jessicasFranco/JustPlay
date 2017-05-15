package com.example.filip.justplay;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service {

    private SensorManager sensorManager;
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;

    float[] gravity = {0, 0, 0};
    float[] linear_acceleration = {0, 0, 0};
    final float alpha = 0.8f;

    ArrayList<Float> buffer = new ArrayList<Float>();
    private int bufferSize = 25;

    //BroadcastIntent
    private Intent broadcastIntent = new Intent();

    //Listeners
    private AccelerometerSensorListener accelerometerSensorListener = new AccelerometerSensorListener();
    private GravitySensorListener gravitySensorListener = new GravitySensorListener();
    private LocationSensorListener locationSensorListener = new LocationSensorListener();

    @Override
    public IBinder onBind(Intent intent) {
        //Used only in case if services are bound (Bound Services).
        return null; //Disable binding
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.i("Service", "Running");
        }
    };
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2 * 1000);

        buffer.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accSensor != null)
            sensorManager.registerListener(accelerometerSensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Gravity (Gyro)
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravitySensor != null)
            sensorManager.registerListener(gravitySensorListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationSensorListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometerSensorListener);
        sensorManager.unregisterListener(gravitySensorListener);
        locationManager.removeUpdates(locationSensorListener);

        mTimer.cancel();
        timerTask.cancel();

        broadcastIntent.putExtra("value", "store");
        sendBroadcast(broadcastIntent);
    }

    private class AccelerometerSensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    }

    private class GravitySensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i("Gravity Sensor Listener", " Force of gravity along the X axis: " +
                    event.values[0] + " Force of gravity along the  Y axis: " + event.values[1] +
                    " Force of gravity along the Z axis: " + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    }

    private class LocationSensorListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("LocationListener", "Latitude:" + location.getLatitude()+ " Longitude:"+location.getLongitude()+ " Altitude:"+location.getAltitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("LocationListener", "Provider status changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("LocationListener", "Provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("LocationListener", "Provider disabled");
        }
    }
}
