package com.example.wschat.ui;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.wschat.App;
import com.example.wschat.R;


public class SettingsActivity extends AppCompatActivity {
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        String signature = PreferenceManager.getDefaultSharedPreferences(this).getString("signature", "");
        String http = PreferenceManager.getDefaultSharedPreferences(this).getString("http", "");
        if (!TextUtils.isEmpty(signature)) {
            App.Companion.setWsServer(signature);
        }
        if (!TextUtils.isEmpty(http)) {
            App.Companion.setHttpServer(http);
        }
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.setting_preference, rootKey);
        }
    }
}