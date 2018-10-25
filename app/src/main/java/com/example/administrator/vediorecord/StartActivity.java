package com.example.administrator.vediorecord;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class StartActivity  extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    private static final int PERMISSIONS_REQUEST = 1;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        requestPermiss();
    }

    /**
     * 请求权限
     */
    private void requestPermiss() {
        PermissionGen.with(this)
                .addRequestCode(PERMISSIONS_REQUEST)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoSuccess() {
        Log.e(TAG, "requestPhotoSuccess: ");
        //成功之后的处理
        handler.postDelayed(() -> startActivity(new Intent(StartActivity.this,MainActivity4.class)),3000);

    }

    @PermissionFail(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoFail() {
        Log.e(TAG, "requestPhotoFail: ");
        //失败之后的处理，我一般是跳到设置界面
        showMissingPermissionDialog();
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消", (dialog, which) -> finish());

        builder.setPositiveButton("设置", (dialog, which) -> startAppSettings());

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
