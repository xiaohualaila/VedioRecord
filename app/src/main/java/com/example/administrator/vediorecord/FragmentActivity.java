package com.example.administrator.vediorecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.vediorecord.fragment.Fragment2;
import com.example.administrator.vediorecord.fragment.Fragment3;

public class FragmentActivity extends AppCompatActivity {
    private Fragment mCurrentFrag;
    private FragmentManager fm;
    private Fragment f2;
    private Fragment f3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        fm = getSupportFragmentManager();
        f2 = new Fragment2();
        f3 = new Fragment3();
        switchContent(f3);
    }

    /**
     * 动态添加fragment，不会重复创建fragment
     *
     * @param to 将要加载的fragment
     */
    public void switchContent(Fragment to) {
        if (mCurrentFrag != to) {
            if (!to.isAdded()) {// 如果to fragment没有被add则增加一个fragment
                if (mCurrentFrag != null) {
                    fm.beginTransaction().hide(mCurrentFrag).commit();
                }
                fm.beginTransaction()
                        .add(R.id.fl_content, to)
                        .commit();
            } else {
                fm.beginTransaction().hide(mCurrentFrag).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mCurrentFrag = to;
        }
    }
}
