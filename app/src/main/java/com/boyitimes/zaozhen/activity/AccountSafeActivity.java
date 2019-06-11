package com.boyitimes.zaozhen.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.R;

public class AccountSafeActivity extends BaseActivity {
    private Intent intent;
    private String phoneNum;
    private boolean password;
    private TextView txt_phoneNum, txt_shezhimima;
    private RelativeLayout rel_shezhimima, rel_shoujihaobangding;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(AccountSafeActivity.this, true);
        setContentView(R.layout.activity_safe);
        intent = new Intent();
        intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        password = intent.getBooleanExtra("password", false);
        BysdApplication.isXiuGai = password;
        initView();
    }

    private void initView() {
        btn_back = findViewById(R.id.btn_back);
        rel_shoujihaobangding = findViewById(R.id.rel_shoujihaobangding);
        txt_phoneNum = findViewById(R.id.txt_phoneNum);
        txt_phoneNum.setText(phoneNum);
        txt_shezhimima = findViewById(R.id.txt_shezhimima);
        rel_shezhimima = findViewById(R.id.rel_shezhimima);
        if (password) {
            txt_shezhimima.setText("修改密码");
        } else {
            txt_shezhimima.setText("设置密码");
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rel_shezhimima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password) {

                    Intent intent = new Intent();
                    intent.setClass(AccountSafeActivity.this, XiuGaiPhoneNumActivity.class);
                    intent.putExtra("phoneNum", phoneNum);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(AccountSafeActivity.this, SafePassWordActivity.class);
                    intent.putExtra("phoneNum", phoneNum);
                    startActivity(intent);
                }

            }
        });
        rel_shoujihaobangding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AccountSafeActivity.this, PhoneNumBindingActivity.class);
                intent.putExtra("phoneNum", phoneNum);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if ("".equals(BysdApplication.newPhoneNum)) {
            txt_phoneNum.setText(phoneNum);
        }
        if (!"".equals(BysdApplication.newPhoneNum)) {
            phoneNum = BysdApplication.newPhoneNum;
            txt_phoneNum.setText(phoneNum);
        }
        if (BysdApplication.isXiuGai) {
            txt_shezhimima.setText("修改密码");
            rel_shezhimima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(AccountSafeActivity.this, XiuGaiPhoneNumActivity.class);
                    intent.putExtra("phoneNum", phoneNum);
                    startActivity(intent);
                }
            });
        }
        if (!BysdApplication.isXiuGai) {
            txt_shezhimima.setText("设置密码");
            rel_shezhimima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(AccountSafeActivity.this, SafePassWordActivity.class);
                    intent.putExtra("phoneNum", phoneNum);
                    startActivity(intent);
                }
            });
        }
    }
}

