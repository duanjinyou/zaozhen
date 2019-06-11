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
import com.boyitimes.zaozhen.utils.MD5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;

public class LoginActivity extends BaseActivity {
    private String type = "2";
    private Dialog mDialog;
    private String method;
    private TextView txt_zhuce, txt_wangji;
    private Button btn_login;
    private TextView txt_duanxindl, txt_mimadl;
    private TextView txt_huoquyanzhengma;
    private EditText edit_honenum, edit_yanzhengma;
    private int recLen = 60;
    private ImageButton btn_back;
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
        setAndroidNativeLightStatusBar(LoginActivity.this, true);
        setContentView(R.layout.activity_login);
        initView();

    }

    private void initView() {
        txt_zhuce = (TextView) findViewById(R.id.txt_zhuce);
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_huoquyanzhengma = (TextView) findViewById(R.id.txt_huoquyanzhengma);
        txt_duanxindl = (TextView) findViewById(R.id.txt_duanxindl);
        txt_mimadl = (TextView) findViewById(R.id.txt_mimadl);
        edit_honenum = (EditText) findViewById(R.id.edit_honenum);
        edit_yanzhengma = (EditText) findViewById(R.id.edit_yanzhengma);
        txt_wangji = (TextView) findViewById(R.id.txt_wangji);
        txt_zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, SetPassWordActivity.class);
//                startActivity(intent);
                if (!"".equals(edit_honenum.getText().toString()) && edit_honenum.getText().toString().length() == 11) {
                    if (!"".equals(edit_yanzhengma.getText().toString()) && edit_yanzhengma.getText().toString().length() == 6 && type.equals("2")) {
                        mDialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this, "请稍等...");
                        method = "login";
                        HttpTask loginTask = new HttpTask();
                        loginTask.execute("login");
                    } else if (type.equals("1")) {
                        mDialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this, "请稍等...");
                        method = "login";
                        HttpTask loginTask = new HttpTask();
                        loginTask.execute("login");
                    } else {
                        Toast.makeText(LoginActivity.this, "请输入六位验证码", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                }

            }
        });

        txt_duanxindl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_huoquyanzhengma.setVisibility(View.VISIBLE);
                txt_duanxindl.setTextColor(getResources().getColor(R.color.black));
                txt_mimadl.setTextColor(getResources().getColor(R.color.gray2));
                edit_honenum.setText("");
                edit_yanzhengma.setText("");
                edit_yanzhengma.setHint("请输入验证码");
                type = "2";
                txt_wangji.setVisibility(View.GONE);
                edit_yanzhengma.setInputType(128);
            }
        });
        txt_mimadl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_huoquyanzhengma.setVisibility(View.GONE);
                txt_duanxindl.setTextColor(getResources().getColor(R.color.gray2));
                txt_mimadl.setTextColor(getResources().getColor(R.color.black));
                edit_honenum.setText("");
                edit_yanzhengma.setText("");
                edit_yanzhengma.setHint("请输入密码");
                type = "1";
                edit_yanzhengma.setInputType(129);
                txt_wangji.setVisibility(View.VISIBLE);
                txt_wangji.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, ResetPassWordActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        txt_huoquyanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(edit_honenum.getText().toString()) && edit_honenum.getText().toString().length() == 11) {
                    mDialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this, "请稍等...");
                    method = "verification";
                    new HttpTask().execute("verification");
                } else {
                    Toast.makeText(LoginActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
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
            switch (strings[0]) {
                case "login":
                    result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/login/index", getDenglu());
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
                            BysdApplication.isUpdate = true;
                            Toast.makeText(LoginActivity.this, description, Toast.LENGTH_SHORT).show();
                            JSONObject json = jsonObject.getJSONObject("data");
                            boolean isHavePassWord = json.getBoolean("password");
                            String token = json.getString("token");
                            SharedPreferences mSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putString("phoneNum", edit_honenum.getText().toString());
                            editor.commit();
                            if (isHavePassWord) {
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, SetPassWordActivity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("phoneNum", edit_honenum.getText().toString());
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, description, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, description, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
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

    private FormBody getYanzhengma() {
        FormBody formBody = new FormBody
                .Builder()
                .add("userphone", edit_honenum.getText().toString())
                .add("type", "2")
                .build();
        return formBody;
    }

    private FormBody getDenglu() {
        String password = edit_yanzhengma.getText().toString();
        Logs.logE("type", type);
        if ("1".equals(type)) {
            password = MD5Utils.encode(password);
        }
        Logs.logE("password", password);
        FormBody formBody = new FormBody
                .Builder()
                .add("userphone", edit_honenum.getText().toString())

                .add("password", password)

                .add("type", type)
                .build();
        return formBody;
    }
}
