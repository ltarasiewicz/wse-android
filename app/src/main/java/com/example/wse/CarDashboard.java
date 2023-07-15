package com.example.wse;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wse.databinding.ActivityCarDashboardBinding;
import com.example.wse.types.AccelerationUnit;
import com.example.wse.types.DashboardSettings;
import com.example.wse.types.DashboardTheme;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

public class CarDashboard extends AppCompatActivity {

    /*
     * "Lat-init" properties, set in onCreate, read from intent (DashboardSettings)
     */
    private static AccelerationUnit accelerationUnit;

    private static Integer decimalPrecision;

    /*
     * "Late-init" properties, set in onCreate
     */
    private Resources resources;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private ActivityCarDashboardBinding binding;

    /*
     * "Late-init" properties, set in onResume
     */
    private SensorEventListener sensorEventListener;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        binding = ActivityCarDashboardBinding.inflate(getLayoutInflater());



        setContentView(binding.getRoot());

    }

    protected void onResume() {
        super.onResume();
        /*
         * NOTE: the "entry" view has its own "view model" (DashboardSettings) so it's not the same instance as the one here
         *
         * A strategy being tried here in order to share a single "view model" between the two views
         * is to "inflate" the view model from the intent and then bind it to the view
         */
        Intent intent = getIntent();
        DashboardSettings dashboardSettingsIntent = intent.getParcelableExtra("settings");
        binding.setViewModel(dashboardSettingsIntent);
        /*
         * ViewModel wired up with "observable" data binding
         */
        DashboardSettings viewModel = binding.getViewModel();
        /*
         * NOTE: at this point the Intent passed from previous view and the view model are bound together
         */

        sensorEventListener = setUpAccelerometer(binding, resources, viewModel, sensorManager);
        locationListener = setUpLocationManager(binding, locationManager);

        // @Bindable, read the view model registered with data binding
        accelerationUnit = viewModel.getAccelerationUnit();

        // not @Bindable, read once from "Parcelable" implementation
        decimalPrecision = viewModel.getDecimalPrecision();

        /*
         * Set the theme of the dashboard based on the settings passed in the intent
         */
        var layout = binding.getRoot();
        var selectedTheme = viewModel.getTheme();
        Log.d("Dash debug", selectedTheme.getTheme());
        layout.setBackgroundColor(selectedTheme == DashboardTheme.DARK ? Color.DKGRAY : Color.LTGRAY);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener setUpLocationManager(ActivityCarDashboardBinding binding, LocationManager locationManager) {
        LocationListener locationListener = location -> {
            var rawFormat = "#." + "#".repeat(decimalPrecision);

            var decimalFormat = new DecimalFormat(rawFormat);
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

            var latitudeFormatted = decimalFormat.format(location.getLatitude());
            var longitudeFormatted = decimalFormat.format(location.getLongitude());

            binding.locationData.setText(String.format(Locale.getDefault(), "Latitude: %s | Longitude: %s", latitudeFormatted, longitudeFormatted));
        };

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        }

        return locationListener;
    }

    private static SensorEventListener setUpAccelerometer(ActivityCarDashboardBinding binding, Resources resources, DashboardSettings viewModel, SensorManager sensorManager) {

        // get button with ID "swapSpeedUnit", add event on click event listener that would change the text of TextView with ID "speedUnit"
        binding.swapSpeedUnit.setOnClickListener(v -> {
            if (viewModel.getAccelerationUnit() == AccelerationUnit.KILOMETER_PER_HOUR) {
                viewModel.setAccelerationUnit(AccelerationUnit.METER_PER_SECOND);
            } else {
                viewModel.setAccelerationUnit(AccelerationUnit.KILOMETER_PER_HOUR);
            }
        });

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener listener = new SensorEventListener() {
            final Float alpha = 0.8f;
            private float[] gravityValues = null;

            @Override
            public void onSensorChanged(SensorEvent event) {
                // Apply high-pass filter to remove gravity
                if (gravityValues == null) {
                    // Will run only once
                    gravityValues = new float[3];
                    System.arraycopy(event.values, 0, gravityValues, 0, 3);
                } else {
                    gravityValues[0] = alpha * gravityValues[0] + (1 - alpha) * event.values[0];
                    gravityValues[1] = alpha * gravityValues[1] + (1 - alpha) * event.values[1];
                    gravityValues[2] = alpha * gravityValues[2] + (1 - alpha) * event.values[2];
                }

                Float[] motionAcceleration = new Float[3];

                // Remove the gravity contribution with the high-pass filter.
                motionAcceleration[0] = event.values[0] - gravityValues[0];
                motionAcceleration[1] = event.values[1] - gravityValues[1];
                motionAcceleration[2] = event.values[2] - gravityValues[2];

                // get user locale
                Locale locale = resources.getConfiguration().getLocales().get(0);

                // display msg in text view with id "speedDisplay"
                Float accelerationAlongXAxisInTargetUnit = motionAcceleration[0] * accelerationUnit.getValue();
                binding.speedDisplay.setText(String.format(locale, accelerationAlongXAxisInTargetUnit.toString()));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // do nothing
            }
        };

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return listener;
    }
}
