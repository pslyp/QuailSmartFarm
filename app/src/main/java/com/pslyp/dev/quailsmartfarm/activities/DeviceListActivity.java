package com.pslyp.dev.quailsmartfarm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.Device;
import com.pslyp.dev.quailsmartfarm.models.DeviceListResponse;
import com.pslyp.dev.quailsmartfarm.models.Temp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceListActivity extends AppCompatActivity {

    private TextView mTitle, noDevice;
    private TextInputLayout boardIdText, boardNameText;
    private RecyclerView recyclerView;

    private ListView listView;
    private ProgressBar progressBar;

    private FloatingActionButton addBoardFloat;

    private RestAPI restAPI;
    private DeviceListAdapter adapter;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setTitle("Devices");

        initInstance();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_menu :
//                startActivity(new Intent(DeviceListActivity.this, AddBoardActivity.class));
//                return true;
//                default: return super.onOptionsItemSelected(item);
//        }
//    }

    private void initInstance() {
        restAPI = new RestAPI();

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        userId = sp.getString("ID", "");

        noDevice = findViewById(R.id.no_device_text_view);
        listView = findViewById(R.id.list_view);
        progressBar = findViewById(R.id.device_list_progress_bar);

        //Recycler View
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        addBoardFloat = findViewById(R.id.add_board_float_button);
        addBoardFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(DeviceListActivity.this, "Add Device", Toast.LENGTH_SHORT).show();

//                startActivity(new Intent(DeviceListActivity.this, AddBoardActivity.class));

                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
                View alertDialogView = getLayoutInflater().inflate(R.layout.layout_dialog_add_board, null);

//                TextView tokenTextView = alertDialogView.findViewById(R.id.text_view_token);
//                tokenTextView.setText("ASFASGFAD");

                boardIdText = alertDialogView.findViewById(R.id.text_input_board_id);
                boardNameText = alertDialogView.findViewById(R.id.text_input_board_name);
                Button submit = alertDialogView.findViewById(R.id.submit_button);

                submit.setVisibility(View.GONE);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String boardId = boardIdText.getEditText().getText().toString().trim();
                        String boardName = boardNameText.getEditText().getText().toString().trim();

                        if(!textInputEmpty(boardId, boardName)) {

                            String token = MD5.getInstance().create(boardId);
//
//                            if(!boardAlready(userId, token)) {
//                                addBoard(userId, token, boardName);
//                            }
                        }
                    }
                });


                builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String boardId = boardIdText.getEditText().getText().toString().trim();
                        String boardName = boardNameText.getEditText().getText().toString().trim();

                        if(!textInputEmpty(boardId, boardName)) {

                            String token = MD5.getInstance().create(boardId);

                            boardAlready(userId, token, boardName);

                            progressBar.setVisibility(View.VISIBLE);
//
//                            if (!already) {
//                                progressBar.setVisibility(View.VISIBLE);
//                                addBoard(userId, token, boardName);
//                            }
                        }
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setView(alertDialogView);

                AlertDialog dialog = builder.create();
//                dialog.setContentView(R.layout.layout_dialog_add_board);
                dialog.show();
                dialog.getWindow().setLayout(1000, 1250);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        mTitle.setText("Devices");

//        Call<DeviceListResponse> call = restAPI.getQsfService().getBoardById(userId);
//        call.enqueue(new Callback<DeviceListResponse>() {
//            @Override
//            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
//                int status = response.code();
//
//                if(status == 200) {
//                    DeviceListResponse res = response.body();
//                    final List<Device> deviceList = res.getDevices();
//
////                    DeviceRecyclerAdapter adapter = new DeviceRecyclerAdapter(getApplicationContext(), deviceList);
////
////                    recyclerView.setAdapter(adapter);
////
////                    adapter.setOnItemClickListener(new DeviceRecyclerAdapter.OnItemClickListener() {
////                        @Override
////                        public void onItemClick(Device item) {
////                            Toast.makeText(DeviceListActivity.this, item.getName(), Toast.LENGTH_SHORT).show();
////
////                            Intent intent = new Intent(DeviceListActivity.this, DashboardActivity.class);
////                            intent.putExtra("TOKEN", item.getToken());
////                            startActivity(intent);
////                        }
////                    });
//
//                    adapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_item, deviceList);
//                    listView.setAdapter(adapter);
//
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                            Intent intent = new Intent(DeviceListActivity.this, DashboardActivity.class);
//                            intent.putExtra("TOKEN", deviceList.get(position).getToken());
//                            startActivity(intent);
//
//                            Log.e("Token", deviceList.get(position).getToken());
//                        }
//                    });
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
//                Log.e("Get Device By ID", t.getMessage());
//            }
//        });

        initListView();
    }

    private void initListView() {
        Call<DeviceListResponse> call = restAPI.getQsfService().getBoardById(userId);
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                int status = response.code();

                if(status == 200) {
                    DeviceListResponse res = response.body();
                    final List<Device> deviceList = res.getDevices();

                    if(deviceList != null) {

//                    DeviceRecyclerAdapter adapter = new DeviceRecyclerAdapter(getApplicationContext(), deviceList);
//
//                    recyclerView.setAdapter(adapter);
//
//                    adapter.setOnItemClickListener(new DeviceRecyclerAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(Device item) {
//                            Toast.makeText(DeviceListActivity.this, item.getName(), Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(DeviceListActivity.this, DashboardActivity.class);
//                            intent.putExtra("TOKEN", item.getToken());
//                            startActivity(intent);
//                        }
//                    });

                        Log.e("BBBBBB", String.valueOf(deviceList.get(0).getBrightness()));

                        adapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_item, deviceList);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Intent intent = new Intent(DeviceListActivity.this, DashboardActivity.class);
                                intent.putExtra("TOKEN", deviceList.get(position).getToken());
                                startActivity(intent);

//                                Log.e("Token", deviceList.get(position).getToken());
                            }
                        });

                        noDevice.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        noDevice.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                Log.e("Get Device By ID", t.getMessage());
            }
        });
    }

    private boolean textInputEmpty(final String id, final String name) {
        if(id.isEmpty() || name.isEmpty()) {
            if(id.isEmpty()) {
                boardIdText.setError("Device ID can't be empty");
            } else {
                boardIdText.setError(null);
            }
            if(name.isEmpty()) {
                boardNameText.setError("Device Name can't be empty");
            } else{
                boardNameText.setError(null);
            }
            return true;
        } else {
            boardIdText.setError(null);
            boardNameText.setError(null);
            return false;
        }
    }

    private void boardAlready(final String id, final String token, final String name) {
//        final String boardToken = MD5.getInstance().create(boardId);

//            Toast.makeText(this, "Lenght 0", Toast.LENGTH_SHORT).show();
//        String board_token = md5.create(boardTokenTextInput.getEditText().getText().toString());

        Log.e("Add", id);

        Call<DeviceListResponse> call = restAPI.getQsfService().getBoardByIdAndToken(id, token);
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                int status = response.code();

                Log.e("Status Already", String.valueOf(status));
//                Log.e("AAAAA", String.valueOf(response.body().getDevices().get(0).getBrightness()));
//                Log.e("_id", response.body().get_id());

//                Toast.makeText(AddBoardActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();

                if(status == 200) {
                    Toast.makeText(DeviceListActivity.this, "Device ID is already used", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                if(status == 204){
                    addBoard(id, token, name);
                }
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                Log.e("Device Already", t.getMessage());
            }
        });
    }

    private void addBoard(final String id, final String token, final String name) {
        final String personToken = sp.getString("PERSON_TOKEN", "");

        editor = sp.edit();
        editor.putString("BOARD_TOKEN", token);
        editor.commit();

        Call<Device> call = restAPI.getQsfService().insertBoard(id, new Device(token, name, 3000, 20, 70, "15", "0800", "2300"));
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                int status = response.code();

                if(status == 204) {
//                    if(mqtt.isConnected()) {
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(0, 70) + ">1");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(70, 140) + ">2");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(140) + ">3");
//                    }

                    Toast.makeText(DeviceListActivity.this, "Add Device success", Toast.LENGTH_SHORT).show();

                    initListView();

//                    startActivity(new Intent(DeviceListActivity.this, MainActivity.class));
//                    finish();
                } else {
                    Toast.makeText(DeviceListActivity.this, "Add Device fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {

            }
        });
    }
}
