package com.boyitimes.zaozhen.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.R;
import com.boyitimes.zaozhen.http.OkHttpClientRequest;
import com.boyitimes.zaozhen.log.Logs;
import com.boyitimes.zaozhen.utils.LoadingDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;

public class NewNumActivity extends BaseActivity {
    private ImageButton btn_back;
    private Intent intent;
    private String phoneNum;
    private TextView txt_phone;
    private String type = "2";
    private Dialog mDialog;
    private String method;
    private Button btn_login;
    private TextView txt_huoquyanzhengma;
    private EditText edit_yanzhengma, edt_shoujihao;
    private String token;
    private SharedPreferences tSharedPreferences;
    private int recLen = 60;
    private Timer timer;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    txt_huoquyanzhengma.setText(recLen + "s后重新发送");
                    if (recLen == 0) {
                        txt_huoquyanzhengma.setClickable(true);
                        txt_huoquyanzhengma.setText("发送验证码");

                        timer.cancel();
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(NewNumActivity.this, true);
        setContentView(R.layout.activity_newphonenum);
        init();
    }

    private void init() {
        tSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        token = tSharedPreferences.getString("token", "");
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_yanzhengma = findViewById(R.id.edt_yanzhengma);
        edt_shoujihao = findViewById(R.id.edt_shoujihao);
        txt_huoquyanzhengma = findViewById(R.id.txt_huoquyanzhengma);
        txt_huoquyanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_shoujihao.getText().length() == 11) {
                    method = "verification";
                    new HttpTask().execute("verification");
                } else {
                    Toast.makeText(NewNumActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "login";
                new HttpTask().execute("login");
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
            switch (strings[0]) {
                case "login":
                    result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/reg/editPhone", getDenglu());
                    break;
                case "verification":
                    result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/reg/SendPhoneSms", getYanzhengma());
                    break;

            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

//            progressBar.setProgress(progresses[0]);
//            textView.setText("loading..." + progresses[0] + "%");
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            switch (method) {
                case "login":
                    try {
                        LoadingDialogUtils.closeDialog(mDialog);
                        Logs.logE("result", "result");
                        JSONObject jsonObject = null;
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.getString("code");
                        String description = jsonObject.getString("msg");
                        if ("2000".equals(code)) {
                            Toast.makeText(NewNumActivity.this, description, Toast.LENGTH_SHORT).show();
                            hideKeyBoard(NewNumActivity.this, edt_shoujihao);
                            BysdApplication.newPhoneNum = edt_shoujihao.getText().toString();
                            finish();
                        } else {
                            Toast.makeText(NewNumActivity.this, description, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LoadingDialogUtils.closeDialog(mDialog);
                    }
                    break;
                case "verification":
                    try {
                        LoadingDialogUtils.closeDialog(mDialog);
                        Logs.logE("result", "result");
                        JSONObject jsonObject = null;
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.getString("code");
                        String description = jsonObject.getString("msg");
                        if ("2000".equals(code)) {
                            txt_huoquyanzhengma.setClickable(false);
                            showtime();
                            Toast.makeText(NewNumActivity.this, description, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NewNumActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LoadingDialogUtils.closeDialog(mDialog);
                    }
                    break;

            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }

    private FormBody getYanzhengma() {
        FormBody formBody = new FormBody
                .Builder()
                .add("userphone", edt_shoujihao.getText().toString())
                .add("type", "6")
                .build();
        return formBody;
    }

    private FormBody getDenglu() {
        String password = edit_yanzhengma.getText().toString();
        FormBody formBody = new FormBody
                .Builder()
                .add("token", token)
                .add("userphone", edt_shoujihao.getText().toString())
                .add("smsno", password)

                .build();
        return formBody;
    }

    private void showtime() {
        cancelTimer();
        recLen = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                recLen--;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 1000, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
