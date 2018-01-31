package com.blestech.musk.hand_screen_on;

/*import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private SensorManager sensorManager;
    private PowerManager pm;
    private PowerManager.WakeLock mWakelock;
    private long shakeTime;//抬手时间
    private long showTime;//平放手机时间
    private Sensor accelerometer;
    private static final String TAG = "musk001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeLock");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer == null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    //重力感应监听
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            int medumValue = 14;
            Log.d(TAG, "x="+x+"   y="+y+"   z="+z);
            //判断是否抬手
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                shakeTime = System.currentTimeMillis();
            }

            //判断是否平放手机
            if(9 < z  && -2 < x && x < 2 && -2 < y && y < 2){
                showTime = System.currentTimeMillis();
                if(0 < showTime - shakeTime && showTime - shakeTime < 1000){
                    shakeTime = 0;
		            Log.d(TAG, "need screen on now!!!!");
                    mWakelock.acquire();
                    mWakelock.release();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
