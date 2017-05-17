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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.filip.justplay.Fragments.MyMusic;

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

    public boolean isRunning;
    public boolean isWalking;
    public boolean isSeated;

    //BroadcastIntent
    private Intent broadcastIntent = new Intent();

    public SensorService() {
    }

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
            FilteringData();
            sendMessage();
            Log.d("State", " Run: " + isRunning + " Walk: " + isWalking + " Seated: " + isSeated);

        }
    };
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2 * 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        broadcastIntent.setAction("PLAYLIST");

        Log.d("onStartCommand", "onStartCommand");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accSensor != null)
            sensorManager.registerListener(accelerometerSensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        timerTask.cancel();
        sensorManager.unregisterListener(accelerometerSensorListener);
    }

    private void sendMessage(){
        String activity = "Jogging";
        if(isSeated) activity = "Resting";
        else if(isWalking) activity = "Walking";

        Log.i("SendMessage", activity);
        broadcastIntent.putExtra("activity",activity);
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
}
