package com.example.myapplication2;

import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.car.Car;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.car.VehiclePropertyIds.*;
import static android.car.hardware.CarSensorManager.SENSOR_RATE_NORMAL;
import static java.lang.Integer.toHexString;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "VehiclePropertyTest";
    private Car mCarApi;
    private CarPropertyManager mPropertyManager;
    private PropertyListener mPropertyListener;
    private static Context appcontext;
    private String[] permissions = {Car.PERMISSION_SPEED};

    class PropertyListener implements CarPropertyManager.CarPropertyEventCallback {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Log.v(TAG, "Received Car Property Event: " + carPropertyValue.toString());
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.append("\n" + VehiclePropertyIds.toString(carPropertyValue.getPropertyId()) + ": " + carPropertyValue.toString());
        }

        @Override
        public void onErrorEvent(int propertyId, int zone) {
            Log.v(TAG, "Error: propertyId=" + toHexString(propertyId) + " zone=" + zone);
        }
    }


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            initCarApi();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
    }

    private void initCarApi() {
        if (mCarApi != null && mCarApi.isConnected()) {
            mCarApi.disconnect();
            mCarApi = null;
        }
        mCarApi = Car.createCar(this, mConnectionListener);
        mCarApi.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
      // resumeDiagnosticManager();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resumeDiagnosticManager();
        if(checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            //resumePropertyManager();
        } else {
            requestPermissions(permissions, 0);
        }
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
       // pauseDiagnosticManager();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCarApi != null) {
            mCarApi.disconnect();
        }
        Log.i(TAG, "onDestroy");
    }

    public Car getCar() {
        return mCarApi;
    }

    public CarPropertyManager getPropertyManager() {
        return mPropertyManager;
   }

    public static Context getAppContext() {
        return appcontext;
    }
    private final ServiceConnection mConnectionListener =
            new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    //assertMainThread();
                    Log.i(TAG, "car service disconnected");

                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    Log.i(TAG, "car service connected");
                    //assertMainThread();
                   // mConnectionWait.release();

                    try {
                        mPropertyManager = (CarPropertyManager)mCarApi.getCarManager(Car.PROPERTY_SERVICE);
                        //if (mLiveListener != null) {
                        Log.i(TAG, "in resumedidonostic");
                        if (mPropertyListener == null) {
                            Log.i(TAG, "in resumedidonostic1");
                            mPropertyListener = new PropertyListener();
                        }

                        // Create a list of properties in debug window
                        List<CarPropertyConfig> mPropertyList = mPropertyManager.getPropertyList();
                        Log.d(TAG, "carPropertyList: " + mPropertyList.toString());
                        for (int i=0; i < mPropertyList.size(); i++) {
                            Log.v(TAG, VehiclePropertyIds.toString(mPropertyList.get(i).getPropertyId()));
                            TextView textView = (TextView) findViewById(R.id.textView);
                            textView.append("\n" + VehiclePropertyIds.toString(mPropertyList.get(i).getPropertyId()));
                            mPropertyManager.registerCallback(mPropertyListener, mPropertyList.get(i).getPropertyId(), CarPropertyManager.SENSOR_RATE_NORMAL);
                        }

                        //mPropertyManager.setFloatProperty(PERF_VEHICLE_SPEED, VEHICLE_AREA_TYPE_GLOBAL, 77);
                        //mPropertyManager.getFloatProperty(PERF_VEHICLE_SPEED, VEHICLE_AREA_TYPE_GLOBAL);
                        //mPropertyManager.registerCallback(mPropertyListener, INFO_MAKE, CarPropertyManager.SENSOR_RATE_NORMAL);
                        //mPropertyManager.registerCallback(mPropertyListener, INFO_MODEL_YEAR, CarPropertyManager.SENSOR_RATE_NORMAL);
                        //mPropertyManager.registerCallback(mPropertyListener, PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_NORMAL);
                        //mPropertyManager.registerCallback(mPropertyListener, HVAC_TEMPERATURE_SET, CarPropertyManager.SENSOR_RATE_NORMAL);

                        Log.i(TAG, "in resumedidonostic2");

                      //
                        //}
                        //  if (mFreezeListener != null) {
                        //    mDiagnosticManager.registerListener(mFreezeListener,
                        //          CarDiagnosticManager.FRAME_TYPE_FREEZE,
                        //        CarSensorManager.SENSOR_RATE_NORMAL);
                        //}
                    } catch (android.car.CarNotConnectedException e) {
                        Log.e(TAG, "Car not connected or not supported", e);
                        e.printStackTrace();

                    }
                }
            };

    private void resumePropertyManager() {
        if (mPropertyListener == null) {
            mPropertyListener = new PropertyListener();
        }

        try {
            Log.i(TAG, "in resumedidonostic4");
        }
        catch(android.car.CarNotConnectedException e) {
            Log.e(TAG, "register listener failed", e);
            e.printStackTrace();
        }
    }

    private void pausePropertyManager() {
        if (mPropertyManager != null) {
            if (mPropertyListener != null) {
                mPropertyManager.unregisterCallback(mPropertyListener);
            }
            //if (mFreezeListener != null) {
              //  mDiagnosticManager.unregisterListener(mFreezeListener);
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
