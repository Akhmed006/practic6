package ru.mirea.abdullin.mireaproject.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.mirea.abdullin.mireaproject.R;


public class SensorsFragment extends Fragment implements SensorEventListener {
    private Activity mActivity;
    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private Sensor pressureSensor;
    private Sensor tempSensor;

    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView tempTextView;

    public SensorsFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static SensorsFragment newInstance(String param1, String param2) {
        SensorsFragment fragment = new SensorsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        pressureSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        tempSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        humidityTextView = getView().findViewById(R.id.textView1);
        pressureTextView = getView().findViewById(R.id.textView2);
        tempTextView = getView().findViewById(R.id.textView3);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, humiditySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, pressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, tempSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            float valueHumidity = sensorEvent.values[0];
            humidityTextView.setText("Относительная влажность в процентах: "+ valueHumidity);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
            float valuePressure = sensorEvent.values[0];
            pressureTextView.setText("Атмосферное давление: "+valuePressure);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            float valueTemperature = sensorEvent.values[0];
            tempTextView.setText("Температура: "+ valueTemperature);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}