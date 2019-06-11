package com.boyitimes.zaozhen.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.R;
import com.boyitimes.zaozhen.http.OkHttpClientRequest;
import com.boyitimes.zaozhen.log.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

public class SetNickNameActivity extends BaseActivity {
    private EditText txt_name;
    private ImageButton btn_back;
    private Intent intent;
    private TextView txt_baocun;
    private String token = "";
    private SharedPreferences tSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(SetNickNameActivity.this, true);
        setContentView(R.layout.activity_setnickname);
        initView();
    }

    private void initView() {
        tSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        token = tSharedPreferences.getString("token", "");
        intent = new Intent();
        intent = getIntent();
        txt_name = findViewById(R.id.txt_name);
        btn_back = findViewById(R.id.btn_back);
        txt_baocun = findViewById(R.id.txt_baocun);
        txt_name.setText(intent.getStringExtra("userName"));
        txt_name.setSelection(intent.getStringExtra("userName").length());//将光标移至文字末尾
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(SetNickNameActivity.this, txt_name);
                finish();
            }
        });
        txt_baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_name.getText().length() > 1 && txt_name.getText().length() < 12) {
                    new HttpTask().execute();
                } else {
                    Toast.makeText(SetNickNameActivity.this, "请输入正确的昵称", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class HttpTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            //         textView.setText("loading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/user/editUserInfo", getSetNickName());
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
//            progressBar.setProgress(progresses[0]);
//            textView.setText("loading..." + progresses[0] + "%");
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Logs.logE("result", "result");
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                String description = jsonObject.getString("msg");
                if ("2000".equals(code)) {
                    BysdApplication.userName = txt_name.getText().toString();
                    Toast.makeText(SetNickNameActivity.this, description, Toast.LENGTH_SHORT).show();
                    hideKeyBoard(SetNickNameActivity.this, txt_name);
                    finish();
                } else {
                    Toast.makeText(SetNickNameActivity.this, description, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }

    private FormBody getSetNickName() {
        FormBody formBody = new FormBody
                .Builder()
                .add("token", token)
                .add("type", "1")
                .add("username", txt_name.getText().toString() + "")
                .build();
        return formBody;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}