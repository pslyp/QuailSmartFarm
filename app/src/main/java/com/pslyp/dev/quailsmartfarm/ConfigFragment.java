package com.pslyp.dev.quailsmartfarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Board;
import com.pslyp.dev.quailsmartfarm.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends Fragment implements NumberDialog.NumberDialogListener, TimeDialog.TimeDialogListener {

    private RestAPI restAPI;

    private Button buttonSave, buttonNet;
    private MaterialSpinner device;
    private LinearLayout linearStartTime, linearEndTime;
    private TextView textViewBright, textViewTemp, textViewStartHour, textViewStartMinute, textViewEndHour, textViewEndMinute;

    private SharedPreferences sp;
    private final String PREF_NAME = "LoginPreferences";

    private List<String> boardTokens;
    private List<String> boardNames;
    private List<Board> boardList;

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        restAPI = new RestAPI();
        boardTokens = new ArrayList<>();
        boardNames = new ArrayList<>();
        boardList = new ArrayList<>();

        device = view.findViewById(R.id.spinner_device);
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

        sp = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        final String id = sp.getString("ID", "");

        setBoardArrayList(id);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, boardNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        device.setAdapter(adapter);
        device.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position != -1) {
                    Toast.makeText(getContext(), position + item.toString() + "Token: " + boardTokens.get(position), Toast.LENGTH_SHORT).show();
                    textViewBright.setText(String.valueOf(boardList.get(position).getBrightness()));
                    textViewTemp.setText(String.valueOf(boardList.get(position).getTemperature()));
                    textViewStartHour.setText(String.valueOf(boardList.get(position).getStart().substring(0, 2)));
                    textViewStartMinute.setText(String.valueOf(boardList.get(position).getStart().substring(2)));
                    textViewEndHour.setText(String.valueOf(boardList.get(position).getEnd().substring(0, 2)));
                    textViewEndMinute.setText(String.valueOf(boardList.get(position).getEnd().substring(2)));
                }
            }
        });

        textViewBright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int brightOld = Integer.valueOf(textViewBright.getText().toString());

                NumberDialog numberDialog = new NumberDialog();
                numberDialog.setId(R.id.text_view_bright);
                numberDialog.setTitle("Brightness");
                numberDialog.setMinValue(0);
                numberDialog.setMaxValue(1000);
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
                timeDialog.setTitle("Start time");
                timeDialog.setHour(hour);
                timeDialog.setMinute(minute);
                timeDialog.show(getFragmentManager(), "timePicker");
                timeDialog.setTargetFragment(ConfigFragment.this, 1);
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
            case R.id.linear_end_time:
                textViewEndHour.setText((hour/10 == 0)? ("0" + String.valueOf(hour)) : String.valueOf(hour));
                textViewEndMinute.setText((minute/10 == 0)? ("0" + String.valueOf(minute)) : String.valueOf(minute));
                break;
        }
    }

    private void setBoardArrayList(String id) {
        Call<User> boardCall = restAPI.getQsfService().getBoard(id);
        boardCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int status = response.code();
                if(status == 200) {
                    User user = response.body();

                    for(Board board : user.getBoard()) {
                        boardTokens.add(board.getToken());
                        boardNames.add(board.getName());
                        boardList.add(new Board(board.getToken(), board.getName(), board.getBrightness(), board.getTemperature(), board.getStart(), board.getEnd()));
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void saveConfig() {

    }


}
