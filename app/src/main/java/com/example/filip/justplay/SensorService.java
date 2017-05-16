package com.example.filip.justplay;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service {

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private float[] gravity = {0, 0, 0};
    private float[] linear_acceleration = {0, 0, 0};
    private final float alpha = 0.8f;

    private ArrayList<Float> accBuffer = new ArrayList<Float>();
    private ArrayList<double[]> geoBuffer = new ArrayList<>();

    private boolean isRunning;
    private boolean isWalking;
    private boolean isSeated;

    //BroadcastIntent
    private Intent broadcastIntent = new Intent();

    //Listeners
    private AccelerometerSensorListener accelerometerSensorListener = new AccelerometerSensorListener();
    //private LocationSensorListener locationSensorListener = new LocationSensorListener();

    @Override
    public IBinder onBind(Intent intent) {
        //Used only in case if services are bound (Bound Services).
        return null; //Disable binding
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.i("Service", "Running");

            /*float distance = 0;
            if(geoBuffer.size() > 2)
            {
                distance = (float) calculateDistance(geoBuffer);
            }*/
            FilteringData();
            Log.d("State", " Run: " + isRunning + " Walk: " + isWalking + " Seated: " + isSeated);

        }
    };
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 1000, 1 * 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "onStartCommand");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accSensor != null)
            sensorManager.registerListener(accelerometerSensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Location
        /*
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //This is already verified
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationSensorListener);
        }*/
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometerSensorListener);
        //locationManager.removeUpdates(locationSensorListener);

        mTimer.cancel();
        timerTask.cancel();

        broadcastIntent.putExtra("value", "store");
        sendBroadcast(broadcastIntent);
    }

    private void FilteringData(/*float distance*/){

        double accMean = mean(accBuffer);
        Log.d("Acc", " " + accMean);
        if(accMean > 2.0){
            isRunning = true;
            isWalking = false;
            isSeated = false;
        }
        else if(accMean > 1.0){
            isRunning = false;
            isWalking = true;
            isSeated = false;
        }
        else {
            isRunning = false;
            isWalking = false;
            isSeated = true;
        }
    }

    private double mean(ArrayList<Float> m) {
        double sum = 0;
        for (int i = 0; i < m.size(); i++) {
            sum += m.get(i);
        }
        return sum / m.size();
    }

    //If we need, eventually.
    private double calculateDistance(ArrayList<double[]> m){
        Location loc1 = new Location("");
        loc1.setLatitude(m.get(m.size() - 2)[0]);
        loc1.setLongitude(m.get(m.size() - 2)[1]);

        Location loc2 = new Location("");
        loc2.setLatitude(m.get(m.size() - 1)[0]);
        loc2.setLongitude(m.get(m.size() - 1)[1]);

        float distanceInMeters = Math.abs(loc1.distanceTo(loc2));
        return distanceInMeters;
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

            float magnitude = Math.abs((float)Math.sqrt(linear_acceleration[0]*linear_acceleration[0]
                    +linear_acceleration[1]*linear_acceleration[1]
                    +linear_acceleration[2]*linear_acceleration[2]));

            accBuffer.add(magnitude);
            if(accBuffer.size() > 250){accBuffer.clear();}
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    }

    /*
    private class LocationSensorListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("LocationListener", "Latitude:" + location.getLatitude()+ " Longitude:"+location.getLongitude()+ " Altitude:"+location.getAltitude());

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = location.getAltitude();
            double[] geo = {latitude, longitude, altitude};

            geoBuffer.add(geo);
            if(geoBuffer.size() > 250){geoBuffer.clear();}
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
    */
}
