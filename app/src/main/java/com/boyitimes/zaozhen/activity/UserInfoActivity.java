package com.boyitimes.zaozhen.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyitimes.zaozhen.BysdApplication;
import com.boyitimes.zaozhen.R;
import com.boyitimes.zaozhen.http.OkHttpClientRequest;
import com.boyitimes.zaozhen.log.Logs;
import com.boyitimes.zaozhen.utils.PermissionsSetting;
import com.boyitimes.zaozhen.view.PopTakePhotoView;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserInfoActivity extends BaseActivity {
    private Intent intent;
    private String userName;
    private TextView txt_name;
    private RelativeLayout rel_touxinag;
    private String ImageName;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    private static final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    private ImageView img_avatar;
    private String token;
    private SharedPreferences tSharedPreferences;
    private ImageButton img_back;
    private String avatarUrl = "";
    private RelativeLayout rel_nicheng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidNativeLightStatusBar(UserInfoActivity.this, true);
        setContentView(R.layout.activity_userinfo);
        initView();
    }

    private void initView() {
        tSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        token = tSharedPreferences.getString("token", "");
        intent = new Intent();
        intent = getIntent();
        rel_nicheng = findViewById(R.id.rel_nicheng);
        userName = intent.getStringExtra("userName");
        avatarUrl = intent.getStringExtra("avatarUrl");
        txt_name = findViewById(R.id.txt_name);
        rel_touxinag = findViewById(R.id.rel_touxinag);
        img_avatar = findViewById(R.id.img_avatar);
        img_back = findViewById(R.id.btn_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_name.setText(userName);
        rel_touxinag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PopTakePhotoView(UserInfoActivity.this, UserInfoActivity.this).showMenuWindow(new PopTakePhotoView.PopwindowInterface() {
                    @Override
                    public void conchoose() {
                        intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                IMAGE_UNSPECIFIED);
                        // 调用剪切功能
                        startActivityForResult(intent, PHOTOZOOM);
                    }

                    @Override
                    public void conpaizhao() {
                        if (PermissionsSetting.isGrantExternalRW(UserInfoActivity.this)) {

                        }
                        if (!PermissionsSetting.isGrantExternalRW(UserInfoActivity.this)) {
                            Toast.makeText(UserInfoActivity.this, "请授权读取文件权限！", Toast.LENGTH_LONG).show();
                            getCamera();
                        }
                        if (new PermissionsSetting(UserInfoActivity.this).checkper()) {
                            ImageName = "/" + getStringToday() + ".jpg";
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File tempFile = new File(Environment.getExternalStorageDirectory(), ImageName);
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= 24) {
                                uri = FileProvider.getUriForFile(UserInfoActivity.this, "DEV", tempFile);
                                Logs.logE("242424242424242424", "242424242424242424");
                            } else {
                                uri = Uri.fromFile(tempFile);
                            }
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Uri.fromFile(tempFile)
                            startActivityForResult(intent, PHOTOHRAPH);
                        } else {
                            getCamera();
                        }
                    }

                    @Override
                    public void conquxiao() {

                    }
                });
            }
        });
        Glide.with(UserInfoActivity.this)
                .load(avatarUrl)
                .error(R.mipmap.img_touxiang)
                .into(img_avatar);
        rel_nicheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserInfoActivity.this, SetNickNameActivity.class);
                intent.putExtra("userName", txt_name.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void getCamera() {
        getPersimmions();
        if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserInfoActivity.this,
                    Manifest.permission.CAMERA)) {
                // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                // 弹窗需要解释为何需要该权限，再次请求授权
                Toast.makeText(UserInfoActivity.this, "请授权开启摄像头！", Toast.LENGTH_LONG).show();

                // 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", UserInfoActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            } else {
                // 不需要解释为何需要该权限，直接请求授权
                Toast.makeText(UserInfoActivity.this, "请授权开启摄像头！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", UserInfoActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        } else {
            // 已经获得授权，可以打电话
            Toast.makeText(UserInfoActivity.this, "请授权开启摄像头！", Toast.LENGTH_LONG).show();
            // 帮跳转到该应用的设置界面，让用户手动授权
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", UserInfoActivity.this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            return;
        }


    }

    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @TargetApi(23)
    protected void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // 读写权限
            if (addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
//            // 麦克风权限
//            if (addPermission(permissions, android.Manifest.permission.RECORD_AUDIO)) {
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
//            }
            if (addPermission(permissions, Manifest.permission.CAMERA)) {
                permissionInfo += "Manifest.permission.CAMERA Deny \n";
            }
//            if (addPermission(permissions, android.Manifest.permission.READ_PHONE_STATE)) {
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
//            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTOHRAPH) {
            // 设置文件保存路径这里放在跟目录下
            File picture = new File(Environment.getExternalStorageDirectory()
                    + "/" + ImageName);
            if (!picture.exists()) {
                Logs.logE("picture", "不存在");
                return;
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Logs.logE("picture", "picture" + picture);
                startPhotoZoomT(picture);
            } else {
                Logs.logE("picture", "pictureelse" + picture);
                startPhotoZoom(Uri.fromFile(picture));
            }
        }
        if (data == null)
            return;

        // 读取相册缩放图片
        if (requestCode == PHOTOZOOM) {
            Logs.logE("picture", "requestCode");
            File file = new File(getPhotoPathByUri(data));
            Logs.logE("file", file.toString());
            startPhotoZoomT(file);
        }
        // 处理结果
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/avater.jpg");
                Bitmap photo = extras.getParcelable("data");
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    Logs.logE("file", file.toString());
                    HttpTask httpTask = new HttpTask();
                    httpTask.execute(file.toString());
                    img_avatar.setImageBitmap(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID + ".provider", tempFile));
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }

    public void startPhotoZoomT(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID + ".provider", tempFile));
        intent.setDataAndType(getImageContentUri(UserInfoActivity.this, file), IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private class HttpTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            //         textView.setText("loading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            result = OkHttpClientRequest.get().uploadfile("http://dongzhongapi.boyitimes.com/api/user/editUserInfo", token, strings[0]);
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
                Logs.logE("result", result);
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                String description = jsonObject.getString("msg");
                String data = jsonObject.getString("data");
                if ("2000".equals(code)) {
                    BysdApplication.isUpdate = true;
                    Glide.with(UserInfoActivity.this)
                            .load(new JSONObject(data).getString("userimg"))
                            .error(R.mipmap.img_touxiang)
                            .into(img_avatar);
                    Toast.makeText(UserInfoActivity.this, description, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserInfoActivity.this, description, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        txt_name.setText(BysdApplication.userName);
    }

    private String getPhotoPathByUri(Intent data) {
        Uri sourceUri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = UserInfoActivity.this.managedQuery(sourceUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(columnIndex);
        return imgPath;
    }

}
