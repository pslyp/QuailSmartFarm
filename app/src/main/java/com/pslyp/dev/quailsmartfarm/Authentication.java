package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Authentication extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ERROR";
    private TextView logInTV, createAccountTV;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    boolean isConnected;

    //Facebook Signin
    private LoginButton loginButton;

    //Google Sign-in
    Google google;

    //MQTT
    String clientId;
    MqttAndroidClient client;
    IMqttToken token;

    String MQTTHOST = "tcp://35.240.137.230:1883";
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        setTitle("");

        initInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = google.getLastSignIn();
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
        if (requestCode == google.getRcSignIn()) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void initInstance() {
        logInTV = findViewById(R.id.text_view_log_in);
        createAccountTV = findViewById(R.id.text_view_create_new_account);

        findViewById(R.id.text_view_log_in).setOnClickListener(this);
        findViewById(R.id.text_view_create_new_account).setOnClickListener(this);

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

        google = new Google(this);

        google.GSO();

        /*
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        */

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
            connectMQTT();
        else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.authenLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    //Open Create Account Activity
    private void createAccount() {
        Intent intent = new Intent(Authentication.this, CreateAccount.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    //Open Login Activity
    private void logIn() {
        Intent logInIntent = new Intent(Authentication.this, LogIn.class);
        startActivity(logInIntent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //finish();
    }

    private void signIn() {
        Intent signInIntent = google.getmGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, google.getRcSignIn());
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

            String data = (personId + "-" + personGivenName + "-" + personFamilyName + "-" + personEmail);

            String result = (personName + "\n" + personGivenName + "\n" + personFamilyName + "\n" + personEmail + "\n" + personId);

            publish("user/create", data);

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

    public void connectMQTT() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
