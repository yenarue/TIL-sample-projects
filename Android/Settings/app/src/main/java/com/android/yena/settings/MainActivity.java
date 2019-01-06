package com.android.yena.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.yena.common.util.Log;

/**
 * Created by Yena on 2016-11-02.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_main);
    }

    public void goToSettings(View view) {
//        Intent intent = new Intent(this, SettingsPreferenceActivity.class);
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
