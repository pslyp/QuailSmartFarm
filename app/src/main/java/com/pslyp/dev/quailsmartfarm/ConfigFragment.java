package com.pslyp.dev.quailsmartfarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Device;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends Fragment implements NumberDialog.NumberDialogListener, TimeDialog.TimeDialogListener {

    private MQTT mqtt;
    private RestAPI restAPI;

    private Button buttonSave, buttonNet;
    private MaterialSpinner device;
    private LinearLayout linearStartTime, linearEndTime;
    private Spinner spinner;
    private TextView textViewBright, textViewTemp, textViewStartHour, textViewStartMinute, textViewEndHour, textViewEndMinute;

    private SharedPreferences sp;
    private final String PREF_NAME = "LoginPreferences";

    private List<String> boardTokens;
    private List<String> boardNames;
    private List<Device> deviceList;

    private String timeUp;

    public ConfigFragment() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);

//        setHasOptionsMenu(true);

        mqtt = new MQTT(getContext());
        restAPI = new RestAPI();
        boardTokens = new ArrayList<>();
        boardNames = new ArrayList<>();
        deviceList = new ArrayList<>();

//        device = view.findViewById(R.id.spinner_device);
        spinner = view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.time, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setDropDownWidth(180);

        textViewBright = view.findViewById(R.id.text_view_bright);
        textViewTemp = view.findViewById(R.id.text_view_temp);
        textViewStartHour = view.findViewById(R.id.text_view_start_hour);
        textViewStartMinute = view.findViewById(R.id.text_view_start_minute);
        textViewEndHour = view.findViewById(R.id.text_view_end_hour);
        textViewEndMinute = view.findViewById(R.id.text_view_end_minute);
        linearStartTime = view.findViewById(R.id.linear_start_time);
        linearEndTime = view.findViewById(R.id.linear_end_time);
        buttonSave = view.findViewById(R.id.button_save);
        buttonNet = view.findViewById(R.id.button_internet);

        mqtt.connect();

        sp = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        setConfig();

//        final String id = sp.getString("ID", "");

//        setBoardArrayList(id);
//        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, boardNames);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        device.setAdapter(adapter);
//        device.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                if(position != -1) {
//                    Toast.makeText(getContext(), position + item.toString() + "Token: " + boardTokens.get(position), Toast.LENGTH_SHORT).show();
//                    textViewBright.setText(String.valueOf(deviceList.get(position).getBrightness()));
//                    textViewTemp.setText(String.valueOf(deviceList.get(position).getTemperature()));
//                    textViewStartHour.setText(String.valueOf(deviceList.get(position).getStart().substring(0, 2)));
//                    textViewStartMinute.setText(String.valueOf(deviceList.get(position).getStart().substring(2)));
//                    textViewEndHour.setText(String.valueOf(deviceList.get(position).getEnd().substring(0, 2)));
//                    textViewEndMinute.setText(String.valueOf(deviceList.get(position).getEnd().substring(2)));
//                }
//            }
//        });

        textViewBright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int brightOld = Integer.valueOf(textViewBright.getText().toString());

                NumberDialog numberDialog = new NumberDialog();
                numberDialog.setId(R.id.text_view_bright);
                numberDialog.setTitle("Brightness");
                numberDialog.setMinValue(0);
                numberDialog.setMaxValue(4095);
                numberDialog.setValue(brightOld);
                numberDialog.show(getFragmentManager(), "bright dialog");
                numberDialog.setTargetFragment(ConfigFragment.this, 1);
            }
        });

        textViewTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int tempOld = Integer.valueOf(textViewTemp.getText().toString());

                NumberDialog numberDialog = new NumberDialog();
                numberDialog.setId(R.id.text_view_temp);
                numberDialog.setTitle("Temperature");
                numberDialog.setMinValue(0);
                numberDialog.setMaxValue(100);
                numberDialog.setValue(tempOld);
                numberDialog.show(getFragmentManager(), "temp dialog");
                numberDialog.setTargetFragment(ConfigFragment.this, 1);
            }
        });

        linearStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int hour = Integer.parseInt(textViewStartHour.getText().toString());
                final int minute = Integer.parseInt(textViewStartMinute.getText().toString());

                TimeDialog timeDialog = new TimeDialog();
                timeDialog.setId(R.id.linear_start_time);
                timeDialog.setTitle("Start time");
                timeDialog.setHour(hour);
                timeDialog.setMinute(minute);
                timeDialog.show(getFragmentManager(), "timePicker");
                timeDialog.setTargetFragment(ConfigFragment.this, 1);
            }
        });

        linearEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int hour = Integer.parseInt(textViewEndHour.getText().toString());
                final int minute = Integer.parseInt(textViewEndMinute.getText().toString());

                TimeDialog timeDialog = new TimeDialog();
                timeDialog.setId(R.id.linear_end_time);
                timeDialog.setTitle("End time");
                timeDialog.setHour(hour);
                timeDialog.setMinute(minute);
                timeDialog.show(getFragmentManager(), "timePicker");
                timeDialog.setTargetFragment(ConfigFragment.this, 1);
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
                saveConfig();
            }
        });

        buttonNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SmartConfigWiFI.class));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.config, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_config :
                startActivity(new Intent(getContext(), SmartConfigWiFI.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void getValue(int id, int value) {
        switch (id) {
            case R.id.text_view_bright:
                textViewBright.setText(String.valueOf(value));
                break;
            case R.id.text_view_temp:
                textViewTemp.setText(String.valueOf(value));
                break;
        }
    }

    @Override
    public void getTime(int id, int hour, int minute) {
        switch (id) {
            case R.id.linear_start_time:
                textViewStartHour.setText((hour/10 == 0)? ("0" + String.valueOf(hour)) : String.valueOf(hour));
                textViewStartMinute.setText((minute/10 == 0)? ("0" + String.valueOf(minute)) : String.valueOf(minute));
                break;
            case R.id.linear_end_time:
                textViewEndHour.setText((hour/10 == 0)? ("0" + String.valueOf(hour)) : String.valueOf(hour));
                textViewEndMinute.setText((minute/10 == 0)? ("0" + String.valueOf(minute)) : String.valueOf(minute));
                break;
        }
    }

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

    private void setConfig() {
        String id = sp.getString("ID", "");
        String token = sp.getString("BOARD_TOKEN", "");

        Toast.makeText(getContext(), id + " " + token, Toast.LENGTH_SHORT).show();

//        Call<User> call = restAPI.getQsfService().getBoardByToken(id, token);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                int status = response.code();
//
//                if(status == 200) {
//                    User user = response.body();
//                    List<Device> devices = user.getDevice();
//
////                    System.out.println(devices.get(0).getBrightness());
////
////                    for(Device board : devices) {
////                        System.out.println(board.getBrightness());
////                        System.out.println(board.getTemperature());
////                        System.out.println(board.getStart());
////                        System.out.println(board.getEnd());
////                    }
////                    Device board = devices.get(0);
////                    Toast.makeText(getContext(), board.getBrightness(), Toast.LENGTH_SHORT).show();
//
//                    int bright = devices.get(0).getBrightness();
//                    int temp = devices.get(0).getTemperature();
//                    String timeUp = devices.get(0).getTimeUp();
//                    String start = devices.get(0).getStart();
//                    String end = devices.get(0).getEnd();
//
////                    Toast.makeText(getContext(), String.valueOf(temp), Toast.LENGTH_SHORT).show();
////                    Toast.makeText(getContext(), String.valueOf(bright), Toast.LENGTH_SHORT).show();
//
////                    Toast.makeText(getContext(), board.getBrightness(), Toast.LENGTH_SHORT).show();
//
//                    textViewBright.setText(String.valueOf(bright));
//                    textViewTemp.setText(String.valueOf(temp));
//                    spinner.setSelection(getIndex(spinner, timeUp));
////                    textViewStartHour.setText(start.substring(0, 2));
////                    textViewStartMinute.setText(start.substring(2));
////                    textViewEndHour.setText(end.substring(0, 2));
////                    textViewEndMinute.setText(end.substring(2));
//                } else if(status == 204) {
//                    Toast.makeText(getContext(), "No content", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//
//            }
//        });

    }

    private void saveConfig() {
        int brightness = Integer.valueOf(textViewBright.getText().toString());
        int temperature = Integer.valueOf(textViewTemp.getText().toString());
        String start = textViewStartHour.getText().toString() + textViewStartMinute.getText().toString();
        String end = textViewEndHour.getText().toString() + textViewEndMinute.getText().toString();

//        Device device = new Device(brightness, temperature, start, end);
        Device device = new Device(brightness, temperature, timeUp);

        String id = sp.getString("ID", "");
        final String token = sp.getString("BOARD_TOKEN", "");

        Call<Device> call = restAPI.getQsfService().editBoard(id, token, device);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                int status = response.code();

                if(status == 204) {
                    Toast.makeText(getContext(), "Configs Success", Toast.LENGTH_SHORT).show();
                    if(mqtt.isConnected()) {
                        mqtt.publish("/" + token + "/configs", "YES");
                    }
                } else {
                    Toast.makeText(getContext(), "Configs Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {

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
