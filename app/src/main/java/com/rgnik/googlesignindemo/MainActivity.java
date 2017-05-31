package com.rgnik.googlesignindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout profile;
    private Button signout;
    private SignInButton signInButton;
    private TextView txtname;
    private TextView txtemail;
    private ImageView profilepic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = (LinearLayout)findViewById(R.id.linear_profile);
        signout = (Button)findViewById(R.id.buttonLogout);
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        txtname = (TextView)findViewById(R.id.textUserName);
        txtemail = (TextView)findViewById(R.id.textUserEmail);
        profilepic = (ImageView)findViewById(R.id.profile_pic);
        signInButton.setOnClickListener(this);
        signout.setOnClickListener(this);
        profile.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.buttonLogout:
                signOut();
                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult googleSignInResult)
    {
        if(googleSignInResult.isSuccess())
        {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String uname = googleSignInAccount.getDisplayName();
            String uemail = googleSignInAccount.getEmail();
            String img_url = googleSignInAccount.getPhotoUrl().toString();
            txtname.setText(uname);
            txtemail.setText(uemail);
            Glide.with(this).load(img_url).into(profilepic);
            updateUI(true);
        }
        else
            {
            updateUI(false);
        }

    }

    private void updateUI(boolean islogin)
    {
        if(islogin)
        {
            profile.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        }
        else
        {
            profile.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
