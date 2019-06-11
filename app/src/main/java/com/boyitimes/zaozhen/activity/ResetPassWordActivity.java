package com.boyitimes.zaozhen.activity;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class ResetPassWordActivity extends BaseActivity {
    private EditText edt_shoujihao, edt_yanzhengma, edt_mima, edt_mimas;
    private String type = "2";
    private Dialog mDialog;
    private String method;
    private Button btn_login;
    private ImageButton img_back;
    private TextView txt_huoquyanzhengma;
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
        setAndroidNativeLightStatusBar(ResetPassWordActivity.this, true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_resetpassword);
        initView();

    }

    private void initView() {
        edt_shoujihao = findViewById(R.id.edt_shoujihao);
        edt_yanzhengma = findViewById(R.id.edt_yanzhengma);
        edt_mima = findViewById(R.id.edt_mima);
        edt_mimas = findViewById(R.id.edt_mimas);
        btn_login = (Button) findViewById(R.id.btn_login);
        img_back = findViewById(R.id.img_back);
        txt_huoquyanzhengma = (TextView) findViewById(R.id.txt_huoquyanzhengma);
        txt_huoquyanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(edt_shoujihao.getText().toString()) && edt_shoujihao.getText().toString().length() == 11) {
                    mDialog = LoadingDialogUtils.createLoadingDialog(ResetPassWordActivity.this, "请稍等...");
                    method = "verification";
                    new HttpTask().execute("verification");
                } else {
                    Toast.makeText(ResetPassWordActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, SetPassWordActivity.class);
//                startActivity(intent);
                if (!"".equals(edt_shoujihao.getText().toString()) && edt_shoujihao.getText().toString().length() == 11) {
                    if (!"".equals(edt_yanzhengma.getText().toString()) && edt_yanzhengma.getText().toString().length() == 6) {
                        if (!"".equals(edt_mima.getText().toString()) && edt_mima.getText().toString().length() > 6) {
                            if (!edt_mima.getText().toString().equals(edt_mimas.getText().toString())) {
                                Toast.makeText(ResetPassWordActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
                            } else {
                                mDialog = LoadingDialogUtils.createLoadingDialog(ResetPassWordActivity.this, "请稍等...");
                                method = "login";
                                HttpTask loginTask = new HttpTask();
                                loginTask.execute("login");
                            }
                        } else {
                            Toast.makeText(ResetPassWordActivity.this, "请输入正确密码", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(ResetPassWordActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ResetPassWordActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                }

            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/reg/passreg", getDenglu());
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
                            Toast.makeText(ResetPassWordActivity.this, description, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetPassWordActivity.this, description, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ResetPassWordActivity.this, description, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPassWordActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
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
                .add("userphone", edt_shoujihao.getText().toString())
                .add("type", "3")
                .build();
        return formBody;
    }

    private FormBody getDenglu() {
        FormBody formBody = new FormBody
                .Builder()
                .add("userphone", edt_shoujihao.getText().toString())
                .add("smsno", edt_yanzhengma.getText().toString())
                .add("password", MD5Utils.encode(edt_mimas.getText().toString()))
                .add("type", "1")
                .build();
        return formBody;
    }
}