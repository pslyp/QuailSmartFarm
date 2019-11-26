package com.pslyp.dev.quailsmartfarm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pslyp.dev.quailsmartfarm.Authentication;
import com.pslyp.dev.quailsmartfarm.Google;
import com.pslyp.dev.quailsmartfarm.MainActivity;
import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    MD5 md5;
    RestAPI restAPI;
    Google google;

    Button login;
    TextView mTitle, mSignUpTextView;
    TextInputLayout email_text, pass_text;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    String data = null;
    String TAG = "Log in";
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("Log In");

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
    public void onBackPressed() {
        super.onBackPressed();

        //Intent authIntent = new Intent(LogInActivity.this, Authentication.class);
        //startActivity(authIntent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(LogInActivity.this, Authentication.class));
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_log_in :
                logIn();
                break;
            case R.id.sign_up_text_view :
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                break;
            case R.id.sign_in_button :
                if(isConnected)
                    signIn();
                break;
            default:
        }
    }

    private void initInstance() {
        md5 = new MD5();
        google = new Google(this);
        restAPI = new RestAPI();

        mTitle = findViewById(R.id.toolbar_title);
        login = findViewById(R.id.button_log_in);
        email_text = findViewById(R.id.text_input_email);
        pass_text = findViewById(R.id.text_input_password);
        mSignUpTextView = findViewById(R.id.sign_up_text_view);

        findViewById(R.id.button_log_in).setOnClickListener(this);
        findViewById(R.id.sign_up_text_view).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        mTitle.setText("Log In");
        setTitle("");


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

    private void logIn() {
        String email = email_text.getEditText().getText().toString();
        String pass = md5.create(pass_text.getEditText().getText().toString());

        email_text.setErrorEnabled(false);
        pass_text.setErrorEnabled(false);

        Call<User> call = restAPI.getQsfService().logIn(email, pass);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int status = response.code();
                Log.e("Code", String.valueOf(status));
                if(status == 200) {
                    User user = response.body();

                    String id = user.getId();
                    String first_name = user.getFirstname();
                    String last_name = user.getLastname();
                    String email = user.getEmail();
                    String personToken = FirebaseInstanceId.getInstance().getToken();

                    sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                    editor = sp.edit();
                    editor.putBoolean("LOG_IN", true);
                    editor.putString("ID", id);
                    editor.putString("FIRST_NAME", first_name);
                    editor.putString("LAST_NAME", last_name);
                    editor.putString("EMAIL", email);
                    editor.putString("PERSON_TOKEN", personToken);
                    editor.commit();

                    startActivity(new Intent(LogInActivity.this, DeviceListActivity.class));
                    finish();
                }
                if(status == 204) {
                    pass_text.setError("Invalid password");
                    Log.e("Login", "password");
                }
                if(status == 400){
                    email_text.setError("Invalid email");
                    Log.e("Login", "email");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Log in", t.getMessage());
            }
        });

    }

    private void signIn() {
        Intent signInIntent = google.mGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, google.RC_SIGN_IN());
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

            setUser(personId, new User(personId, personGivenName, personFamilyName, personEmail, personToken));
            //mqtt.publish("user/create", data);

            //AlertDialog.Builder builder = new AlertDialog.Builder(Authentication.this);
            //builder.setMessage(result);
            //builder.show();

            Toast.makeText(this, personToken, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LogInActivity.this, DeviceListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Not SignIn", Toast.LENGTH_SHORT).show();
        }
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
                    Toast.makeText(LogInActivity.this, "User sum", Toast.LENGTH_SHORT).show();
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

}
