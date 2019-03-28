package com.pslyp.dev.quailsmartfarm;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Google {

    private Context context;
    private GoogleSignInClient mGoogleSignInClient;

    //private final String TAG = "Signin Fail";
    private static final int RC_SIGN_IN = 1010;

    public Google(Context context) {
        this.context = context;

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public GoogleSignInClient mGoogleSignInClient() {
        return this.mGoogleSignInClient;
    }

    public int RC_SIGN_IN() {
        return this.RC_SIGN_IN;
    }

    public GoogleSignInAccount lastSignIn() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account;
    }

}
