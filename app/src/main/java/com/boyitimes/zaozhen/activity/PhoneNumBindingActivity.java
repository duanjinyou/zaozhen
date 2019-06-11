package com.boyitimes.zaozhen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.R;

public class PhoneNumBindingActivity extends BaseActivity {
    private TextView txt_phone;
    private Intent intent;
    private TextView txt_xiugai;
    private String phoneNum = "";
    private ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(PhoneNumBindingActivity.this, true);
        setContentView(R.layout.activity_phonebinding);
        initView();

    }

    private void initView() {
        intent = new Intent();
        intent = getIntent();
        btn_back = findViewById(R.id.btn_back);
        txt_phone = findViewById(R.id.txt_phone);
        txt_xiugai = findViewById(R.id.txt_xiugai);
        phoneNum = intent.getStringExtra("phoneNum");
        txt_phone.setText("绑定的手机号: " + phoneNum);
        txt_xiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhoneNumBindingActivity.this, GengHuanActivity.class);
                intent.putExtra("phoneNum", phoneNum);
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("".equals(BysdApplication.newPhoneNum)) {
            txt_phone.setText("绑定的手机号: " + phoneNum);
        } else {
            phoneNum = BysdApplication.newPhoneNum;
            txt_phone.setText("绑定的手机号: " +phoneNum);
        }
    }
}
