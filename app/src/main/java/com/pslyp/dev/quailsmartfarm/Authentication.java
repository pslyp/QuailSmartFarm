package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.api.qsfService;
import com.pslyp.dev.quailsmartfarm.models.Status;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Authentication extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ERROR";
    private TextView logInTV, createAccountTV;
    private Button testAPI;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    boolean isConnected;

    RestAPI restAPI;
    String data = null;

    //Facebook Signin
    private LoginButton loginButton;

    //Google Sign-in
    Google google;

    //MQTT
    MQTT mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = google.lastSignIn();
        updateUI(account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_create_new_account :
                createAccount();
                break;
            case R.id.sign_in_button :
                if(isConnected)
                    signIn();
                break;
            case R.id.text_view_log_in :
                logIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == google.RC_SIGN_IN()) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void initInstance() {
        logInTV = findViewById(R.id.text_view_log_in);
        createAccountTV = findViewById(R.id.text_view_create_new_account);
        testAPI = findViewById(R.id.test_api_button);

        findViewById(R.id.text_view_log_in).setOnClickListener(this);
        findViewById(R.id.text_view_create_new_account).setOnClickListener(this);
        //findViewById(R.id.test_api_button).setOnClickListener(this);

        //restAPI = new RestAPI();

        //restAPI.create(qsfService.class);

//        //Retrofit
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://quailsmartfarm.herokuapp.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        qsfService = retrofit.create(qsfService.class);
        //setUser(qsfService);


        /*
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        */

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://quailsmartfarm.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final qsfService qsfService = retrofit.create(qsfService.class);

        testAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = qsfService.getUsers();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User userResponse = response.body();
                        String id = userResponse.getId();

                        Toast.makeText(Authentication.this, id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }
        });

        google = new Google(this);
        mqtt = new MQTT(this);
        restAPI = new RestAPI();

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // Set on click button
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        isConnected = networkInfos != null &&
                      networkInfos.isConnected();

        if(isConnected)
            mqtt.connected();
        else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.authenLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    //Open Create Account Activity
    private void createAccount() {
        startActivity(new Intent(Authentication.this, CreateAccount.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void setUser(String id, String data) {
        Call<User> resCall = restAPI.getQsfService().checkUser(id);
        resCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()) {
                    Log.e(TAG, String.valueOf(response.code()));
                    return;
                }

                User userResponse = response.body();
                String id = userResponse.getId();

                Toast.makeText(Authentication.this, id, Toast.LENGTH_SHORT).show();

//                Log.e(TAG, status);
//                //publish("user/create", data);
//
//                if(status.equals("Not Found"))
//                    Log.e(TAG, status);
//                    //publish("user/create", data);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Authentication.this, "Set User Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Open Login Activity
    private void logIn() {
        Intent logInIntent = new Intent(Authentication.this, LogIn.class);
        startActivity(logInIntent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //finish();
    }

    private void signIn() {
        Intent signInIntent = google.mGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, google.RC_SIGN_IN());
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account != null) {
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

            editor = sp.edit();
            editor.putBoolean("log_in", true);
            editor.putString("id", personId);
            editor.putString("first_name", personGivenName);
            editor.putString("last_name", personFamilyName);
            editor.putString("email", personEmail);
            editor.commit();

            data = (personId + "-" + personGivenName + "-" + personFamilyName + "-" + personEmail);

            //String result = (personName + "\n" + personGivenName + "\n" + personFamilyName + "\n" + personEmail + "\n" + personId);

            setUser(personId, data);
            //mqtt.publish("user/create", data);

            //AlertDialog.Builder builder = new AlertDialog.Builder(Authentication.this);
            //builder.setMessage(result);
            //builder.show();

            Intent intent = new Intent(Authentication.this, MainActivity.class);
            //intent.putExtra("user", data);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Not SignIn", Toast.LENGTH_SHORT).show();
        }
    }

}
