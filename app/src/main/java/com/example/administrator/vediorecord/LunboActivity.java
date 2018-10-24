package com.example.administrator.vediorecord;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.example.administrator.vediorecord.util.FileUtil;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LunboActivity extends AppCompatActivity {

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.ban)
    ImageView ban;

    private List<String> images;

    public  String FILE_MAIN_PICTURE = Environment.getExternalStorageDirectory()+ "/billboard/file/main/picture";

    private int index = 0;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunbo);
        ButterKnife.bind(this);
        images = new ArrayList<>();
        images = FileUtil.getFilePath(FILE_MAIN_PICTURE);

        mHandler.postDelayed(runnable,5000);


    }

    Runnable runnable =  new Runnable() {
        @Override
        public void run()
        {
            if (index == images.size()){
                index = 0;
            }


            Glide.with(LunboActivity.this)
                    .load(images.get(index))
                    .animate(R.anim.slide_in_left)
                    .into(img);

            mHandler.postDelayed(runnable, 5000);
            index++;
        }
    };



}
