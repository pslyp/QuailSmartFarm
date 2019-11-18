package com.pslyp.dev.quailsmartfarm.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pslyp.dev.quailsmartfarm.MQTT;
import com.pslyp.dev.quailsmartfarm.NumberDialog;
import com.pslyp.dev.quailsmartfarm.NumberPickerDialog;
import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.SmartConfigWiFI;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Device;
import com.pslyp.dev.quailsmartfarm.models.DeviceListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigActivity extends AppCompatActivity implements NumberPickerDialog.NumberDialogListener {

    private MQTT mqtt;
    private RestAPI restAPI;

    private Button buttonSave, buttonNet;
    private MaterialSpinner device;
    private LinearLayout linearStartTime, linearEndTime;
    private Spinner spinner;
    private TextView textViewBright, textViewTemp, textViewStartHour, textViewStartMinute, textViewEndHour, textViewEndMinute;
    private TextView tempMinText, tempMaxText;
    private ProgressBar progressBar;

    private SharedPreferences sp;
    private final String PREF_NAME = "LoginPreferences";

    private List<String> boardTokens;
    private List<String> boardNames;
    private List<Device> deviceList;

    private String timeUp;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        initInstance();
    }

//    @Override
//    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//        Toast.makeText(this,
//                "selected number " + numberPicker.getValue(), Toast.LENGTH_SHORT).show();
//
//        switch (i) {
//            case 1000 : textViewBright.setText(numberPicker.getValue());
//                break;
//        }
//    }



    public void showNumberPicker(){
        final int brightOld = Integer.valueOf(textViewBright.getText().toString());

        NumberPickerDialog newFragment = new NumberPickerDialog();
        newFragment.setId(R.id.text_view_bright);
        newFragment.setMinValue(0);
        newFragment.setMaxValue(4095);
        newFragment.setValue(brightOld);
//        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "time picker");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initInstance() {
        mqtt = new MQTT(this);
        restAPI = new RestAPI();
        boardTokens = new ArrayList<>();
        boardNames = new ArrayList<>();
        deviceList = new ArrayList<>();

        progressBar = findViewById(R.id.config_progress_bar);

        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setDropDownWidth(180);

        textViewBright = findViewById(R.id.text_view_bright);
//        textViewTemp = findViewById(R.id.text_view_temp);
        tempMinText = findViewById(R.id.text_view_temp_min);
        tempMaxText = findViewById(R.id.text_view_temp_max);
        textViewStartHour = findViewById(R.id.text_view_start_hour);
        textViewStartMinute = findViewById(R.id.text_view_start_minute);
        textViewEndHour = findViewById(R.id.text_view_end_hour);
        textViewEndMinute = findViewById(R.id.text_view_end_minute);
        linearStartTime = findViewById(R.id.linear_start_time);
        linearEndTime = findViewById(R.id.linear_end_time);
        buttonSave = findViewById(R.id.button_save);
        buttonNet = findViewById(R.id.button_internet);

        mqtt.connect();

        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        final String boardToken = getIntent().getStringExtra("TOKEN");
        final String userId = sp.getString("ID", "");

        initConfig(userId, boardToken);

        textViewBright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int brightOld = Integer.valueOf(textViewBright.getText().toString());

//                NumberDialog numberDialog = new NumberDialog();
//                numberDialog.setId(R.id.text_view_bright);
//                numberDialog.setTitle("Brightness");
//                numberDialog.setMinValue(0);
//                numberDialog.setMaxValue(4095);
//                numberDialog.setValue(brightOld);
//                numberDialog.show(getFragmentManager(), "bright dialog");
//                numberDialog.setTargetFragment(getSupportFragmentManager(), 1);

                NumberPickerDialog newFragment = new NumberPickerDialog();
                newFragment.setId(R.id.text_view_bright);
                newFragment.setMinValue(0);
                newFragment.setMaxValue(4095);
                newFragment.setValue(brightOld);
                newFragment.show(getSupportFragmentManager(), "brightness picker");
            }
        });

//        textViewTemp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                final int tempOld = Integer.valueOf(textViewTemp.getText().toString());
////
////                NumberDialog numberDialog = new NumberDialog();
////                numberDialog.setId(R.id.text_view_temp);
////                numberDialog.setTitle("Temperature");
////                numberDialog.setMinValue(0);
////                numberDialog.setMaxValue(100);
////                numberDialog.setValue(tempOld);
////                numberDialog.show(getFragmentManager(), "temp dialog");
////                numberDialog.setTargetFragment(ConfigFragment.this, 1);
////
////                NumberPickerDialog newFragment = new NumberPickerDialog();
////                newFragment.setId(R.id.text_view_temp);
////                newFragment.setMinValue(0);
////                newFragment.setMaxValue(100);
////                newFragment.setValue(tempOld);
////                newFragment.show(getSupportFragmentManager(), "temp picker");
//            }
//        });

        tempMinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int tempOld = Integer.valueOf(tempMinText.getText().toString());

                NumberPickerDialog newFragment = new NumberPickerDialog();
                newFragment.setId(R.id.text_view_temp_min);
                newFragment.setMinValue(0);
                newFragment.setMaxValue(Integer.parseInt(tempMaxText.getText().toString()) - 10);
                newFragment.setValue(tempOld);
                newFragment.show(getSupportFragmentManager(), "temp min picker");
            }
        });

        tempMaxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int tempOld = Integer.valueOf(tempMaxText.getText().toString());

                NumberPickerDialog newFragment = new NumberPickerDialog();
                newFragment.setId(R.id.text_view_temp_max);
                newFragment.setMinValue(Integer.parseInt(tempMinText.getText().toString()) + 10);
                newFragment.setMaxValue(100);
                newFragment.setValue(tempOld);
                newFragment.show(getSupportFragmentManager(), "temp max picker");
            }
        });

        linearStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final int hour = Integer.parseInt(textViewStartHour.getText().toString());
//                final int minute = Integer.parseInt(textViewStartMinute.getText().toString());
//
//                TimeDialog timeDialog = new TimeDialog();
//                timeDialog.setId(R.id.linear_start_time);
//                timeDialog.setTitle("Start time");
//                timeDialog.setHour(hour);
//                timeDialog.setMinute(minute);
//                timeDialog.show(getFragmentManager(), "timePicker");
//                timeDialog.setTargetFragment(ConfigFragment.this, 1);
            }
        });

        linearEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final int hour = Integer.parseInt(textViewEndHour.getText().toString());
//                final int minute = Integer.parseInt(textViewEndMinute.getText().toString());
//
//                TimeDialog timeDialog = new TimeDialog();
//                timeDialog.setId(R.id.linear_end_time);
//                timeDialog.setTitle("End time");
//                timeDialog.setHour(hour);
//                timeDialog.setMinute(minute);
//                timeDialog.show(getFragmentManager(), "timePicker");
//                timeDialog.setTargetFragment(ConfigFragment.this, 1);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String text = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                timeUp = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfig(userId, boardToken);
            }
        });

        buttonNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SmartConfigWiFI.class));
            }
        });
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.config, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_config :
//                startActivity(new Intent(getApplicationContext(), SmartConfigWiFI.class));
//                return true;
//            default: return super.onOptionsItemSelected(item);
//        }
//
//    }

    @Override
    public void getValue(int id, int value) {
        switch (id) {
            case R.id.text_view_bright:
                textViewBright.setText(String.valueOf(value));
                break;
//            case R.id.text_view_temp:
//                textViewTemp.setText(String.valueOf(value));
//                break;
            case R.id.text_view_temp_min :
                tempMinText.setText(String.valueOf(value));
                break;
            case R.id.text_view_temp_max :
                tempMaxText.setText(String.valueOf(value));
                break;
        }
    }
//
//    @Override
//    public void getTime(int id, int hour, int minute) {
//        switch (id) {
//            case R.id.linear_start_time:
//                textViewStartHour.setText((hour/10 == 0)? ("0" + String.valueOf(hour)) : String.valueOf(hour));
//                textViewStartMinute.setText((minute/10 == 0)? ("0" + String.valueOf(minute)) : String.valueOf(minute));
//                break;
//            case R.id.linear_end_time:
//                textViewEndHour.setText((hour/10 == 0)? ("0" + String.valueOf(hour)) : String.valueOf(hour));
//                textViewEndMinute.setText((minute/10 == 0)? ("0" + String.valueOf(minute)) : String.valueOf(minute));
//                break;
//        }
//    }

//    private void setBoardArrayList(String id) {
//        Call<User> boardCall = restAPI.getQsfService().getDevice(id);
//        boardCall.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                int status = response.code();
//                if(status == 200) {
//                    User user = response.body();
//
//                    for(Device board : user.getDevice()) {
//                        boardTokens.add(board.getToken());
//                        boardNames.add(board.getName());
//                        deviceList.add(new Device(board.getToken(), board.getName(), board.getBrightness(), board.getTemperature(), board.getStart(), board.getEnd()));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//
//            }
//        });
//    }

    private void initConfig(final String id, final String token) {
//        String id = sp.getString("ID", "");
//        String token = sp.getString("BOARD_TOKEN", "");

        Toast.makeText(getApplicationContext(), id + " " + token, Toast.LENGTH_SHORT).show();

        Call<DeviceListResponse> call = restAPI.getQsfService().getBoardByIdAndToken(id, token);
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                int status = response.code();

                if(status == 200) {
                    Device device = response.body().getDevices().get(0);

//                    System.out.println(devices.get(0).getBrightness());
//
//                    for(Device device : devices) {
//                        System.out.println(device.getBrightness());
//                        System.out.println(device.getTemperature());
//                        System.out.println(device.getStart());
//                        System.out.println(device.getEnd());
//                    }
//                    Device device = devices.get(0);
//                    Toast.makeText(getContext(), device.getBrightness(), Toast.LENGTH_SHORT).show();

                    int bright = device.getBrightness();
                    int temp = device.getTemperature();
                    int tempMin = device.getTempMin();
                    int tempMax = device.getTempMax();
                    String timeUp = device.getTimeUp();
                    String start = device.getStart();
                    String end = device.getEnd();

//                    Toast.makeText(getContext(), String.valueOf(temp), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), String.valueOf(bright), Toast.LENGTH_SHORT).show();

//                    Toast.makeText(getContext(), device.getBrightness(), Toast.LENGTH_SHORT).show();

                    textViewBright.setText(String.valueOf(bright));
//                    textViewTemp.setText(String.valueOf(temp));
                    tempMinText.setText(String.valueOf(tempMin));
                    tempMaxText.setText(String.valueOf(tempMax));
                    spinner.setSelection(getIndex(spinner, timeUp));
//                    textViewStartHour.setText(start.substring(0, 2));
//                    textViewStartMinute.setText(start.substring(2));
//                    textViewEndHour.setText(end.substring(0, 2));
//                    textViewEndMinute.setText(end.substring(2));

                    progressBar.setVisibility(View.GONE);
                } else if(status == 204) {
                    Toast.makeText(getApplicationContext(), "No content", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                Log.e("Init Config", t.getMessage());
            }
        });
    }

    private void saveConfig(final String id, final String token) {
        final int brightness = Integer.valueOf(textViewBright.getText().toString());
//        final int temperature = Integer.valueOf(textViewTemp.getText().toString());
        final int tempMin = Integer.parseInt(tempMinText.getText().toString());
        final int tempMax = Integer.parseInt(tempMaxText.getText().toString());
        String start = textViewStartHour.getText().toString() + textViewStartMinute.getText().toString();
        String end = textViewEndHour.getText().toString() + textViewEndMinute.getText().toString();

//        Device device = new Device(500, 43, "15");
        Device device = new Device(brightness, tempMin, tempMax, timeUp);

//        String id = sp.getString("ID", "");
//        final String token = sp.getString("BOARD_TOKEN", "");

        Log.e("ID Config", id);
        Log.e("TOKEN Config", token);

        Call<Device> call = restAPI.getQsfService().editBoard(id, token, device);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                int status = response.code();

                if(status == 204) {
                    Toast.makeText(getApplicationContext(), "Configs Success", Toast.LENGTH_SHORT).show();
                    if(mqtt.isConnected()) {
//                        Toast.makeText(ConfigActivity.this, "MQTT Connected", Toast.LENGTH_SHORT).show();
                        mqtt.publish(token + "/config/brightness", String.valueOf(brightness));
                        mqtt.publish(token + "/config/tempMin", String.valueOf(tempMin));
                        mqtt.publish(token + "/config/tempMax", String.valueOf(tempMax));
                        mqtt.publish(token + "/config/time", String.valueOf(timeUp));

                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Configs Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                Log.e("Save Config", t.getMessage());
            }
        });
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
