package com.example.administrator.vediorecord;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.vediorecord.util.MyUtil;

import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity3 extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "MainActivity2";
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private Button mBtnPlay;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mIsPlay = false;//是否正在播放录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private ImageView mImageView;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private TextView textView;
    private int text = 0;


    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            textView.setText(text + "");
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);
        requestPermiss();
        mSurfaceview = findViewById(R.id.surfaceview);
//        mImageView = findViewById(R.id.imageview);
        mBtnStartStop = findViewById(R.id.btnStartStop);//录制或停止
        mBtnPlay = findViewById(R.id.btnPlayVideo);
        textView = findViewById(R.id.text);
//        mBtnStartStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

//        mBtnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {//播放视屏
//
//            }
//        });

        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mStartedFlg) {
//            mImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 录视频
     *
     * @param view
     */
    public void btnStartStop(View view) {
        if (mIsPlay) {//判断是否正在播放
            if (mediaPlayer != null) {
                mIsPlay = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (!mStartedFlg) {//判断是否在录像
            handler.postDelayed(runnable, 1000);
//                    mImageView.setVisibility(View.GONE);

            ////录像代码
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
            }

            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//设置打开前后摄像头
            //  camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//设置打开前后摄像头
            if (camera != null) {
                camera.setDisplayOrientation(90);
                camera.unlock();
                mRecorder.setCamera(camera);
            }

            try {
                // 这两项需要放在setOutputFormat之前
                mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                // Set output file format//设置文件输出格式
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                // 这两项需要放在setOutputFormat之后
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码方式
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//设置视频编码方式

                mRecorder.setVideoSize(640, 480);//设置要拍摄的宽度和视频的高度。
                mRecorder.setVideoFrameRate(30);//设置录制视频的捕获帧速率。
                mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);//设置所录制视频的编码位率。
                mRecorder.setOrientationHint(90);//设置输出的视频播放的方向提示。
                //设置记录会话的最大持续时间（毫秒）
                mRecorder.setMaxDuration(30 * 1000);
                mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());//设置使用哪个SurfaceView来显示视频预览。

                path = MyUtil.getSDPath();
                if (path != null) {
                    File dir = new File(path + "/recordtest");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    path = dir + "/" + MyUtil.getDate() + ".mp4";
                    mRecorder.setOutputFile(path);//设置录制的音频文件的保存位置。
                    mRecorder.prepare();
                    mRecorder.start();
                    mStartedFlg = true;
                    mBtnStartStop.setText("Stop");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//停止录像
            //stop
            if (mStartedFlg) {
                try {
                    handler.removeCallbacks(runnable);
                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    mBtnStartStop.setText("Start");
                    if (camera != null) {
                        camera.release();
                        camera = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mStartedFlg = false;
        }
    }

    /**
     * 播放视频
     *
     * @param view
     */
    public void btnPlayVideo(View view) {

        if (mStartedFlg) {
            return;
        }
        mIsPlay = true;
//                mImageView.setVisibility(View.GONE);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        Uri uri = Uri.parse(path);
        mediaPlayer = MediaPlayer.create(MainActivity3.this, uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }


    private static final int PERMISSIONS_REQUEST = 1;

    /**
     * 请求权限
     */
    private void requestPermiss() {
        PermissionGen.with(this)
                .addRequestCode(PERMISSIONS_REQUEST)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
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
