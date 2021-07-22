package com.example.todonote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    int AUTHUI_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //to match the status bar color with layout color
        Window window=LoginActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.white));

        //check the user is not null.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }

    // Handle to login page..id(button)
    public void handleLoginRegister(View view) {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
               // new AuthUI.IdpConfig.GoogleBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(provider)
                        .setTheme(R.style.LoginTheme)
                        .setTosAndPrivacyPolicyUrls("http://example.coms", "http://examples.com")
                        .setLogo(R.drawable.front_icon)
                        .setAlwaysShowSignInMethodScreen(true)
                        .build(),
                AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHUI_REQUEST_CODE)//check request code is current User {
            if (resultCode == RESULT_OK) {
                //we have signed in the user or have a new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.getEmail());
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    Toast.makeText(this, "Welcome New User..!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Welcome Back Again..!", Toast.LENGTH_SHORT).show();
                }
                //when the user to start the back button to again to ready the MainActivity.
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();

            } else {
                //Signing Failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                //to check user to acton on the page or not.
                if (response == null) {
                    Log.d(TAG, "onActivityResult: The user has cancelled the signing request");
                } else {
                    Log.d(TAG, "onActivityResult: ", response.getError());
                }
            }
    }
}
