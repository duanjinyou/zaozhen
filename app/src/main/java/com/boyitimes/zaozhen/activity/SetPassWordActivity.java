package  com.boyitimes.zaozhen.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boyitimes.zaozhen.R;
import com.boyitimes.zaozhen.http.OkHttpClientRequest;
import com.boyitimes.zaozhen.log.Logs;
import com.boyitimes.zaozhen.utils.LoadingDialogUtils;
import com.boyitimes.zaozhen.utils.MD5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

public class SetPassWordActivity extends BaseActivity {
    private Dialog mDialog;
    private Intent intent;
    private String phoneNum;
    private String token;
    private TextView txt_shoujihao;
    private Button btn_tiao;
    private EditText edt_mima, edt_mimas;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(SetPassWordActivity.this, true);
        setContentView(R.layout.activity_password);
        intent = new Intent();
        intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        token = intent.getStringExtra("token");
        initView();
    }

    private void initView() {
        txt_shoujihao = (TextView) findViewById(R.id.txt_shoujihao);
        edt_mima = (EditText) findViewById(R.id.edt_mima);
        edt_mimas = (EditText) findViewById(R.id.edt_mimas);
        btn_tiao = (Button) findViewById(R.id.btn_tiao);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        txt_shoujihao.setText("您的手机号:  " + phoneNum);
        btn_tiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(edt_mima.getText().toString()) && edt_mima.getText().toString().length() > 6) {
                    if (!edt_mima.getText().toString().equals(edt_mimas.getText().toString())) {
                        Toast.makeText(SetPassWordActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
                    } else {
                        LoadingDialogUtils.closeDialog(mDialog);
                        new HttpTask().execute();
                    }
                } else {
                    Toast.makeText(SetPassWordActivity.this, "请输入正确密码", Toast.LENGTH_LONG).show();
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
            result = OkHttpClientRequest.get().SentRequest("dongzhongapi.boyitimes.com/api/reg/passreg", getSetPassWord());
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

            try {
                LoadingDialogUtils.closeDialog(mDialog);
                Logs.logE("result", "result");
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                String description = jsonObject.getString("msg");
                if ("2000".equals(code)) {
                    Toast.makeText(SetPassWordActivity.this, description, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SetPassWordActivity.this, description, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                LoadingDialogUtils.closeDialog(mDialog);
            }


        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }

    private FormBody getSetPassWord() {
        FormBody formBody = new FormBody
                .Builder()
                .add("userphone", phoneNum)
                .add("password", MD5Utils.encode(edt_mima.getText().toString()))
                .add("type", "2")
                .build();
        return formBody;
    }
}