package com.example.smt_bunny;

import static android.os.SystemClock.uptimeMillis;

import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataCollectorService extends IntentService {
    //WARNING: THIS APPROACH WILL NOT WORK. AN INTENTSERVICE QUITS AFTER ITS INTENTHANDLER COMPLETES.
    // IT WILL SET UP THE ACCEL LISTENER, BUT THEN IT WILL QUIT AND NEVER RECEIVE BROADCASTS.
    // USE A BUFFEREDOUTPUTSTREAM IN THE EXPERIMENT ACTIVITY TO COLLECT TOUCHDATA. THE EVENT DATA
    // CAN GET WRITTEN AS A FILEOUTPUTSTREAM AS IS, BECAUSE IT HAPPENS INFREQUENTLY AT THE START OF A TRIAL.
    public DataCollectorService() {
        super("DataCollectorService");
    }
    // Define some fields for your data and logic
    private static final String TAG = "MyDataCollector";

    private File File_Accel;
    private File File_Touch;
    private File File_Event;
    private Boolean can_record;

    private BroadcastReceiver testReceiver;

    // Register the BroadcastReceiver in the onCreate() method of the service
    @Override
    public void onCreate() {
        super.onCreate();
        // Create an intent filter for the broadcast action
      //  IntentFilter filter = new IntentFilter("TOUCH_DATA");
        // Register the receiver with the filter
      //  registerReceiver(touchReceiver, filter);
        // Create an intent filter for the broadcast action
      //  IntentFilter eventFilter = new IntentFilter("EVENT_DATA");
        // Register the receiver with the filter
      //  registerReceiver(eventReceiver, eventFilter);


    }

    // Unregister the BroadcastReceiver in the onDestroy() method of the service
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        //unregisterReceiver(touchReceiver);
        //unregisterReceiver(eventReceiver);
        //unregisterReceiver(testReceiver);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        File_Accel = (File) intent.getSerializableExtra("f_accel"); // Get the file object from the intent
       // File_Touch = (File) intent.getSerializableExtra("f_touch");
       // File_Event = (File) intent.getSerializableExtra("f_event");
        can_record = intent.getBooleanExtra("can_record", false);

        // Get the sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Get the accelerometer sensor
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Register a listener to get the sensor data
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Get the accelerometer values
                float accelValues1 = event.values[0];
                float accelValues2 = event.values[1];
                float accelValues3 = event.values[2];
                // Format the data as a string
                String dat = String.valueOf(accelValues1) + ", " + String.valueOf(accelValues2) + ", " + String.valueOf(accelValues3);
                // Write the data to the file
                WriteDatToFile(File_Accel, dat);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Do nothing
            }
        }, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }
    /*
    // Create a BroadcastReceiver object
    private final BroadcastReceiver touchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "got a touch broadcast");
            // Get the touch data from the intent
            String touchData = intent.getStringExtra("touchData");
            // Write the touch data to the file
            WriteToFile(File_Touch, touchData);
        }
    };

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "got an event broadcast");
            // Get the touch data from the intent
            String eventData = intent.getStringExtra("eventData");
            // Write the touch data to the file
            WriteToFile(File_Event, eventData);
        }
    };
*/
    private void WriteToFile(File WriteFile, String data) {
            if (can_record) {
                try {
                    FileOutputStream fos = new FileOutputStream(WriteFile, true);
                    fos.write(data.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void WriteDatToFile(File WriteFile, String data) {
            WriteToFile(WriteFile, uptimeMillis() + ", " + data + "\n");
        }


}

