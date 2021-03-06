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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pslyp.dev.quailsmartfarm.activities.SignUpActivity;
import com.pslyp.dev.quailsmartfarm.activities.LogInActivity;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authentication extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ERROR";
    private TextView logInTV, createAccountTV, test;
    private Button testAPI;
    private ImageView acc_pic;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    //Internet
    boolean isConnected;

    RestAPI restAPI;
    String data = null;

    //Facebook Signin
    private LoginButton loginButton;

    //Google Sign-in
    Google google;

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
        test = findViewById(R.id.test_text_view);
        acc_pic = findViewById(R.id.image_view_account);

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
        loginButton.setReadPermissions("email_text");
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
        restAPI = new RestAPI();

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // Set on click button
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        //Internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        isConnected = networkInfos != null &&
                      networkInfos.isConnected();

        if(!isConnected) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.authenLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    //Open Create Account Activity
    private void createAccount() {
        startActivity(new Intent(Authentication.this, SignUpActivity.class));
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //finish();
    }

    private void setUser(String id, final User user) {
        Call<User> call = restAPI.getQsfService().checkUser(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e("Response", "not success");
                    return;
                }

                int status = response.code();
                if (status == 204) {
                    Toast.makeText(Authentication.this, "User sum", Toast.LENGTH_SHORT).show();
                    createUser(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void createUser(User user) {
        Call<User> call = restAPI.getQsfService().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()) {
                    Log.e("Response", "not success");
                    return;
                }

//                int status = response.code();
//                if(status == 200) {
//                    startActivity(new Intent(Authentication.this, MainActivity.class));
//                    finish();
//                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    //Open Login Activity
    private void logIn() {
        Intent logInIntent = new Intent(Authentication.this, LogInActivity.class);
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
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personToken = FirebaseInstanceId.getInstance().getToken();
            Uri personPhoto = account.getPhotoUrl();

            sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

            editor = sp.edit();
            editor.putBoolean("LOG_IN", true);
            editor.putString("ID", personId);
            editor.putString("FIRST_NAME", personGivenName);
            editor.putString("LAST_NAME", personFamilyName);
            editor.putString("EMAIL", personEmail);
            editor.putString("PERSON_TOKEN", personToken);
            editor.putString("URL_PHOTO", String.valueOf(personPhoto));
            editor.commit();

//            Glide.with(this).load(personPhoto).into(acc_pic);

            data = (personId + "-" + personGivenName + "-" + personFamilyName + "-" + personEmail);

            //String result = (personName + "\n" + personGivenName + "\n" + personFamilyName + "\n" + personEmail + "\n" + personId);

//            setUser(personId, new User(personId, personGivenName, personGivenName, personEmail, personToken));
            //mqtt.publish("user/create", data);

            //AlertDialog.Builder builder = new AlertDialog.Builder(Authentication.this);
            //builder.setMessage(result);
            //builder.show();

            Toast.makeText(this, personToken, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Authentication.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Not SignIn", Toast.LENGTH_SHORT).show();
        }
    }

}
