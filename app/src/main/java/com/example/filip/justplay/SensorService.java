package com.example.filip.justplay;

import android.*;
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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

import static android.R.attr.gravity;
import static com.example.filip.justplay.R.attr.alpha;

public class SensorService extends Service {

    //objects for the sensor
    private Sensor myacc;
    private Sensor mylocal;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    //Inicialize variables
    float[] gravity = {0, 0, 0};
    float[] linear_acceleration = {0, 0, 0};


    ArrayList<Float> _accelerometerBuffer = new ArrayList<Float>();
    ArrayList<Float> _locationBuffer = new ArrayList<Float>();
    private int buffer_size = 25;

    //BroadcastIntent
    private Intent _broadcastIntent = new Intent();

    //Listeners
    private AccelerometerSensorListener acc_listener = new AccelerometerSensorListener();
    private LocationSensorListener location_listener = new LocationSensorListener();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int id) {

        //Create the sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accSensor != null)
            // Register Sensor Listener
            sensorManager.registerListener(acc_listener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(Build.VERSION.SDK_INT < 23)
        {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location_listener);
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            else{
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location_listener);
            }
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(acc_listener);
        locationManager.removeUpdates(location_listener);
    }

    private void AccSensorValues(float value){
        if(_accelerometerBuffer.size()==25){
            ArrayList buffer_temp = (ArrayList) _accelerometerBuffer.clone();
            sendMessage(buffer_temp);
            Log.i("Service", "sending message");
            _accelerometerBuffer.clear();
        }else{
            _accelerometerBuffer.add(value);
            Log.i("Service", "adding values");
        }
    }

    private void LocationSensorValues(float value){
        if(_locationBuffer.size()==25){
            ArrayList buffer_temp = (ArrayList) _locationBuffer.clone();
            sendMessage(buffer_temp);
            Log.i("Service", "sending message");
            _locationBuffer.clear();
        }else{
            _locationBuffer.add(value);
            Log.i("Service", "adding values");
        }
    }

    private void sendMessage(ArrayList<Float> buffer){
        float total=0;
        for(int i=0;i<buffer.size();i++)
            total=total+buffer.get(i);

        float average = total/buffer_size;

        _broadcastIntent.putExtra("average_lx",average);
        sendBroadcast(_broadcastIntent);
    }

    private class AccelerometerSensorListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
            linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
            linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

            //Log.i("Sensor Listener", "gravity "+gravity[0]+" "+gravity[1]+" "+gravity[2]);
            //Log.i("Sensor Listener", "linear_acceleration "+linear_acceleration[0]+" "+linear_acceleration[1]+" "+linear_acceleration[2]);
        }
    }

    private class LocationSensorListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i("LocationListener", " latitude:"+location.getLatitude()+ " longitude:"+location.getLongitude()+ " altitude:"+location.getAltitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("LocationListener", "provider status changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("LocationListener", "provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("LocationListener", "provider disabled");
        }
    }


}
