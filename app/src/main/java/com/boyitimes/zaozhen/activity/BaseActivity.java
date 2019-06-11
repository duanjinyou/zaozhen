package com.boyitimes.zaozhen.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.http.OkHttpClientRequest;
import com.boyitimes.zaozhen.utils.LoadingDialogUtils;

/**
 * Created by duanjy on 2018/5/14.
 */

public class BaseActivity extends AppCompatActivity {
    private BysdApplication bysdApplication;
    private BaseActivity oContext;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        if (bysdApplication == null) {
            // 得到Application对象
            bysdApplication = (BysdApplication) getApplication();
        }
        oContext = this;// 把当前的上下文对象赋值给BaseActivity

        addActivity();// 调用添加方法


    }

    // 添加Activity方法
    public void addActivity() {
        bysdApplication.addActivity_(oContext);// 调用myApplication的添加Activity方法
    }

    //销毁当个Activity方法
    public void removeActivity() {
        bysdApplication.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }

    //销毁所有Activity方法
    public void removeALLActivity() {
        bysdApplication.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
    }

    /* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
    public void show_Toast(String text) {
        Toast.makeText(oContext, text, Toast.LENGTH_SHORT).show();
    }

    public void transparentTitle() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }

    public static void showKeyBoard(Context context, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.findFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showDialog() {
        mDialog = LoadingDialogUtils.createLoadingDialog(this, "请稍等...");
    }

    public void closeDialog() {
        LoadingDialogUtils.closeDialog(mDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bysdApplication.removeActivity_(oContext);
        OkHttpClientRequest.get().cancelAll();
        System.gc();
//        Logs.logE("onDestroy","***********************onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Logs.logE("onResume","***********************onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Logs.logE("onPause","***********************onPause");
    }


    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}

