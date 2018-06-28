package com.app.iostudio.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.app.iostudio.R;
import com.app.iostudio.pref.IOPref;

public class SettingsActivity extends BaseActivity {

    Switch switch_mute;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initBase();
    }

    @Override
    protected void initView() {
        context = this;
        switch_mute = findViewById(R.id.switch_mute);

        boolean isMute =
                IOPref.getInstance().getBoolean(context, IOPref.PreferenceKey.isMute, true);
        switch_mute.setChecked(isMute);

        switch_mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    IOPref.getInstance()
                            .saveBoolean(context, IOPref.PreferenceKey.isMute, isChecked);
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void bindEvent() {

    }
}
