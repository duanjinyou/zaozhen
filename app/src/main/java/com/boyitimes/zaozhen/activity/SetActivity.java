package com.boyitimes.zaozhen.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.boyitimes.zaozhen.R;

public class SetActivity extends BaseActivity {
    private ImageButton btn_back;
    private Button btn_logout;
    private SharedPreferences tSharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(SetActivity.this, true);
        setContentView(R.layout.activity_set);
        initView();

    }

    private void initView() {
        tSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("token", "");
                editor.commit();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        token = tSharedPreferences.getString("token", "");
        if ("".equals(token)) {
            btn_logout.setVisibility(View.GONE);

        } else {
            btn_logout.setVisibility(View.VISIBLE);
        }
    }
}
