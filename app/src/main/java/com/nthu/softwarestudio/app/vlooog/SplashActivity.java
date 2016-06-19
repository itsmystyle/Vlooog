package com.nthu.softwarestudio.app.vlooog;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = SplashActivity.class.getSimpleName();
    private AccountHelper accountHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        accountHelper = new AccountHelper(this);

        if(accountHelper.isEmpty()){
            //Login
            Intent intent = new Intent(this, LoginActivity.class);
            Log.v(LOG_TAG, "Login Activity Start");
            startActivity(intent);
            finish();

        }else{
            //No need to login
            Intent intent = new Intent(this, MainActivity.class);
            Log.v(LOG_TAG, "Main Activity Start");
            startActivity(intent);
            finish();

        }
    }
}
