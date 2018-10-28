package com.example.administrator.vediorecord;

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.vediorecord.util.MyUtil;

import com.iceteck.silicompressorr.VideoCompress;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
//https://www.jb51.net/article/141101.htm
// https://www.jb51.net/article/106594.htm

//https://blog.csdn.net/qq_36421691/article/details/79113392
public class MainActivity4 extends AppCompatActivity implements SurfaceHolder.Callback ,View.OnClickListener{

    private static final String TAG = "MainActivity4";
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private Button mBtnPlay;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mIsPlay = false;//是否正在播放录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private String path2;
    private TextView textView;
    private int text = 0;
    private Unbinder unbinder;


    private long startTime;
    private long endTime;
    private Handler handler = new Handler();
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
        setContentView(R.layout.activity_main_4);
        unbinder = ButterKnife.bind(this);
        mSurfaceview = findViewById(R.id.surfaceview);

        mBtnStartStop = findViewById(R.id.btnStartStop);//录制或停止
        mBtnPlay = findViewById(R.id.btnPlayVideo);
        textView = findViewById(R.id.text);

        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startRecord();
//                handler.postDelayed(runnable, 1000);
//            }
//        },1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

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


    /**
     * 录制视频
     */
    public void startRecord(){
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.reset();
    //   camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//设置打开前后摄像头
         camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (camera != null) {
            camera.setDisplayOrientation(90);
            camera.unlock();
            mRecorder.setCamera(camera);
        }

        try {
            // 这两项需要放在setOutputFormat之前
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.AudioSource.MIC);

            // Set output file format//设置文件输出格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 这两项需要放在setOutputFormat之后
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码方式
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//设置视频编码方式

            mRecorder.setVideoSize(640, 480);//设置要拍摄的宽度和视频的高度。
            mRecorder.setVideoFrameRate(30);//设置录制视频的捕获帧速率。
            mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);//设置所录制视频的编码位率。
            mRecorder.setOrientationHint(270);//设置输出的视频播放的方向提示。
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
                mBtnStartStop.setText("停止");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @OnClick({R.id.btnStartStop,R.id.btnPlayVideo})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStartStop:
                text = 0;
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
                    startRecord();
                } else {//停止录像
                    //stop
                    if (mStartedFlg) {
                        try {

                            handler.removeCallbacks(runnable);
                            if(mRecorder != null){
                                mRecorder.stop();
                                mRecorder.reset();
                                mRecorder.release();
                                mRecorder = null;
                            }
                            mBtnStartStop.setText("开始");
                            if (camera != null) {
                                camera.release();
                                camera = null;
                            }

                            //压缩视频
                            compressVideo();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false;
                }
                break;
            case R.id.btnPlayVideo:
                if (mStartedFlg) {
                    return;
                }
                mIsPlay = true;
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.reset();
                Uri uri = Uri.parse(path);
                mediaPlayer = MediaPlayer.create(MainActivity4.this, uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(mSurfaceHolder);
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void compressVideo() {
        path2 = MyUtil.getSDPath();
        if (path2 != null) {
            File dir = new File(path2 + "/recordtest");
            if (!dir.exists()) {
                dir.mkdir();
            }
            path2 = dir + "/" + MyUtil.getDate() + "sy.mp4";

            VideoCompress.compressVideoLow(path, path2, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    startTime = System.currentTimeMillis();

                    Log.i(TAG, "开始时间" + startTime);

                }

                @Override
                public void onSuccess() {
                    endTime = System.currentTimeMillis();

                    Log.i(TAG, "结束时间 = " + endTime);
                    Log.i(TAG, "压缩后大小 = " + getFileSize(path2));
                }

                @Override
                public void onFail() {
                    endTime = System.currentTimeMillis();

                    Log.i(TAG, "失败时间 = " + endTime);
                }

                @Override
                public void onProgress(float percent) {
                    Log.i(TAG, String.valueOf(percent) + "%");
                }

            });
        }
    }

        private String getFileSize(String path) {
            File f = new File(path);
            if (!f.exists()) {
                return "0 MB";
            } else {
                long size = f.length();
                return (size / 1024f) / 1024f + "MB";
            }
        }
}
