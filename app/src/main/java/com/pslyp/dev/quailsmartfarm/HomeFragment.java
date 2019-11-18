package com.pslyp.dev.quailsmartfarm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.activities.AddBoardActivity;
import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Device;
import com.pslyp.dev.quailsmartfarm.models.User;

//import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private DeviceData deviceData;

//    private MQTT mqtt;

    private RestAPI restAPI;
    private ArrayList<String> nameDeviceList;
    private List<Device> deviceList;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    private LinearLayout linearLayout1;
    private ListView mDeviceListView;
    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar).setSubtitle("Your Title");

        initInstance(view);

        return view;
    }


    public void initInstance(View view) {
//        mqtt = new MQTT(getContext());
//        mqtt.connectMQTT();

        restAPI = new RestAPI();

        nameDeviceList = new ArrayList<>();

        deviceData = DeviceData.getInstance();
        deviceData.deviceArrayList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar_home);
        linearLayout1 = view.findViewById(R.id.linear_layout_1);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddBoardActivity.class));
            }
        });
        mDeviceListView = view.findViewById(R.id.list_view_device);
        mDeviceListView.setOnItemClickListener(mDeviceClick);

        sp = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String id = sp.getString("ID", "");

        setDeviceList(id);
    }

    public AdapterView.OnItemClickListener mDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent = new Intent(getActivity().getBaseContext(), DeviceDashBoard.class);

//            intent.putExtra("SENDER_KEY", "Home");
//            intent.putExtra("TOKEN", deviceList.get(position).getToken());

            String topic = (deviceList.get(position).getToken() + "/cloudMessage");
            String personToken = sp.getString("PERSON_TOKEN", "");
            Toast.makeText(getContext(), personToken, Toast.LENGTH_SHORT).show();

            if (!sp.getBoolean("MSG_FIRST", false)) {
//                mqtt.publish(topic, personToken.substring(0, 70) + ">1");
//                mqtt.publish(topic, personToken.substring(70, 140) + ">2");
//                mqtt.publish(topic, personToken.substring(140) + ">3");
//                mqtt.publish(deviceList.get(position).getToken() + "/save", "msg");

                editor = sp.edit();
                editor.putBoolean("MSG_FIRST", true);
                editor.commit();
            }

//            getActivity().startActivity(intent);
        }
    };

    private void setDeviceList(String id) {
        Call<User> boardCall = restAPI.getQsfService().getBoard(id);
        boardCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int status = response.code();

                if(status == 200) {
                    User user = response.body();
//                    List<Device> boards = user.getDevice();
                    deviceList = user.getDevice();

//                    for(Device board : boards) {
//                        nameDeviceList.add(board.getName());
//                    }

//                    NameDeviceListAdapter adapter = new NameDeviceListAdapter(getContext(), R.layout.device_item, nameDeviceList);
                    DeviceListAdapter adapter = new DeviceListAdapter(getContext(), R.layout.device_item, deviceList);
                    mDeviceListView.setAdapter(adapter);
                }

                progressBar.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
                mDeviceListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

}
