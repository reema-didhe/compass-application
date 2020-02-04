package com.example.compassapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView compass;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float[] mGravity = new float[3];
    private float[] mGeometric = new float[3];
    private float mDegree = 0f;
    private float degreeCurrent = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews(){
        //initializing compass imageview and device's sensor manager
        compass = findViewById(R.id.compass_circle);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensor manager registers orientation listener
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister the listener
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float rotate = 0.80f;
        synchronized (this){
            if (event.sensor.getType() ==  Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = rotate*mGravity[0]+(1-rotate)*event.values[0];
                mGravity[1] = rotate*mGravity[1]+(1-rotate)*event.values[1];
                mGravity[2] = rotate*mGravity[2]+(1-rotate)*event.values[2];
            }
            if (event.sensor.getType() ==  Sensor.TYPE_MAGNETIC_FIELD) {
                mGravity[0] = rotate*mGeometric[0]+(1-rotate)*event.values[0];
                mGravity[1] = rotate*mGeometric[1]+(1-rotate)*event.values[1];
                mGravity[2] = rotate*mGeometric[2]+(1-rotate)*event.values[2];
            }
            float R[] = new float[9];
            float I[] = new  float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeometric);
            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R,orientation);
                mDegree = (float) Math.toDegrees(orientation[0]);
                mDegree = (mDegree+360)%360;

                Animation anim = new RotateAnimation(-degreeCurrent,-mDegree,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

                //setting up the properties of animation such as duration, reservation status, and repeat count
                anim.setDuration(400);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                degreeCurrent = mDegree;

                //start animation on imageview
                compass.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
